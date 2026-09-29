package astrotweaks.block.black_hole;

//import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Random;
import java.util.Set;


/**
 * Вакуум чёрной дыры — расход кислорода в области сильной гравитации.
 *
 * <p><b>Почему свой счётчик, а не {@code Entity#getAir()}.</b>
 * {@code EntityLivingBase.onUpdate()} в начале каждого тика игрока делает
 * {@code if (!isInsideOfMaterial(WATER)) setAir(300)}. Любой дренаж, который
 * читает {@code getAir()} позже (блочные TE тикают после сущностей), всегда
 * видит ровно 300 и замораживает полоску на 299 — то есть 10 полных пузырьков
 * и ноль урона. Ванилла от этого защищена тем, что её дренаж живёт ВНУТРИ
 * {@code onUpdate()} сразу после сброса и в воде сброса вообще нет.
 * Поэтому здесь держим собственный счётчик (в {@code getEntityData()} игрока,
 * он же переживает релог) и каждый тик переписываем его в поле {@code AIR},
 * которое синхронизируется на клиент как data-param.
 *
 * <p>Дренаж дословно повторяет ванильный: -1 за тик
 * при {@code air == -20} — {@code setAir(0)}, 8 частиц
 * {@code WATER_BUBBLE} и 2 урона {@code DROWN}. Отличается только условие входа:
 * {@code isInsideOfMaterial(WATER)} заменено на ускорение поля дыры.
 *
 * <p>Зона считается только на сервере (массы там настоящие). Клиент зону не
 * повторяет — он берёт значение воздуха, пришедшее с сервера.
 */
public final class BlackHoleVacuum {

    /** Ключ счётчика в {@code Entity#getEntityData()} (переживает релог). */
    private static final String TAG_AIR = "in_vacuum";

    /** Entity.rand защищён, а getRand() в 1.12.2 ещё нет — свой генератор. */
    private static final Random RNG = new Random();

    private BlackHoleVacuum() {}

    /** Ускорение поля, при котором кислород уже заканчивается. */
    public static boolean isVacuum(double accel) {
        return accel > BlackHoleUtils.SUFFOCATION_ACCEL;
    }

    /**
     * Кто вообще может задохнуться. Точное условие ваниллы
     * ({@code !canBreatheUnderwater() && !WATER_BREATHING && !disableDamage}).
     */
    public static boolean canDrown(EntityPlayer player) {
        return !player.canBreatheUnderwater() && !player.isPotionActive(MobEffects.WATER_BREATHING) && !player.capabilities.disableDamage && !player.isSpectator() && player.isEntityAlive();
    }

    /**
     * Максимальное ускорение поля всех активных дыр в точке сущности.
     * Только сервер: массы дыр там настоящие, на клиенте они отстают.
     */
    public static double accelAt(World world, Entity entity) {
        Set<BlackHoleTileEntity> active = BlackHoleTileEntity.getActiveHoles();
        if (active.isEmpty()) return 0.0D;
        // Тот же anchor, что и в tickEntities(): центр тела, а не ноги.
        double x = entity.posX;
        double y = entity.posY + entity.height * 0.5;
        double z = entity.posZ;

        double best = 0.0D;
        for (BlackHoleTileEntity bh : active) {
            if (bh.isInvalid() || bh.getWorld() != world) continue;
            BlockPos p = bh.getPos();
            double m = bh.getMass();
            double dx = (p.getX() + 0.5) - x;
            double dy = (p.getY() + 0.5) - y;
            double dz = (p.getZ() + 0.5) - z;
            double distSq = dx * dx + dy * dy + dz * dz;
            // Буст максимум x10 от сырого G*m/distSq — дальше вакуум невозможен.
            // Проверяем ДО обращения к чанку: isBlockLoaded + getTileEntity — самые
            // дорогие вызовы в цикле, который крутится каждый тик на каждого игрока.
            if (BlackHoleUtils.G * m / distSq * (1.0D + BlackHoleUtils.BOOST_MAX) <= BlackHoleUtils.SUFFOCATION_ACCEL) continue;
            if (!world.isBlockLoaded(p)) continue;
            // Stale-экземпляр после выгрузки чанка — как в BlackHoleWorldRenderer.
            TileEntity current = world.getTileEntity(p);
            if (current != null && current != bh) continue;

            double accel = BlackHoleUtils.getAccelerationSqBoosted(m, distSq,
                    BlackHoleUtils.getHorizonRadius(m), BlackHoleUtils.getBoostRadius(m));
            if (accel > best) {
                best = accel;
                if (isVacuum(best)) return best; // дальше некуда
            }
        }
        return best;
    }

    /** 
     * Ванильный {@code EntityLivingBase.decreaseAirSupply}: -1/tick.
     * Текущий:  -2/tick
     * */
    public static int decreaseAirSupply(EntityLivingBase living, int air) {
        //int resp = EnchantmentHelper.getRespirationModifier(living);
        //return resp > 0 && RNG.nextInt(resp + 1) > 0 ? air : air - 2;
        return air - 2; // 2x расход кислорода от ванильного
    }

    /**
     * Тик вакуума для игрока. Вызывать из {@link TickEvent.PlayerTickEvent}
     * в фазе END — она стреляет в конце {@code EntityPlayer.onUpdate()}, то
     * есть уже ПОСЛЕ того, как ванилла сбросила воздух в 300.
     */
    public static void tick(EntityPlayerMP player) {
        NBTTagCompound data = player.getEntityData();
        boolean inVacuum = canDrown(player) && isVacuum(accelAt(player.world, player));

        if (!inVacuum) {
            // Вышли из вакуума: счётчик сбрасываем, а поле AIR уже 300 —
            // ровно как в ванилле, когда игрок вынырнул из воды.
            if (data.hasKey(TAG_AIR)) data.removeTag(TAG_AIR);
            return;
        }

        int air = data.hasKey(TAG_AIR) ? data.getInteger(TAG_AIR) : 300; // 300 - Полный запас воздуха у Entity
        air = decreaseAirSupply(player, air);
        // Ванилла сравнивает "== -20"; "<=" даёт то же в норме и заодно
        // не даёт зависнуть, если воздух оказался ниже порога.
        if (air <= BlackHoleUtils.DROWN_AIR) {
            air = 0;
            spawnBubbles(player);
            player.attackEntityFrom(DamageSource.DROWN, 4.0F); // 2x drown damage
        }
        data.setInteger(TAG_AIR, air);
        player.setAir(air);
    }

    private static void spawnBubbles(EntityLivingBase living) {
        World world = living.world;
        //if (world == null) return;  player.world УЖЕ ПРОВЕРЕН И НЕ NULL
        for (int i = 0; i < 8; ++i) {
            float fx = RNG.nextFloat() - RNG.nextFloat();
            float fy = RNG.nextFloat() - RNG.nextFloat();
            float fz = RNG.nextFloat() - RNG.nextFloat();
            world.spawnParticle(EnumParticleTypes.WATER_BUBBLE,
                    living.posX + fx, living.posY + fy, living.posZ + fz,
                    living.motionX, living.motionY, living.motionZ);
        }
    }
}
