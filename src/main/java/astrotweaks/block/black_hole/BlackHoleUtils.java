package astrotweaks.block.black_hole;

import net.minecraft.block.material.Material;



public final class BlackHoleUtils {

    private BlackHoleUtils() {}

    /**
     * Fake hardness used ONLY for the eat-ability check (accel >= hardness).
     * Mass gain always uses the real hardness.
     *
     * <ul>
     *   <li>Material.ROCK with real hardness in [1.5, 5.0] -&gt; 1.5 (stone level,
     *       so ores don't hang in the air of the crater);</li>
     *   <li>Material.WOOD -&gt; 0.6 unconditionally;</li>
     *   <li>everything else -&gt; real hardness (0 maps to 0.1 as before).</li>
     * </ul>
     * Hot path: reference equality on Material singletons + one float range
     * check, no allocations. JIT-inlinable.
     */
    public static final double FAKE_HARDNESS_ROCK = 1.5D;
    public static final double FAKE_HARDNESS_WOOD = 0.6D;

    public static double effectiveHardnessForCheck(Material mat, float realHardness) {
        if (mat == Material.ROCK) {
            // Most common case in the crater is stone-like rock: single range check.
            // Outside the window (e.g. obsidian 50) falls through to real hardness.
            // The else-if below is intentional: ROCK != WOOD, saves one comparison.
            if (realHardness >= 1.5F && realHardness <= 5.0F) return FAKE_HARDNESS_ROCK;
        } else if (mat == Material.WOOD) {
            return FAKE_HARDNESS_WOOD;
        }
        return realHardness == 0.0F ? 0.1D : (double) realHardness;
    }

    /** Real hardness mapped to mass gain (zero-hardness blocks give 0.1). */
    public static double massGainForHardness(float realHardness) {
        return realHardness == 0.0F ? 0.1D : (double) realHardness;
    }

    /** Game gravity constant tuned so mass=1000 => gravity range ~22 blocks at threshold 0.001 */
    public static final double G = 5.0e-4;
    /** Minimal displacement per tick to be applied */
    public static final double MIN_ACCEL = 0.001D;
    /** Hard cap for gravity scan box (per user req) */
    public static final double MAX_GRAVITY_RANGE = 256.0D;
    /** Block capture radius cap - same constant as gravity per user req (perf limited) */
    public static final double MAX_BLOCK_CAPTURE_RANGE = 192.0D;


    // Horizon: R_h = C * mass^E ; v3: -25% base (H_SCALE*m^H_EXP): 200->0.58 ; 1000->0.82 ; 5000->1.17
    public static final double H_SCALE = 0.022D;
    public static final double H_EXP = 0.34D;

    /** Мемоизация для частых pow — single-slot, single-thread (майн single thread) */
    private static double lastHorizonMassBits = Double.NaN;
    private static double lastHorizonR = 0;
    private static double lastHaloHorizon = Double.NaN;
    private static double lastHaloThickness = 0;
    private static double lastEvapMass = Double.NaN;
    private static double lastEvapLoss = 0;

    /** Halo thickness base formula: halo = 0.25 * horizon^0.602 (1->0.25, 10->1.0) */
    public static double getHaloThickness(double horizon) {
        if (horizon <= 0) return 0.2D;
        if (Double.doubleToLongBits(horizon) == Double.doubleToLongBits(lastHaloHorizon)) return lastHaloThickness;
        double h = 0.2D * Math.pow(horizon, 0.60206D);
        if (h < 0.12D) h = 0.12D;
        if (h > 1.8D) h = 1.8D;
        lastHaloHorizon = horizon;
        lastHaloThickness = h;
        return h;
    }
    /** Legacy constant for compat - now computed */
    //public static final double HALO_DELTA = 0.35D;

    /** Blocks per block-eat cycle (every 5 ticks). Configurable */
    //public static int BLOCKS_PER_TICK = 16;
    /** Entity blacklist for capture */
    public static final java.util.Set<Class<? extends net.minecraft.entity.Entity>> ENTITY_BLACKLIST = new java.util.HashSet<>();
    static {
        ENTITY_BLACKLIST.add(net.minecraft.entity.passive.EntitySquid.class);
        ENTITY_BLACKLIST.add(net.minecraft.entity.boss.EntityDragon.class);
    }

    /** Default mass for newly placed black hole */
    public static final double DEFAULT_MASS = 5000.0D;

    /** Hard floor for mass (evaporation and drains never go below this). */
    public static final double MIN_MASS = 0.5D;
    /**
     * Hard cap for mass. Clamp, not reset: values above (e.g. huge NBT)
     * saturate here, values below MIN_MASS saturate at the floor.
     * Gravity range caps at 128 anyway (~32768 mass); horizon keeps growing
     * up to ~3.6e8 mass, so this cap only bounds runaway growth.
     */
    public static final double MAX_MASS = 1.0E12D;

    /**
     * Evaporation: mass lost per tick, inversely proportional to mass.
     * Halves every decade: 1e4 -&gt; 32, 1e5 -&gt; 16, 1e6 -&gt; 8, ...
     * loss(m) = EVAP_BASE / m^EVAP_EXP, EVAP_EXP = log10(2).
     */
    public static final double EVAP_BASE = 512.0D;
    public static final double EVAP_EXP = 0.30103D / 2;

    public static double getEvaporationPerTick(double mass) {
        if (mass < MIN_MASS)  return MIN_MASS; // floor or NaN: nothing to evaporate
        if (Double.doubleToLongBits(mass) == Double.doubleToLongBits(lastEvapMass)) return lastEvapLoss;
        double loss = EVAP_BASE / Math.pow(mass, EVAP_EXP);
        double maxLoss = mass - MIN_MASS;
        double ret = loss < maxLoss ? loss : maxLoss;
        lastEvapMass = mass;
        lastEvapLoss = ret;
        return ret;
    }

    /**
     * BH-vs-BH mass tug rate. A hole of mass M drains
     * TUG_RATE * M * (1 + grav) per tick from every other hole inside its
     * gravity range, where grav is its own acceleration at that distance.
     * The drained amount is credited to the drainer (conserved transfer).
     */
    public static final double TUG_RATE = 0.0001D;

    /** Clamp any mass value into [MIN_MASS, MAX_MASS] (NaN-safe: NaN -> floor). */
    public static double clampMass(double m) {
        if (!(m >= MIN_MASS)) return MIN_MASS;
        if (m > MAX_MASS) return MAX_MASS;
        return m;
    }

    /** Mass delta per absorption */
    public static final double MASS_PER_ITEM = 1.0D;
    public static final double MASS_PER_ENTITY = 15.0D;
    public static final double MASS_PER_XP = 0.5D;
    public static final double MASS_PER_PLAYER = 25.0D;
    /** Mass gained per liquid block eaten. Cheap — liquids have no structural cost. */
    public static final double MASS_PER_LIQUID = 0.5D;

    // =================================================================
    // Adaptive sync / NBT интервалы — чем больше масса, тем реже обновления
    // =================================================================
    /** Минимальный интервал синхронизации с клиентом (тики) — для крошечных BH, где испарение заметно */
    public static final int SYNC_TICKS_MIN = 1;
    /** Максимальный интервал синхронизации — для гигантов, где горизонт почти не меняется */
    public static final int SYNC_TICKS_MAX = 50;
    /** Минимальный интервал markDirty / сохранения NBT */
    public static final int NBT_TICKS_MIN = 5;
    /** Максимальный интервал сохранения NBT */
    public static final int NBT_TICKS_MAX = 200;
    /** Относительный порог для внепланового сохранения NBT (2% массы) — крупная дельта форсит сохранение даже до истечения интервала */
    public static final double NBT_DIRTY_RELATIVE_THRESHOLD = 0.02D;

    // Log-domain bounds for the adaptive intervals below (hoisted: log10 is needlessly
    // recomputed on every call otherwise; values are bit-identical to inline computation).
    private static final double LOG_MIN_MASS = Math.log10(MIN_MASS);
    private static final double LOG_MID_MASS = Math.log10(100000D);
    private static final double LOG_MAX_MASS = Math.log10(MAX_MASS);

    /** Адаптивный интервал: <100k — чаще (испарение заметно), >1M — реже. Плюс форсирование по дельте в TileEntity. */
    public static int getSyncInterval(double mass) {
        if (!(mass >= MIN_MASS)) return SYNC_TICKS_MIN;
        if (mass >= MAX_MASS) return SYNC_TICKS_MAX;
        final double LOG_MIN = LOG_MIN_MASS;
        final double LOG_MID = LOG_MID_MASS;
        final double LOG_MAX = LOG_MAX_MASS;
        double log = Math.log10(mass);
        if (mass < 100000D) {
            double t = (log - LOG_MIN) / (LOG_MID - LOG_MIN);
            if (t < 0) t = 0; if (t > 1) t = 1;
            // 1 .. 8 тиков для 0.5..100k — дно 1-2 тика реально достигается на 20k
            return (int) Math.round(SYNC_TICKS_MIN + t * (8 - SYNC_TICKS_MIN));
        } else {
            double t = (log - LOG_MID) / (LOG_MAX - LOG_MID);
            if (t < 0) t = 0; if (t > 1) t = 1;
            return (int) Math.round(8 + t * (SYNC_TICKS_MAX - 8));
        }
    }

    /** Адаптивный интервал сохранения NBT. */
    public static int getNbtInterval(double mass) {
        if (!(mass >= MIN_MASS)) return NBT_TICKS_MIN;
        if (mass >= MAX_MASS) return NBT_TICKS_MAX;
        final double LOG_MIN = LOG_MIN_MASS;
        final double LOG_MID = LOG_MID_MASS;
        final double LOG_MAX = LOG_MAX_MASS;
        double log = Math.log10(mass);
        if (mass < 100000D) {
            double t = (log - LOG_MIN) / (LOG_MID - LOG_MIN);
            if (t < 0) t = 0; if (t > 1) t = 1;
            return (int) Math.round(NBT_TICKS_MIN + t * (40 - NBT_TICKS_MIN));
        } else {
            double t = (log - LOG_MID) / (LOG_MAX - LOG_MID);
            if (t < 0) t = 0; if (t > 1) t = 1;
            return (int) Math.round(40 + t * (NBT_TICKS_MAX - 40));
        }
    }

    public static double getGravityRange(double mass) {
        if (mass <= 0) return 0;
        double r = Math.sqrt(G * mass / MIN_ACCEL);
        if (r > MAX_GRAVITY_RANGE) r = MAX_GRAVITY_RANGE;
        return r;
    }

    /** Effective block capture radius - not just hardnessMin, but also capped by MAX range */
    public static double getBlockCaptureRadius(double mass) {
        return getGravityRange(mass); // per user: same constant as gravity
    }

    public static double getHorizonRadius(double mass) {
        if (mass <= 0) return 0.01D;
        if (Double.doubleToLongBits(mass) == Double.doubleToLongBits(lastHorizonMassBits)) return lastHorizonR;
        double r = H_SCALE * Math.pow(mass, H_EXP);
        if (r < 0.01D) r = 0.01D;
        if (r > 100D) r = 100D;
        lastHorizonMassBits = mass;
        lastHorizonR = r;
        return r;
    }

    /**
     * Visual-only horizon radius — сейчас идентична геймплейной, делегируем для DRY и кэша.
     */
    public static double getVisualHorizonRadius(double mass) {
        return getHorizonRadius(mass);
    }

    /** Radius where accel >= hardness threshold (dynamic) */
    public static double getBlockEatRadiusByHardness(double mass, double hardness) {
        if (mass <= 0)  return 0;
        if (hardness < 0.05) hardness = 0.1; // zero-hardness ->0.1 per req
        double r = Math.sqrt(G * mass / hardness);
        double h = getHorizonRadius(mass);
        if (r < h + 0.5D) r = h + 0.5D;
        // cap by global block capture limit
        if (r > MAX_BLOCK_CAPTURE_RANGE) r = MAX_BLOCK_CAPTURE_RANGE;
        if (r > MAX_GRAVITY_RANGE) r = MAX_GRAVITY_RANGE;
        return r;
    }

    /** Legacy alias */
    public static double getBlockEatRadius(double mass) {
        return getBlockEatRadiusByHardness(mass, 0.2D);
    }

    /** Acceleration per tick towards center at distance r */
    public static double getAcceleration(double mass, double dist) {
        if (dist < 0.1D) dist = 0.1D;
        return G * mass / (dist * dist);
    }

    /**
     * Same as getAcceleration, but takes a precomputed squared distance.
     * Saves a sqrt in scan loops that only need distSq for the horizon check.
     */
    public static double getAccelerationSq(double mass, double distSq) {
        if (distSq < 0.01D) distSq = 0.01D;
        return G * mass / distSq;
    }

    /** Mass at which the horizon reaches radius r. Inverse of getHorizonRadius. */
    public static double massForHorizon(double r) {
        if (r <= H_SCALE) return H_SCALE;
        return Math.pow(r / H_SCALE, 1.0 / H_EXP);
    }

    /** Horizon radius that slowly expands with mass (alternative log formula)
     *  Exposed for debug, not used by default.
     */
    public static double getHorizonLog(double mass) {
        return 0.5D + Math.log10(1 + mass) * 1.2D;
    }
}
