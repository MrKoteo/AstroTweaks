package astrotweaks.block.black_hole;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.*;



public class BlackHoleRegionManager {

    /** Total block reads allowed per tick for this black hole. */
    public static final int BUDGET_PER_TICK = 2048;

    /**
     * Safety full-rescan delay after a mass change (5 minutes = 6000 ticks).
     * Catches blocks/liquids missed by the first pass. At most one pending
     * request per black hole; the earliest wins, later changes are ignored
     * until it fires.
     */
    public static final long RESCAN_DELAY_TICKS = 3600L; // 3 минуты
    /**
     * Prompt revisit throttle: an immediate EMPTY-region recheck fires only
     * once the mass has grown by this much since the last prompt revisit...
     */
    public static final double PROMPT_RESCAN_MIN_DELTA = 1000.0D;
    /** ...and no more often than this (ticks), so dense feasts can't thrash the budget. */
    public static final long PROMPT_RESCAN_MIN_INTERVAL = 200L;

    private final BlackHoleTileEntity te;
    private final Map<Long, BlackHoleRegion> allRegions = new HashMap<>();
    private final List<BlackHoleRegion> active = new ArrayList<>();
    private final PriorityQueue<BlackHoleRegion> waiting = new PriorityQueue<>(Comparator.comparingDouble(r -> r.wakeMass));

    private double massAtLastWake = 0;
    private boolean seeded = false;

    // Throttle for the waiting-queue frontier pass (see tick()).
    private long lastFrontierTick = -1000L;

    // --- Delayed safety rescan state (transient: fresh seed scan covers reloads) ---
    private long rescanDueTick = -1L; // -1 = none scheduled
    private double lastSeenMass = Double.NaN;

    // --- Prompt revisit state: high-water mark of mass at last prompt recheck ---
    private double massAtLastPrompt = Double.NaN;
    private long lastPromptTick = 0L;

    public BlackHoleRegionManager(BlackHoleTileEntity te) {
        this.te = te;
        this.massAtLastWake = te.getMass();
    }

    // =================================================================
    // Active/waiting membership — O(1) via inActive/inWaiting flags.
    // All mutations of `active`/`waiting` must go through these helpers.
    // =================================================================
    private void addActive(BlackHoleRegion r) {
        if (!r.inActive) {
            r.inActive = true;
            active.add(r);
        }
    }

    /** Ordered remove at index: preserves BFS (center-out) processing order. */
    private void removeActiveAt(int idx) {
        BlackHoleRegion r = active.remove(idx);
        r.inActive = false;
    }

    /** Ordered remove by reference with index hint (falls back to search, never taken). */
    private void removeActive(int idx, BlackHoleRegion r) {
        if (idx >= 0 && idx < active.size() && active.get(idx) == r) {
            removeActiveAt(idx);
        } else {
            // Reentrant event path disturbed the list — stay correct, pay O(n) once.
            if (active.remove(r)) r.inActive = false;
        }
    }

    private void addWaiting(BlackHoleRegion r) {
        if (r.inWaiting) waiting.remove(r); // drop stale entry, at most one exists
        waiting.add(r);
        r.inWaiting = true;
    }
    private void removeWaiting(BlackHoleRegion r) {
        if (r.inWaiting) {
            waiting.remove(r);
            r.inWaiting = false;
        }
    }

    // =================================================================
    // Main tick — called from BlackHoleTileEntity.update() every tick.
    // =================================================================
    public void tick() {
        World world = te.getWorld();
        if (world == null || world.isRemote) return;

        if (!seeded) { seeded = true; seed(); }

        double mass = te.getMass();
        long now = world.getTotalWorldTime();

        // --- Delayed safety rescan: a mass INCREASE schedules one full
        // recheck in RESCAN_DELAY_TICKS, but only if none is pending
        // (earliest request wins). If the rescan eats new blocks, the mass
        // change it causes schedules the next one; if mass stays flat
        // (everything edible already eaten), nothing is scheduled and the
        // hole stays idle. Decreases (evaporation, BH tug losses, /blockdata
        // down) schedule nothing: they can't make anything newly edible.
        // O(1) per tick.
        if (Double.isNaN(lastSeenMass)) {
            lastSeenMass = mass; // first tick after load: no spurious schedule
        } else if (mass > lastSeenMass) {
            lastSeenMass = mass;
            if (rescanDueTick < 0) rescanDueTick = now + RESCAN_DELAY_TICKS;
        } else if (mass < lastSeenMass) {
            lastSeenMass = mass;
        }
        if (rescanDueTick >= 0 && now >= rescanDueTick) {
            rescanDueTick = -1L;
            fullRescan();
        }

        // --- Prompt revisit on significant mass growth: EMPTY regions (settled
        // craters, event-less arrivals like fallen sand) are invisible to the
        // wake queue, so without this a /blockdata jump would sit idle until
        // the 5-minute safety rescan. WAITING regions need nothing extra: the
        // wake loop below already releases them the same tick.
        // Throttled by mass delta AND time so dense feasts can't thrash the budget.
        if (Double.isNaN(massAtLastPrompt)) {
            massAtLastPrompt = mass;
            lastPromptTick = now;
        } else if (mass < massAtLastPrompt) {
            massAtLastPrompt = mass; // decrease: just lower the high-water mark
        } else if (mass - massAtLastPrompt >= PROMPT_RESCAN_MIN_DELTA
                && now - lastPromptTick >= PROMPT_RESCAN_MIN_INTERVAL) {
            massAtLastPrompt = mass;
            lastPromptTick = now;
            promptRescan(mass);
        }

        // Wake deferred regions as soon as mass reaches their wakeMass.
        // No delta-threshold: guarantees EMPTY=only air/unbreakable and liquids
        // are rechecked immediately when BH grows enough to pull them (accel>0.08).
        // PriorityQueue peek is O(1), so per-tick cost is negligible.
        while (!waiting.isEmpty() && waiting.peek().wakeMass <= mass) {
            BlackHoleRegion r = waiting.poll();
            r.inWaiting = false; // entry consumed (stale or not)
            if (r.state != BlackHoleRegion.STATE_WAITING) continue; // stale entry
            r.state = BlackHoleRegion.STATE_RECHECKING;
            r.recheckCursor = 0;
            addActive(r);
            expandFrontier(r);
        }
        // Frontier expansion for still-waiting regions when mass grew.
        // Throttled to 1/sec: creation is gated by the static MAX range, so a
        // retry almost never succeeds — but the pass stays as a safety net.
        // Eating itself continues every tick via the active budget below.
        if (mass > massAtLastWake) {
            massAtLastWake = mass;
            if (now - lastFrontierTick >= 20L) {
                lastFrontierTick = now;
                for (BlackHoleRegion r : waiting) {
                    expandFrontier(r);
                }
            }
        }

        if (active.isEmpty()) return;

        double cx = te.getPos().getX() + 0.5;
        double cy = te.getPos().getY() + 0.5;
        double cz = te.getPos().getZ() + 0.5;

        double totalWeight = 0;
        if (active.size() > 1) {
            for (BlackHoleRegion r : active) {
                r.weight = 1.0 / Math.max(r.distSq, 1.0);
                totalWeight += r.weight;
            }
        }

        // Итерация по индексу без snapshot-копии и без O(n) contains — проверка state O(1)
        int budget = BUDGET_PER_TICK;
        for (int idx = 0; idx < active.size() && budget > 0; ) {
            BlackHoleRegion r = active.get(idx);
            // Регион мог быть удалён лениво, но в active бывают только SCANNING/RECHECKING
            if (r.state != BlackHoleRegion.STATE_SCANNING && r.state != BlackHoleRegion.STATE_RECHECKING) {
                removeActiveAt(idx);
                continue;
            }
            int share;
            if (active.size() == 1) {
                share = budget;
            } else {
                share = (int) Math.round(BUDGET_PER_TICK * r.weight / totalWeight);
                if (share < 1) share = 1;
                if (share > budget) share = budget;
            }
            int used = processRegion(r, share, cx, cy, cz, idx);
            budget -= used;
            // если processRegion удалил r, элемент на idx уже следующий — не инкрементим
            if (idx < active.size() && active.get(idx) == r) idx++;
        }
    }

    // =================================================================
    // Region processing
    // =================================================================
    private int processRegion(BlackHoleRegion r, int budget, double cx, double cy, double cz, int idx) {
        switch (r.state) {
            case BlackHoleRegion.STATE_SCANNING:
                return processScan(r, budget, cx, cy, cz, idx);
            case BlackHoleRegion.STATE_RECHECKING:
                return processRecheck(r, budget, cx, cy, cz, idx);
            default:
                return 0;
        }
    }

    /**
     * Relative mass gain after which horizonPlus is recomputed inside a scan
     * slice. Horizon follows mass^(1/3), so a 0.2% mass drift moves the eat
     * front by ~0.07% (~1e-4 blocks) — invisible, but saves a pow per block.
     */
    private static final double HORIZON_REFRESH_REL = 0.002D;

    private int processScan(BlackHoleRegion r, int budget, double cx, double cy, double cz, int idx) {
        World world = te.getWorld();
        if (r.sortedOrder == null) r.buildSortedOrder(cx, cy, cz);
        int used = 0;
        double massDelta = 0.0D;
        double curMass = te.getMass();
        double horizon = BlackHoleUtils.getHorizonRadius(curMass);
        double horizonPlus = horizon + 0.5D;
        double horizonPlusSq = horizonPlus * horizonPlus;
        double boostRadius = BlackHoleUtils.getBoostRadius(curMass);
        double boostOuter = horizon + boostRadius;
        double boostOuterSq = boostOuter * boostOuter;
        double horizonSq = horizon * horizon;
        double pendingGain = 0.0D;
        double refreshAt = Math.max(1.0D, curMass * HORIZON_REFRESH_REL);
        BlockPos.PooledMutableBlockPos pooled = BlockPos.PooledMutableBlockPos.retain();
        try {
            while (used < budget && r.scanCursor < BlackHoleRegion.VOLUME) {
                int localIdx = r.sortedOrder[r.scanCursor++];
                used++;
                int lx = localIdx & 7;
                int ly = (localIdx >> 3) & 7;
                int lz = (localIdx >> 6) & 7;
                int bx = r.originX + lx;
                int by = r.originY + ly;
                int bz = r.originZ + lz;
                pooled.setPos(bx, by, bz);
                if (!world.isBlockLoaded(pooled)) continue;

                IBlockState st = world.getBlockState(pooled);
                if (st.getBlock() == Blocks.AIR || st.getBlock() instanceof BlackHoleBlock) continue;

                double dx = (bx + 0.5) - cx;
                double dy = (by + 0.5) - cy;
                double dz = (bz + 0.5) - cz;
                double bdistSq = dx * dx + dy * dy + dz * dz;

                Material mat = st.getMaterial();
                if (mat.isLiquid() || isVegetation(st, mat)) {
                    boolean insideHorizon = bdistSq <= horizonPlusSq;
                    double accel = BlackHoleUtils.getAccelerationSq(curMass, bdistSq);
                    // near-horizon cubic boost for liquids (0.08 threshold)
                    if (!insideHorizon && boostRadius > 0 && bdistSq > horizonSq && bdistSq < boostOuterSq) {
                        double dist = Math.sqrt(bdistSq);
                        double t = (boostOuter - dist) / boostRadius;
                        double t3 = t * t * t;
                        accel *= (1.0D + BlackHoleUtils.BOOST_MAX * t3);
                    }
                    if (insideHorizon || accel > 0.08) {
                        eat(world, pooled, true);
                        massDelta += BlackHoleUtils.MASS_PER_LIQUID;
                        curMass += BlackHoleUtils.MASS_PER_LIQUID;
                        pendingGain += BlackHoleUtils.MASS_PER_LIQUID;
                        if (pendingGain >= refreshAt) {
                            horizon = BlackHoleUtils.getHorizonRadius(curMass);
                            horizonPlus = horizon + 0.5D;
                            horizonPlusSq = horizonPlus * horizonPlus;
                            horizonSq = horizon * horizon;
                            boostRadius = BlackHoleUtils.getBoostRadius(curMass);
                            boostOuter = horizon + boostRadius;
                            boostOuterSq = boostOuter * boostOuter;
                            pendingGain = 0.0D;
                        }
                    } else {
                        r.addDeferred(localIdx);
                    }
                    continue;
                }

                float hardness;
                try { hardness = st.getBlockHardness(world, pooled); }
                catch (Exception e) { continue; }
                if (hardness < 0) { r.addDeferred(localIdx); continue; }

                double check = BlackHoleUtils.effectiveHardnessForCheck(mat, hardness);

                if (canEatSqBoosted(bdistSq, check, curMass, horizonPlusSq, horizonSq, boostOuterSq, boostOuter, boostRadius, horizon)) {
                    eat(world, pooled, false);
                    double gain = BlackHoleUtils.massGainForHardness(hardness);
                    massDelta += gain;
                    curMass += gain;
                    pendingGain += gain;
                    if (pendingGain >= refreshAt) {
                        horizon = BlackHoleUtils.getHorizonRadius(curMass);
                        horizonPlus = horizon + 0.5D;
                        horizonPlusSq = horizonPlus * horizonPlus;
                        horizonSq = horizon * horizon;
                        boostRadius = BlackHoleUtils.getBoostRadius(curMass);
                        boostOuter = horizon + boostRadius;
                        boostOuterSq = boostOuter * boostOuter;
                        pendingGain = 0.0D;
                    }
                } else {
                    r.addDeferred(localIdx);
                }
            }
        } finally {
            pooled.release();
        }

        if (r.scanCursor >= BlackHoleRegion.VOLUME) finishScan(r, idx);
        if (massDelta != 0.0D) te.addMass(massDelta);
        return used;
    }

    /**
     * Safety full recheck of all settled regions at current mass.
     * EMPTY/WAITING regions go back to SCANNING so missed blocks and liquids
     * are picked up in distance order within the normal per-tick budget.
     * Regions already in flight (SCANNING/RECHECKING) are left alone.
     * Requeued WAITING regions are dropped from the wake queue eagerly.
     */
    private void fullRescan() {
        for (BlackHoleRegion r : allRegions.values()) {
            if (r.state == BlackHoleRegion.STATE_EMPTY
                    || r.state == BlackHoleRegion.STATE_WAITING) {
                if (r.state == BlackHoleRegion.STATE_WAITING) removeWaiting(r);
                r.resetToScanning();
                addActive(r);
            }
        }
    }

    /**
     * Immediate revisit after a significant mass jump (e.g. /blockdata).
     * Only EMPTY regions within the current stone-eat reach are requeued:
     * that's where new growth can appear. WAITING regions are covered by the
     * wake loop in the same tick, distant regions are unreachable anyway.
     * Requeued regions resume frontier expansion in finishScan, so a fully
     * settled hole (empty active/waiting) starts growing again at once.
     */
    private void promptRescan(double mass) {
        double reach = Math.min(
                BlackHoleUtils.getBlockEatRadiusByHardness(mass, BlackHoleUtils.FAKE_HARDNESS_ROCK),
                BlackHoleUtils.MAX_BLOCK_CAPTURE_RANGE);
        double rr = reach + 7.0D; // + region half-diagonal, so edge regions qualify
        double rrSq = rr * rr;
        for (BlackHoleRegion r : allRegions.values()) {
            if (r.state != BlackHoleRegion.STATE_EMPTY) continue;
            if (r.distSq > rrSq) continue;
            r.resetToScanning();
            addActive(r);
        }
    }

    private void finishScan(BlackHoleRegion r, int idx) {
        r.freeSortedOrder();
        // Always expand the frontier — hole keeps eating farther regions even
        // if it had to defer some blocks here.
        expandFrontier(r);
        if (r.deferredCount == 0) {
            r.state = BlackHoleRegion.STATE_EMPTY;
            removeActive(idx, r);
        } else {
            r.state = BlackHoleRegion.STATE_WAITING;
            r.wakeMass = computeWakeMass(r);
            removeActive(idx, r);
            addWaiting(r);
        }
    }

    private int processRecheck(BlackHoleRegion r, int budget, double cx, double cy, double cz, int idx) {
        World world = te.getWorld();
        int used = 0;
        double massDelta = 0.0D;
        double curMass = te.getMass();
        double horizon = BlackHoleUtils.getHorizonRadius(curMass);
        double horizonPlus = horizon + 0.5D;
        double horizonPlusSq = horizonPlus * horizonPlus;
        double horizonSq = horizon * horizon;
        double boostRadius = BlackHoleUtils.getBoostRadius(curMass);
        double boostOuter = horizon + boostRadius;
        double boostOuterSq = boostOuter * boostOuter;
        double pendingGain = 0.0D;
        double refreshAt = Math.max(1.0D, curMass * HORIZON_REFRESH_REL);
        BlockPos.PooledMutableBlockPos pooled = BlockPos.PooledMutableBlockPos.retain();
        try {
            while (r.recheckCursor < r.deferredCount && used < budget) {
                short localIdx = r.deferred[r.recheckCursor];
                used++;
                if (localIdx < 0) { r.recheckCursor++; continue; }
                int lx = localIdx & 7;
                int ly = (localIdx >> 3) & 7;
                int lz = (localIdx >> 6) & 7;
                int bx = r.originX + lx;
                int by = r.originY + ly;
                int bz = r.originZ + lz;
                pooled.setPos(bx, by, bz);
                if (!world.isBlockLoaded(pooled)) { r.recheckCursor++; continue; }

                IBlockState st = world.getBlockState(pooled);
                if (st.getBlock() == Blocks.AIR) {
                    r.markDeferredRemoved(r.recheckCursor++);
                    continue;
                }

                double dx = (bx + 0.5) - cx;
                double dy = (by + 0.5) - cy;
                double dz = (bz + 0.5) - cz;
                double bdistSq = dx * dx + dy * dy + dz * dz;

                Material mat = st.getMaterial();
                if (mat.isLiquid() || isVegetation(st, mat)) {
                    boolean insideHorizon = bdistSq <= horizonPlusSq;
                    double accel = BlackHoleUtils.getAccelerationSq(curMass, bdistSq);
                    if (!insideHorizon && boostRadius > 0 && bdistSq > horizonSq && bdistSq < boostOuterSq) {
                        double dist = Math.sqrt(bdistSq);
                        double t = (boostOuter - dist) / boostRadius;
                        double t3 = t * t * t;
                        accel *= (1.0D + BlackHoleUtils.BOOST_MAX * t3);
                    }
                    if (insideHorizon || accel > 0.08) {
                        eat(world, pooled, true);
                        massDelta += BlackHoleUtils.MASS_PER_LIQUID;
                        curMass += BlackHoleUtils.MASS_PER_LIQUID;
                        pendingGain += BlackHoleUtils.MASS_PER_LIQUID;
                        if (pendingGain >= refreshAt) {
                            horizon = BlackHoleUtils.getHorizonRadius(curMass);
                            horizonPlus = horizon + 0.5D;
                            horizonPlusSq = horizonPlus * horizonPlus;
                            horizonSq = horizon * horizon;
                            boostRadius = BlackHoleUtils.getBoostRadius(curMass);
                            boostOuter = horizon + boostRadius;
                            boostOuterSq = boostOuter * boostOuter;
                            pendingGain = 0.0D;
                        }
                        r.markDeferredRemoved(r.recheckCursor);
                    }
                    r.recheckCursor++;
                    continue;
                }

                float hardness;
                try { hardness = st.getBlockHardness(world, pooled); }
                catch (Exception e) { r.recheckCursor++; continue; }
                if (hardness < 0) { r.recheckCursor++; continue; }

                double check = BlackHoleUtils.effectiveHardnessForCheck(mat, hardness);

                if (canEatSqBoosted(bdistSq, check, curMass, horizonPlusSq, horizonSq, boostOuterSq, boostOuter, boostRadius, horizon)) {
                    eat(world, pooled, false);
                    double gain = BlackHoleUtils.massGainForHardness(hardness);
                    massDelta += gain;
                    curMass += gain;
                    pendingGain += gain;
                    if (pendingGain >= refreshAt) {
                        horizon = BlackHoleUtils.getHorizonRadius(curMass);
                        horizonPlus = horizon + 0.5D;
                        horizonPlusSq = horizonPlus * horizonPlus;
                        horizonSq = horizon * horizon;
                        boostRadius = BlackHoleUtils.getBoostRadius(curMass);
                        boostOuter = horizon + boostRadius;
                        boostOuterSq = boostOuter * boostOuter;
                        pendingGain = 0.0D;
                    }
                    r.markDeferredRemoved(r.recheckCursor);
                }
                r.recheckCursor++;
            }
        } finally {
            pooled.release();
        }

        if (r.recheckCursor >= r.deferredCount) {
            r.compactDeferred();
            if (r.deferredCount == 0) {
                r.state = BlackHoleRegion.STATE_EMPTY;
                removeActive(idx, r);
            } else {
                r.state = BlackHoleRegion.STATE_WAITING;
                r.wakeMass = computeWakeMass(r);
                removeActive(idx, r);
                addWaiting(r);
            }
        }
        if (massDelta != 0.0D) te.addMass(massDelta);
        return used;
    }

    // =================================================================
    // Frontier expansion
    // =================================================================
    private void expandFrontier(BlackHoleRegion r) {
        int s = BlackHoleRegion.SIZE;
        int[][] dirs = {{s,0,0},{-s,0,0},{0,s,0},{0,-s,0},{0,0,s},{0,0,-s}};
        for (int[] d : dirs) {
            BlackHoleRegion nr = getOrCreateRegion(r.originX + d[0], r.originY + d[1], r.originZ + d[2]);
            if (nr != null
                    && nr.state == BlackHoleRegion.STATE_SCANNING
                    && !nr.inActive) {
                addActive(nr);
            }
        }
    }

    private BlackHoleRegion getOrCreateRegion(int ox, int oy, int oz) {
        int s = BlackHoleRegion.SIZE;
        if (oy < 0 || oy >= 256) return null;

        long key = packKey(ox, oy, oz);
        BlackHoleRegion r = allRegions.get(key);
        if (r != null) return r;

        double cx = te.getPos().getX() + 0.5;
        double cy = te.getPos().getY() + 0.5;
        double cz = te.getPos().getZ() + 0.5;

        // Smoothing: use closest point of region cube instead of center.
        // This removes 8x8 cube steps at the MAX boundary.
        // Region creation is gated only by MAX_BLOCK_CAPTURE_RANGE (hard cap),
        // not by current reachSoft - per-block canEat inside the region gives
        // the true spherical front (radial sortedOrder) without quantization.
        double nearX = Math.max(ox, Math.min(cx, ox + s));
        double nearY = Math.max(oy, Math.min(cy, oy + s));
        double nearZ = Math.max(oz, Math.min(cz, oz + s));
        double ndx = nearX - cx, ndy = nearY - cy, ndz = nearZ - cz;
        double nearDist = Math.sqrt(ndx * ndx + ndy * ndy + ndz * ndz);

        if (nearDist > BlackHoleUtils.MAX_BLOCK_CAPTURE_RANGE) return null;

        r = new BlackHoleRegion(ox, oy, oz, cx, cy, cz);
        allRegions.put(key, r);
        return r;
    }

    /** Seed the region containing the black hole itself. Called once on first tick. */
    public void seed() {
        int ox = te.getPos().getX() & ~7;
        int oy = te.getPos().getY() & ~7;
        int oz = te.getPos().getZ() & ~7;
        BlackHoleRegion r = getOrCreateRegion(ox, oy, oz);
        if (r != null) addActive(r);
    }

    // =================================================================
    // Event hooks
    // =================================================================
    /** Called when a block is placed / liquid appears in the world. */
    public void onBlockPlaced(BlockPos bp) {
        long key = packKeyForBlock(bp);
        BlackHoleRegion r = allRegions.get(key);
        if (r == null) {
            int ox = bp.getX() & ~7;
            int oy = bp.getY() & ~7;
            int oz = bp.getZ() & ~7;
            r = getOrCreateRegion(ox, oy, oz);
            if (r == null) return;
            addActive(r);
            // New region will scan and pick the block up in sorted order
            return;
        }

        if (r.state == BlackHoleRegion.STATE_EMPTY) {
            r.resetToScanning();
            addActive(r);
        } else if (r.state == BlackHoleRegion.STATE_WAITING) {
            int lx = bp.getX() - r.originX;
            int ly = bp.getY() - r.originY;
            int lz = bp.getZ() - r.originZ;
            if (lx < 0 || lx >= BlackHoleRegion.SIZE || ly < 0 || ly >= BlackHoleRegion.SIZE || lz < 0 || lz >= BlackHoleRegion.SIZE) return;
            int idx = (lz << 6) | (ly << 3) | lx;
            // Avoid duplicate deferral, but ensure recheck
            if (r.isDeferred != null && idx >= 0 && idx < r.isDeferred.length && r.isDeferred[idx]) {
                // already deferred
            } else {
                r.addDeferred(idx);
            }
            // Recompute wakeMass for the new block - old wakeMass may be huge
            // (hard block) and the new dirt should wake earlier.
            double bdist = dist(bp, te.getPos().getX() + 0.5, te.getPos().getY() + 0.5, te.getPos().getZ() + 0.5);
            World world = te.getWorld();
            if (world != null && world.isBlockLoaded(bp)) {
                try {
                    IBlockState st = world.getBlockState(bp);
                    Material mat = st.getMaterial();
                    boolean veg = isVegetation(st, mat);
                    if (!mat.isLiquid() && !veg) {
                        float h = st.getBlockHardness(world, bp);
                        if (h >= 0) {
                            // Same fake hardness as the eat check (ore 3.0 -> 1.5 etc.)
                            double eff = BlackHoleUtils.effectiveHardnessForCheck(mat, h);
                            double mAccel = eff * bdist * bdist / BlackHoleUtils.G;
                            double mHorizon = BlackHoleUtils.massForHorizon(bdist - 0.5);
                            double need = Math.min(mAccel, mHorizon);
                            if (need == mAccel) need = boostedNeed(bdist, eff, mAccel, mHorizon);
                            if (need < r.wakeMass) r.wakeMass = need;
                        }
                    } else {
                        // Liquids/vegetation never stay long in deferred, wake with 0.08
                        double eff = 0.08;
                        double mAccel = eff * bdist * bdist / BlackHoleUtils.G;
                        double mHorizon = BlackHoleUtils.massForHorizon(bdist - 0.5);
                        double need = Math.min(mAccel, mHorizon);
                        if (need == mAccel) need = boostedNeed(bdist, eff, mAccel, mHorizon);
                        if (need < r.wakeMass) r.wakeMass = need;
                    }
                } catch (Exception ignored) {}
            }
            r.state = BlackHoleRegion.STATE_RECHECKING;
            r.recheckCursor = 0;
            removeWaiting(r); // stale queue entry, if any — region is handled via active now
            addActive(r);
        } else {
            // SCANNING или RECHECKING: sortedOrder мог уже пройти эту позицию,
            // либо его вовсе нет. Явно кладём блок в deferred.
            int lx = bp.getX() - r.originX;
            int ly = bp.getY() - r.originY;
            int lz = bp.getZ() - r.originZ;
            if (lx < 0 || lx >= BlackHoleRegion.SIZE || ly < 0 || ly >= BlackHoleRegion.SIZE || lz < 0 || lz >= BlackHoleRegion.SIZE) return;
            int idx = (lz << 6) | (ly << 3) | lx;
            r.addDeferred(idx);
            // Для SCANNING: finishScan посчитает wakeMass уже с учётом этого блока.
            // Для RECHECKING: запись встанет в конец deferred, и recheckCursor её дойдёт
            // (recheckCursor <= deferredCount всегда, потому что мы только что увеличили count).
        }
    }

    /** Called when a chunk finishes loading. Resets any regions in that chunk. */
    public void onChunkLoaded(Chunk chunk) {
        // Chunk covers 16x16 -> 2x2 region columns, y range 0..255 -> 32 rows.
        for (int dx = 0; dx < 16; dx += BlackHoleRegion.SIZE) {
            for (int dz = 0; dz < 16; dz += BlackHoleRegion.SIZE) {
                int ox = (chunk.x << 4) + dx;
                int oz = (chunk.z << 4) + dz;
                for (int oy = 0; oy < 256; oy += BlackHoleRegion.SIZE) {
                    BlackHoleRegion r = allRegions.get(packKey(ox, oy, oz));
                    if (r == null) continue;
                    // While chunk was unloaded, BH mass and world blocks may have
                    // changed arbitrarily. Need to handle both EMPTY and WAITING:
                    // - EMPTY: full rescan (new blocks may have appeared)
                    // - WAITING: deferred list is stale (new blocks missed, old
                    //   blocks may be air/hard). Move to RECHECKING and recompute
                    //   wakeMass instead of full rescan to keep budget low.
                    if (r.state == BlackHoleRegion.STATE_EMPTY) {
                        r.resetToScanning();
                        addActive(r);
                    } else if (r.state == BlackHoleRegion.STATE_WAITING) {
                        // Stale deferred - force recheck; if chunk was modified
                        // heavily, compact will drop air entries and recompute
                        // wakeMass will be updated after the recheck pass.
                        r.state = BlackHoleRegion.STATE_RECHECKING;
                        r.recheckCursor = 0;
                        r.wakeMass = computeWakeMass(r);
                        removeWaiting(r); // stale queue entry — region is handled via active now
                        addActive(r);
                    }
                }
            }
        }
    }

    // =================================================================
    // Helpers
    // =================================================================
    private boolean canEatSq(double bdistSq, double checkHardness, double curMass, double horizonPlusSq) {
        if (bdistSq <= horizonPlusSq) return true;
        return BlackHoleUtils.getAccelerationSq(curMass, bdistSq) >= checkHardness;
    }

    private boolean canEatSqBoosted(double bdistSq, double checkHardness, double curMass,
                                     double horizonPlusSq, double horizonSq, double boostOuterSq,
                                     double boostOuter, double boostRadius, double horizon) {
        if (bdistSq <= horizonPlusSq) return true;
        double accel = BlackHoleUtils.getAccelerationSq(curMass, bdistSq);
        if (boostRadius > 0 && bdistSq > horizonSq && bdistSq < boostOuterSq) {
            double dist = Math.sqrt(bdistSq);
            double t = (boostOuter - dist) / boostRadius;
            double t3 = t * t * t;
            accel *= (1.0D + BlackHoleUtils.BOOST_MAX * t3);
        }
        return accel >= checkHardness;
    }
    private void eat(World world, BlockPos bp, boolean liquid) {
        if (liquid) world.setBlockState(bp, Blocks.AIR.getDefaultState(), 2);
        else world.setBlockToAir(bp);
    }

    private double computeWakeMass(BlackHoleRegion r) {
        World world = te.getWorld();
        double cx = te.getPos().getX() + 0.5;
        double cy = te.getPos().getY() + 0.5;
        double cz = te.getPos().getZ() + 0.5;
        double minMass = Double.MAX_VALUE;
        BlockPos.PooledMutableBlockPos pooled = BlockPos.PooledMutableBlockPos.retain();
        try {
            for (int i = 0; i < r.deferredCount; i++) {
                short idx = r.deferred[i];
                if (idx < 0) continue;
                int lx = idx & 7;
                int ly = (idx >> 3) & 7;
                int lz = (idx >> 6) & 7;
                int bx = r.originX + lx;
                int by = r.originY + ly;
                int bz = r.originZ + lz;
                pooled.setPos(bx, by, bz);
                IBlockState st = world.getBlockState(pooled);
                if (st.getBlock() == Blocks.AIR) continue;
                float hardness;
                try { hardness = st.getBlockHardness(world, pooled); } catch (Exception e) { continue; }
                if (hardness < 0) continue;
                Material mat = st.getMaterial();
                boolean liquid = mat.isLiquid() || isVegetation(st, mat);
                double effective = liquid ? 0.08 : BlackHoleUtils.effectiveHardnessForCheck(mat, hardness);
                double bdist = dist(bx, by, bz, cx, cy, cz);
                double mAccel = effective * bdist * bdist / BlackHoleUtils.G;
                double mHorizon = BlackHoleUtils.massForHorizon(bdist - 0.5);
                double m = Math.min(mAccel, mHorizon);
                // Apply near-horizon boost correction: effective accel is up to 10x, so mass needed is lower.
                // One-step correction: evaluate boost at mAccel, adjust. Safe to wake early.
                if (m == mAccel && mAccel < mHorizon) {
                    double h = BlackHoleUtils.getHorizonRadius(mAccel);
                    double rb = BlackHoleUtils.getBoostRadius(mAccel);
                    if (rb > 0 && bdist > h && bdist < h + rb) {
                        double t = (h + rb - bdist) / rb;
                        double t3 = t * t * t;
                        double boost = 1.0D + BlackHoleUtils.BOOST_MAX * t3;
                        double mBoosted = mAccel / boost;
                        // refine once: boost shrinks with smaller mass, so take max of two estimates
                        if (mBoosted < m) {
                            // second iteration for stability (cheap, rare path)
                            double h2 = BlackHoleUtils.getHorizonRadius(mBoosted);
                            double rb2 = BlackHoleUtils.getBoostRadius(mBoosted);
                            if (rb2 > 0 && bdist > h2 && bdist < h2 + rb2) {
                                double t2 = (h2 + rb2 - bdist) / rb2;
                                double boost2 = 1.0D + BlackHoleUtils.BOOST_MAX * t2 * t2 * t2;
                                mBoosted = Math.max(mBoosted, mAccel / boost2);
                            }
                            m = Math.min(mBoosted, mHorizon);
                        }
                    }
                }
                if (m < minMass) minMass = m;
            }
        } finally {
            pooled.release();
        }
        return minMass;
    }

    private static double boostedNeed(double bdist, double effective, double mAccel, double mHorizon) {
        if (mAccel >= mHorizon) return mHorizon;
        double h = BlackHoleUtils.getHorizonRadius(mAccel);
        double rb = BlackHoleUtils.getBoostRadius(mAccel);
        if (rb <= 0 || bdist <= h || bdist >= h + rb) return mAccel;
        double t = (h + rb - bdist) / rb;
        double boost = 1.0D + BlackHoleUtils.BOOST_MAX * t * t * t;
        double mBoosted = mAccel / boost;
        double h2 = BlackHoleUtils.getHorizonRadius(mBoosted);
        double rb2 = BlackHoleUtils.getBoostRadius(mBoosted);
        if (rb2 > 0 && bdist > h2 && bdist < h2 + rb2) {
            double t2 = (h2 + rb2 - bdist) / rb2;
            double boost2 = 1.0D + BlackHoleUtils.BOOST_MAX * t2 * t2 * t2;
            double m2 = mAccel / boost2;
            if (m2 > mBoosted) mBoosted = m2;
        }
        return Math.min(mBoosted, mHorizon);
    }

    /** Vegetation that should be absorbed like liquids (hardness ~0.08): tallgrass, flowers, bushes, vine etc. */
    public static boolean isVegetation(IBlockState st) {
        if (st == null) return false;
        return isVegetation(st, st.getMaterial());
    }

    /** Same as above, but reuses an already-fetched Material (hot-path overload). */
    public static boolean isVegetation(IBlockState st, Material m) {
        if (st == null || m == null) return false;
        if (m == Material.PLANTS || m == Material.VINE || m == Material.WEB) return true;
        net.minecraft.block.Block b = st.getBlock();
        if (b instanceof BlockBush) return true;
        if (b instanceof BlockVine) return true;
        return false;
    }
    private static double dist(BlockPos bp, double cx, double cy, double cz) {
        double dx = bp.getX() + 0.5 - cx;
        double dy = bp.getY() + 0.5 - cy;
        double dz = bp.getZ() + 0.5 - cz;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    private static double dist(int bx, int by, int bz, double cx, double cy, double cz) {
        double dx = bx + 0.5 - cx;
        double dy = by + 0.5 - cy;
        double dz = bz + 0.5 - cz;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    private static long packKey(int ox, int oy, int oz) {
        // SIZE==8 → division = arithmetic shift, floorDiv == >>3 for power-of-two
        int rx = ox >> 3;
        int ry = oy >> 3;
        int rz = oz >> 3;
        return ((long)(rx & 0xFFFFFF) << 30)
              | ((long)(ry & 0x3F) << 24)
              | (long)(rz & 0xFFFFFF);
    }

    private static long packKeyForBlock(BlockPos bp) {
        // floor to multiple of 8 via & ~7, same as floorDiv*s for power-of-two
        int ox = bp.getX() & ~7;
        int oy = bp.getY() & ~7;
        int oz = bp.getZ() & ~7;
        return packKey(ox, oy, oz);
    }
}
