package astrotweaks.block.black_hole;

import net.minecraft.util.math.BlockPos;
import java.util.Arrays;

/**
 * State for one 8x8x8 region relative to a black hole.
 * Blocks are processed in strictly distance-sorted order to give a smooth
 * radial eating front (no staircase, no eaten cubes).
 */
public class BlackHoleRegion {

    public static final int SIZE = 8;
    public static final int VOLUME = SIZE * SIZE * SIZE; // 512

    // States
    public static final byte STATE_SCANNING   = 0; // cursor moving through sortedOrder
    public static final byte STATE_WAITING    = 1; // all deferred, waiting for mass
    public static final byte STATE_RECHECKING = 2; // re-checking deferred blocks after wake
    public static final byte STATE_EMPTY      = 3; // nothing left

    public final int originX, originY, originZ; // region origin in block coords (multiple of 8)
    public final double distSq;                 // squared distance from hole center to region center
    public double weight;                       // cached 1/distSq (budget distribution)

    public byte state = STATE_SCANNING;

    /** O(1) membership flags: maintained by BlackHoleRegionManager.addActive/addWaiting. */
    public boolean inActive;
    public boolean inWaiting;

    // --- Scan phase ---
    // 512 indices sorted by distance. Freed after the scan finishes to save memory.
    public short[] sortedOrder;
    public int scanCursor;

    // --- Deferred block tracking (lazy allocated; null means "empty region") ---
    public short[] deferred;
    public boolean[] isDeferred;
    public int deferredCount;

    // --- Recheck phase ---
    public int recheckCursor;

    // --- Wake-up threshold ---
    public double wakeMass = Double.MAX_VALUE;

    public BlackHoleRegion(int ox, int oy, int oz, double hx, double hy, double hz) {
        this.originX = ox;
        this.originY = oy;
        this.originZ = oz;
        double cx = ox + SIZE * 0.5;
        double cy = oy + SIZE * 0.5;
        double cz = oz + SIZE * 0.5;
        double dx = cx - hx, dy = cy - hy, dz = cz - hz;
        this.distSq = dx * dx + dy * dy + dz * dz;
    }

    /**
     * Build a per-region ordering of block indices (0..511) by squared distance
     * from the black hole center. Called once per region.
     *
     * Index layout: idx = (lz << 6) | (ly << 3) | lx with lx,ly,lz in [0,8).
     */
    // Reused sort-key buffer (server tick is single-threaded, no reentrancy here).
    private static final long[] SORT_KEYS = new long[VOLUME];

    public void buildSortedOrder(double hx, double hy, double hz) {
        if (sortedOrder != null) return;
        long[] keys = SORT_KEYS;
        double baseX = originX + 0.5, baseY = originY + 0.5, baseZ = originZ + 0.5;
        for (int i = 0; i < VOLUME; i++) {
            int lx = i & 7;
            int ly = (i >> 3) & 7;
            int lz = (i >> 6) & 7;
            double dx = (baseX + lx) - hx;
            double dy = (baseY + ly) - hy;
            double dz = (baseZ + lz) - hz;
            float d2 = (float)(dx * dx + dy * dy + dz * dz);
            // IEEE-754 bit patterns of positive floats are monotonic -> usable as sort key.
            // 32 bits of float + 9 bits of index = 41 bits, fits in long.
            keys[i] = ((long) Float.floatToIntBits(d2) << 9) | i;
        }
        Arrays.sort(keys);
        short[] order = new short[VOLUME];
        for (int i = 0; i < VOLUME; i++) order[i] = (short)(keys[i] & 0x1FF);
        this.sortedOrder = order;
    }

    public BlockPos blockPos(int localIdx) {
        int lx = localIdx & 7;
        int ly = (localIdx >> 3) & 7;
        int lz = (localIdx >> 6) & 7;
        return new BlockPos(originX + lx, originY + ly, originZ + lz);
    }

    public void addDeferred(int localIdx) {
        if (localIdx < 0 || localIdx >= VOLUME) return;
        if (isDeferred == null) {
            isDeferred = new boolean[VOLUME];
            deferred = new short[VOLUME];
        }
        if (isDeferred[localIdx]) return;
        if (deferredCount >= VOLUME) return; // safety: region already full
        isDeferred[localIdx] = true;
        deferred[deferredCount++] = (short) localIdx;
    }

    public void markDeferredRemoved(int i) {
        short idx = deferred[i];
        if (idx >= 0) isDeferred[idx] = false;
        deferred[i] = -1;
    }
    public void compactDeferred() {
        int w = 0;
        for (int i = 0; i < deferredCount; i++) {
            if (deferred[i] >= 0) deferred[w++] = deferred[i];
        }
        deferredCount = w;
        recheckCursor = 0;
    }
    /** Free the sorted order once the initial scan is complete. */
    public void freeSortedOrder() {
        sortedOrder = null;
    }

    /** Reset to a clean SCANNING state (e.g. after an EMPTY region was touched). */
    public void resetToScanning() {
        state = STATE_SCANNING;
        scanCursor = 0;
        recheckCursor = 0;
        deferredCount = 0;
        wakeMass = Double.MAX_VALUE;
        sortedOrder = null; // will be rebuilt on next scan
        if (isDeferred != null) java.util.Arrays.fill(isDeferred, false);
    }
}
