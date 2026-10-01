package astrotweaks.tech.sd;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

/**
 * Spatial Dome coverage index — QTS-style chunk map, tuned for max range 128.
 *
 * <p>Both sides: server (gravity/block/vacuum/tug suppression) and client
 * (local swim-pull suppression in {@code BlackHoleGravityClientHandler}).
 * Client entries come from TE onLoad/sync packets, tickets only on server.
 *
 * <p>Protection rule: any point with distanceSq to a dome center
 * {@code <= range^2} is protected. Sphere, center = block center.
 * Horizon absorption (dist {@code <=} horizon) intentionally bypasses this —
 * singularity contact still kills, dome only cancels gravity at range.
 */
public final class DomeManager {

    private DomeManager() {}

    /** World -&gt; chunk -&gt; dome positions covering that chunk. */
    private static final Map<World, Map<ChunkPos, Set<BlockPos>>> worldChunkMap = new ConcurrentHashMap<>();
    /** World -&gt; dome pos -&gt; radius. */
    private static final Map<World, Map<BlockPos, Integer>> worldPosRangeMap = new ConcurrentHashMap<>();

    public static void addDome(World world, BlockPos pos, int range) {
        if (world == null || pos == null) return;
        BlockPos key = pos.toImmutable();
        Map<BlockPos, Integer> posMap = worldPosRangeMap.computeIfAbsent(world, k -> new ConcurrentHashMap<>());
        posMap.put(key, range);
        updateChunksForDome(world, key, range, true);
        if (!world.isRemote) notifyHoles(world, key, range);
    }

    public static void removeDome(World world, BlockPos pos) {
        if (world == null || pos == null) return;
        Map<BlockPos, Integer> posMap = worldPosRangeMap.get(world);
        if (posMap == null) return;
        // TE pos instance may differ from the registered immutable key — match by coords.
        BlockPos found = null;
        Integer range = null;
        for (Map.Entry<BlockPos, Integer> e : posMap.entrySet()) {
            BlockPos k = e.getKey();
            if (k.getX() == pos.getX() && k.getY() == pos.getY() && k.getZ() == pos.getZ()) {
                found = k;
                range = e.getValue();
                break;
            }
        }
        if (found == null) return;
        posMap.remove(found);
        updateChunksForDome(world, found, range != null ? range : 0, false);
        if (posMap.isEmpty()) worldPosRangeMap.remove(world);
        if (!world.isRemote) notifyHoles(world, found, range != null ? range : 0);
    }

    public static void updateRange(World world, BlockPos pos, int oldRange, int newRange) {
        if (world == null || pos == null || oldRange == newRange) return;
        Map<BlockPos, Integer> posMap = worldPosRangeMap.get(world);
        if (posMap == null) return;
        BlockPos found = null;
        for (BlockPos k : posMap.keySet()) {
            if (k.getX() == pos.getX() && k.getY() == pos.getY() && k.getZ() == pos.getZ()) {
                found = k;
                break;
            }
        }
        if (found == null) {
            addDome(world, pos, newRange);
            return;
        }
        updateChunksForDome(world, found, oldRange, false);
        posMap.put(found, newRange);
        updateChunksForDome(world, found, newRange, true);
        if (!world.isRemote) notifyHoles(world, found, Math.max(oldRange, newRange));
    }

    /** Drop all entries for an unloaded world (prevents World key leak). */
    public static void removeWorld(World world) {
        if (world == null) return;
        worldPosRangeMap.remove(world);
        worldChunkMap.remove(world);
    }

    /** O(1) fast path: false when no domes exist — BH hot loops skip lookups entirely. */
    public static boolean hasDomes(World world) {
        if (world == null) return false;
        Map<BlockPos, Integer> posMap = worldPosRangeMap.get(world);
        return posMap != null && !posMap.isEmpty();
    }

    // =================================================================
    // Queries — hot path, zero allocation
    // =================================================================

    /** Entity anchor must be computed by the caller (body center, items +0.25). */
    public static boolean isProtected(World world, double x, double y, double z) {
        if (world == null) return false;
        Map<BlockPos, Integer> posMap = worldPosRangeMap.get(world);
        if (posMap == null || posMap.isEmpty()) return false;
        Map<ChunkPos, Set<BlockPos>> chunkMap = worldChunkMap.get(world);
        if (chunkMap == null) return false;
        Set<BlockPos> domes = chunkMap.get(new ChunkPos(((int) Math.floor(x)) >> 4, ((int) Math.floor(z)) >> 4));
        if (domes == null || domes.isEmpty()) return false;
        for (BlockPos sp : domes) {
            Integer range = posMap.get(sp);
            if (range == null) continue;
            double dx = (sp.getX() + 0.5D) - x;
            double dy = (sp.getY() + 0.5D) - y;
            double dz = (sp.getZ() + 0.5D) - z;
            double r = (double) range.intValue();
            if (dx * dx + dy * dy + dz * dz <= r * r) return true;
        }
        return false;
    }

    public static boolean isBlockProtected(World world, int x, int y, int z) {
        return isProtected(world, x + 0.5D, y + 0.5D, z + 0.5D);
    }

    public static boolean isBlockProtected(World world, BlockPos pos) {
        if (pos == null) return false;
        return isBlockProtected(world, pos.getX(), pos.getY(), pos.getZ());
    }

    public static boolean isEntityProtected(Entity e, double anchorY) {
        if (e == null || e.world == null) return false;
        return isProtected(e.world, e.posX, anchorY, e.posZ);
    }

    // =================================================================
    // Internals
    // =================================================================

    private static void updateChunksForDome(World world, BlockPos pos, int range, boolean add) {
        Map<ChunkPos, Set<BlockPos>> chunkMap = worldChunkMap.computeIfAbsent(world, k -> new ConcurrentHashMap<>());
        int minX = (pos.getX() - range) >> 4;
        int maxX = (pos.getX() + range) >> 4;
        int minZ = (pos.getZ() - range) >> 4;
        int maxZ = (pos.getZ() + range) >> 4;
        for (int cx = minX; cx <= maxX; cx++) {
            for (int cz = minZ; cz <= maxZ; cz++) {
                ChunkPos cp = new ChunkPos(cx, cz);
                if (add) {
                    Set<BlockPos> set = chunkMap.computeIfAbsent(cp, k -> ConcurrentHashMap.newKeySet());
                    set.add(pos);
                } else {
                    Set<BlockPos> set = chunkMap.get(cp);
                    if (set != null) {
                        set.remove(pos);
                        if (set.isEmpty()) chunkMap.remove(cp);
                    }
                }
            }
        }
        if (chunkMap.isEmpty()) worldChunkMap.remove(world);
    }

    /**
     * Wake BH region managers around a changed dome so newly exposed blocks
     * are re-eaten without waiting for the 3-minute safety rescan.
     * Server only; bounded by (BH gravRange + domeRange).
     */
    private static void notifyHoles(World world, BlockPos domePos, int domeRange) {
        java.util.Set<astrotweaks.block.black_hole.BlackHoleTileEntity> active =
                astrotweaks.block.black_hole.BlackHoleTileEntity.getActiveHoles();
        if (active.isEmpty()) return;
        for (astrotweaks.block.black_hole.BlackHoleTileEntity bh : active) {
            if (bh.isInvalid() || bh.getWorld() != world) continue;
            BlockPos bp = bh.getPos();
            double dx = (bp.getX() + 0.5D) - (domePos.getX() + 0.5D);
            double dy = (bp.getY() + 0.5D) - (domePos.getY() + 0.5D);
            double dz = (bp.getZ() + 0.5D) - (domePos.getZ() + 0.5D);
            double distSq = dx * dx + dy * dy + dz * dz;
            double reach = astrotweaks.block.black_hole.BlackHoleUtils.getGravityRange(bh.getMass())
                    + domeRange + 16.0D;
            if (distSq > reach * reach) continue;
            try {
                bh.getRegionManager().rescanAround(domePos, domeRange);
            } catch (Exception ignored) {}
        }
    }
}
