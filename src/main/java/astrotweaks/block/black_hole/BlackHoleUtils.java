package astrotweaks.block.black_hole;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;



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
    public static final double MIN_ACCEL = 0.002D;

    // =================================================================
    // Применение тяги к движению сущностей (один набор на сервер и клиент,
    // чтобы локальная тяга в жидкости совпадала с серверной по всем осям XYZ)
    // =================================================================
    /** Доля ускорения поля, уходящая в motion за тик (все оси XYZ). */
    public static final double MOTION_FACTOR = 0.35D;
    /** Потолок прироста скорости от тяги за тик (до MOTION_FACTOR). */
    public static final double MAX_ACCEL_PER_TICK = 5.0D;
    /** Потолок полной скорости сущности после тяги. */
    public static final double MAX_SPEED = 4.5D;
    /** Квадрат потолка скорости — клэмп без sqrt в частом случае (под потолком). */
    public static final double MAX_SPEED_SQ = MAX_SPEED * MAX_SPEED;
    /** Тяга не разгоняет быстрее, чем тело долетело бы до центра за ~2 тика. */
    public static final double DIST_PULL_CAP_FACTOR = 0.45D;

    /**
     * Порог ускорения, выше которого окрестность дыры считается вакуумом:
     * кислород заканчивается подобно как в воде
     */
    public static final double SUFFOCATION_ACCEL = 0.4D;
    /** Hard cap for gravity scan box (per user req) */
    public static final double MAX_GRAVITY_RANGE = 256.0D;
    /** Block capture radius cap - same constant as gravity per user req (perf limited) */
    public static final double MAX_BLOCK_CAPTURE_RANGE = 192.0D;


    // Horizon: R_h = C * mass^E ; v3: -25% base (H_SCALE*m^H_EXP): 200->0.58 ; 1000->0.82 ; 5000->1.17
    public static final double H_SCALE = 0.021D;
    public static final double H_EXP = 0.35D;

    // Near-horizon boost: Rb(m) = m^{0.30103}/16 = 2*(m/1e5)^{0.30103}
    // 1e5->2, 1e6->4, 1e7->8, 1e8->16, 1e9->32 (continuous)
    public static final double BOOST_EXP = 0.30103D;
    public static final double BOOST_INV16 = 1.0D / 16.0D;
    public static final double BOOST_MAX = 9.0D; // 10x at horizon (1+9)
    public static final double BOOST_POW = 3.0D; // cubic: flat far, sharp near horizon

    /**
     * Мемоизация для частых pow — single-slot.
     *
     * <p>Поля volatile НЕ для скорости (на x86 чтение volatile бесплатно), а
     * против гонки: в одиночной игре серверный и клиентский тики идут разными
     * потоками в одном JVM и пишут в эти же статические поля. Запись/чтение
     * {@code double}/{@code long} не атомарны по JMM, поэтому без volatile
     * возможен torn read. Значения при этом всегда «валидные» (результат для
     * какой-то массы) — гонка влияла только на попадание в кэш, не на числа.
     *
     * <p>Single-slot (а не карта) сознательно: при 2+ дырах он всё равно
     * промахивается на каждую, но тогда стоимость — лишь {@code Math.pow}.
     * Дорогие вызовы (pow) вынесены в точки, где масса уже известна: см.
     * {@code BlackHoleTileEntity#getRenderHorizon} и horizon/boost, которые
     * {@code tickEntities}/{@code processScan} считают раз на тик.
     */
    private static volatile double lastHorizonMassBits = Double.NaN;
    private static volatile double lastHorizonR = 0;
    private static volatile double lastHaloHorizon = Double.NaN;
    private static volatile double lastHaloThickness = 0;
    private static volatile double lastEvapMass = Double.NaN;
    private static volatile double lastEvapLoss = 0;
    private static volatile double lastBoostMassBits = Double.NaN;
    private static volatile double lastBoostR = 0;

    /** Halo thickness base formula: halo = 0.25 * horizon^0.602 (1->0.25, 10->1.0) */
    public static double getHaloThickness(double horizon) {
        if (horizon <= 0) return 0.16D;
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
    // Чёрный список сущностей (кальмар и дракон) переехал в BlackHoleTileEntity
    // прямой проверкой instanceof: цикл с Class.isInstance на каждую сущность
    // в кубе каждый тик стоил дороже самой проверки, а классов там всего два.

    /** Default mass for newly placed black hole */
    public static final double DEFAULT_MASS = 20000.0D;

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
    public static final double EVAP_BASE = 256.0D;
    public static final double EVAP_EXP = 0.30103D;

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
    public static final double MASS_PER_ITEM = 1.5D;
    public static final double MASS_PER_ENTITY = 20.0D;
    public static final double MASS_PER_XP = 0.5D;
    public static final double MASS_PER_PLAYER = 50.0D;
    /** Mass gained per liquid eaten. */
    public static final double MASS_PER_LIQUID = 1.0D;

    // =================================================================
    // Кислород (вакуум у дыры) — значения ванильного EntityLivingBase
    // =================================================================
    /** Полный запас воздуха (data-param AIR у Entity, как в ванилле). */
    //public static final int MAX_AIR = 300;
    /** Воздух кончился: EntityLivingBase на этом пороге делает setAir(0) + урон. */
    public static final int DROWN_AIR = -20;

    /** Радиус вакуума (точка, где accel == SUFFOCATION_ACCEL) для массы mass. */
    public static double getVacuumRadius(double mass) {
        if (mass <= 0) return 0;
        double r = Math.sqrt(G * mass / SUFFOCATION_ACCEL);
        if (r > MAX_GRAVITY_RANGE) r = MAX_GRAVITY_RANGE;
        return r;
    }

    // =================================================================
    // Adaptive sync / NBT интервалы — чем больше масса, тем реже обновления
    // =================================================================
    /** Минимальный интервал синхронизации с клиентом (тики) — для крошечных BH, где испарение заметно */
    public static final int SYNC_TICKS_MIN = 2;
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
            // 1 .. 8 тиков для 0.5..100k — дно 1-2 тика достигается на 20k
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
        //if (mass <= 0)  return 0;
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

    // =================================================================
    // Near-horizon gravity boost — sharp cubic ramp inside Ro=horizon+Rb
    // =================================================================

    /** Boost radius Rb(m) — distance from horizon where extra pull starts. */
    public static double getBoostRadius(double mass) {
        //if (mass < 0) return 0;
        if (Double.doubleToLongBits(mass) == Double.doubleToLongBits(lastBoostMassBits)) return lastBoostR;
        double r = Math.pow(mass, BOOST_EXP) * BOOST_INV16;
        if (r < 0) r = 0;
        if (r > MAX_GRAVITY_RANGE) r = MAX_GRAVITY_RANGE;
        lastBoostMassBits = mass;
        lastBoostR = r;
        return r;
    }

    /** Outer edge of boosted annulus. */
    public static double getBoostOuterRadius(double mass) {
        return getHorizonRadius(mass) + getBoostRadius(mass);
    }

    /**
     * Multiplicative boost factor at distance dist (requires horizon and Rb).
     * 1.0 outside Ro, (1+BOOST_MAX) at horizon, cubic falloff.
     * Hot path: 1 branch + 3 mul for t^3.
     */
    public static double getBoostFactor(double dist, double horizon, double boostRadius) {
        if (boostRadius <= 0) return 1.0D;
        double outer = horizon + boostRadius;
        if (dist <= horizon) return 1.0D + BOOST_MAX; // inside horizon already absorbed, but for completeness
        if (dist >= outer) return 1.0D;
        double t = (outer - dist) / boostRadius; // 0..1
        double t2 = t * t;
        double t3 = t2 * t; // cubic
        return 1.0D + BOOST_MAX * t3;
    }

    /** Boosted acceleration using precomputed dist (entity path — already has sqrt). */
    public static double getAccelerationBoosted(double mass, double dist, double horizon, double boostRadius) {
        double accel = getAcceleration(mass, dist);
        if (boostRadius <= 0) return accel;
        double outer = horizon + boostRadius;
        if (dist <= horizon || dist >= outer) return accel;
        double t = (outer - dist) / boostRadius;
        double t3 = t * t * t;
        return accel * (1.0D + BOOST_MAX * t3);
    }

    /**
     * Boosted acceleration using distSq (block path — avoids extra sqrt outside annulus).
     * Only computes sqrt when inside the boosted annulus.
     */
    public static double getAccelerationSqBoosted(double mass, double distSq, double horizon, double boostRadius) {
        double accel = getAccelerationSq(mass, distSq);
        if (boostRadius <= 0) return accel;
        double outer = horizon + boostRadius;
        double outerSq = outer * outer;
        double horizonSq = horizon * horizon;
        if (distSq <= horizonSq || distSq >= outerSq) return accel;
        double dist = Math.sqrt(distSq);
        double t = (outer - dist) / boostRadius;
        double t3 = t * t * t;
        return accel * (1.0D + BOOST_MAX * t3);
    }

    /**
     * Тело в жидкости (любой: ваниль + модовые). Hot path: сначала дешёвое
     * чтение флага воды без аллокаций; иначе ОДИН getBlockState в нижней трети
     * тела. Сознательно не используем {@code Entity.isInLava()}: в 1.12.2 это
     * полный скан AABB по блокам ({@code isMaterialInBB}), а точечная проба
     * покрывает те же игровые случаи (брод/плавание: нижняя часть тела в
     * жидкости) за один lookup. Направление расхождения безопасное: в редком
     * краевом случае ложный negative просто даёт старое поведение (пакеты),
     * ложных positive проба не даёт.
     * Вызывать только для игроков (их мало), а не для каждой сущности в кубе.
     */
    public static boolean isInLiquid(Entity e) {
        if (e == null || e.world == null) return false;
        if (e.isInWater()) return true;
        try {
            IBlockState st = e.world.getBlockState(new BlockPos(
                    e.posX, e.posY + e.height * 0.3D, e.posZ));
            return st.getMaterial().isLiquid();
        } catch (Exception ignored) {
            return false;
        }
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
