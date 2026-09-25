package astrotweaks.item.SpatialAnchor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SpatialAnchorEvents {

    private static final Map<UUID, Vec3d> PREV_POS = new ConcurrentHashMap<>();
    private static final Map<UUID, Vec3d> PREV_MOTION = new ConcurrentHashMap<>();

    // ------------------------------------------------------------------
    // Knockback — оба режима
    // ------------------------------------------------------------------
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onKnockBack(LivingKnockBackEvent event) {
        if (event.getEntityLiving().world.isRemote) return;
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (!SpatialAnchor.hasActiveAnchor(player)) return;
        if (SpatialAnchor.hasFlightAnchor(player)) {
            // В полёте 100 FE/тик уже покрывает всё — блокируем без доп. списания
            event.setCanceled(true);
            return;
        }
        if (SpatialAnchor.tryConsume(player, SpatialAnchor.COST_KNOCKBACK)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onHurt(LivingHurtEvent event) {
        if (event.getEntityLiving().world.isRemote) return;
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (event.getSource() == null || event.getSource().getTrueSource() == null) return;
        Entity attacker = event.getSource().getTrueSource();
        if (!(attacker instanceof EntityIronGolem)) return;
        if (!SpatialAnchor.hasActiveAnchor(player)) return;
        if (SpatialAnchor.hasFlightAnchor(player)) {
            player.motionY = 0;
            player.motionX *= 0.1D;
            player.motionZ *= 0.1D;
            player.velocityChanged = true;
            return;
        }
        if (SpatialAnchor.tryConsume(player, SpatialAnchor.COST_KNOCKBACK)) {
            player.motionY = 0;
            player.motionX *= 0.1D;
            player.motionZ *= 0.1D;
            player.velocityChanged = true;
        }
    }

    // ------------------------------------------------------------------
    // Tick — snapshot
    // ------------------------------------------------------------------
    @SubscribeEvent
    public void onPlayerTickStart(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        EntityPlayer player = event.player;
        if (player == null || player.world.isRemote) return;
        if (!SpatialAnchor.hasActiveAnchor(player)) {
            PREV_POS.remove(player.getUniqueID());
            PREV_MOTION.remove(player.getUniqueID());
            // снимаем полёт если якорь выключен
            if (!player.capabilities.isCreativeMode && SpatialAnchor.hasFlightAnchor(player) == false) {
                // не трогаем если полёт уже выключен
            }
            return;
        }
        PREV_POS.put(player.getUniqueID(), new Vec3d(player.posX, player.posY, player.posZ));
        PREV_MOTION.put(player.getUniqueID(), new Vec3d(player.motionX, player.motionY, player.motionZ));

        // Включаем allowFlying если flight-mode активен
        if (SpatialAnchor.hasFlightAnchor(player)) {
            if (!player.capabilities.allowFlying) {
                player.capabilities.allowFlying = true;
                player.sendPlayerAbilities();
            }
            // flySpeed = скорость ходьбы
            float flySpeed = 0.05F;
            // ходьба 0.1, спринт 0.13 — переводим в flySpeed (vanilla 0.05 = ~0.1 walk)
            // Установим 0.1 для полёта чтобы совпадало с ходьбой
            float target = player.isSprinting() ? 0.13F : 0.10F;
            // flySpeed в capabilities это множитель, 0.05 даёт ~0.1 блок/тик, 0.1 даёт ~0.2 — подберём
            float fs = target * 0.5F;
            if (Math.abs(player.capabilities.getFlySpeed() - fs) > 0.001F) {
                player.capabilities.setFlySpeed(fs);
                player.sendPlayerAbilities();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTickEnd(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        EntityPlayer player = event.player;
        if (player == null || player.world.isRemote) return;
        if (!SpatialAnchor.hasActiveAnchor(player)) return;

        Vec3d prevPos = PREV_POS.get(player.getUniqueID());
        Vec3d prevMotion = PREV_MOTION.get(player.getUniqueID());
        if (prevPos == null || prevMotion == null) return;

        boolean flight = SpatialAnchor.hasFlightAnchor(player);

        if (flight) {
            // ---- Абсолютный якорь: полёт, 100 FE/tick ----
            if (!SpatialAnchor.tryConsume(player, SpatialAnchor.COST_FLIGHT)) {
                // кончилась энергия — выключаем полёт
                for (net.minecraft.item.ItemStack s : player.inventory.mainInventory) if (SpatialAnchor.isFlightEnabled(s)) { SpatialAnchor.setFlightEnabled(s, false); break; }
                for (net.minecraft.item.ItemStack s : player.inventory.offHandInventory) if (SpatialAnchor.isFlightEnabled(s)) SpatialAnchor.setFlightEnabled(s, false);
                for (net.minecraft.item.ItemStack s : player.inventory.armorInventory) if (SpatialAnchor.isFlightEnabled(s)) SpatialAnchor.setFlightEnabled(s, false);
                if (!player.capabilities.isCreativeMode) {
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                    player.sendPlayerAbilities();
                }
                return;
            }
            handleAbsoluteFlight(player, prevPos, prevMotion);
            // В полёте гравитация ЧД уже заблокирована в BlackHoleTileEntity, но дополнительно гасим внешние толчки
            // (абсолютный лок уже всё отменит)
        } else {
            // ---- Обычная ходьба: только упомянутые события ----
            handleNormalMode(player, prevPos, prevMotion);
        }
        player.fallDistance = 0;
    }

    // ------------------------------------------------------------------
    // Обычный режим — блокируем только течение/коллизии (гравитация уже в BH)
    // Стратегия дельта-фильтра: любая внешняя дельта сверх ввода — отменяется
    // ------------------------------------------------------------------
    private void handleNormalMode(EntityPlayer player, Vec3d prevPos, Vec3d prevMotion) {
        Vec3d curPos = new Vec3d(player.posX, player.posY, player.posZ);
        Vec3d delta = curPos.subtract(prevPos);

        boolean rawWantsMove = Math.abs(player.moveForward) > 0.01F || Math.abs(player.moveStrafing) > 0.01F;
        boolean isJumping = isJumping(player);
        boolean wantsVertical = isJumping || player.isSneaking();

        // Векторы внешних сил
        Vec3d flow = getFluidFlowVector(player);
        boolean inFlow = flow.lengthSquared() > 1e-7;
        boolean colliding = isColliding(player);

        // Надёжный wantsMove: если серверный moveForward ещё не синхронизирован, смотрим на дельту против течения
        boolean wantsMove = rawWantsMove;
        if (!wantsMove && inFlow) {
            Vec3d curPos2 = new Vec3d(player.posX, player.posY, player.posZ);
            Vec3d delta2 = curPos2.subtract(prevPos);
            // Если игрок сдвинулся против направления течения — значит пытается выбраться
            if (delta2.lengthSquared() > 0.0004) {
                double dot = delta2.x * flow.x + delta2.z * flow.z;
                if (dot < -1e-7) wantsMove = true;
                // Любой заметный сдвиг при наличии коллизии тоже считаем попыткой выйти
                if (colliding && delta2.lengthSquared() > 1e-6) wantsMove = true;
            }
        }

        long cost = 0;
        if (inFlow) cost += SpatialAnchor.COST_FLUID;
        if (colliding) cost += SpatialAnchor.COST_COLLISION;
        if (cost == 0) return;

        // Пробуем списать суммарную стоимость тика
        if (!SpatialAnchor.tryConsume(player, cost)) return;

        // Гасим внешние составляющие в motion/позиции
        if (inFlow) {
            if (!wantsMove) {
                // Стоим — держим позицию на месте, не плывём против течения
                player.motionX = 0;
                player.motionZ = 0;
                // Возвращаем к снапшоту по горизонтали
                player.setPosition(prevPos.x, player.posY, prevPos.z);
            } else {
                // Движемся — вычитаем только компоненту течения, оставляя ввод игрока
                player.motionX -= flow.x;
                player.motionZ -= flow.z;
            }
            if (!wantsVertical) {
                player.motionY -= flow.y;
                if (Math.abs(player.motionY) < 0.05 && player.isInWater()) player.motionY = 0;
            }
        }
        if (colliding) {
            // Обнуляем толчок соседей
            World world = player.world;
            AxisAlignedBB aabb = player.getEntityBoundingBox().grow(0.45, 0.15, 0.45);
            List<Entity> nearby = world.getEntitiesWithinAABBExcludingEntity(player, aabb);
            for (Entity e : nearby) {
                if (e == null || e.isDead) continue;
                if (!e.canBePushed()) continue;
                if (e instanceof EntityPlayer && ((EntityPlayer) e).isSpectator()) continue;
                e.motionX *= 0.0D;
                e.motionZ *= 0.0D;
                e.velocityChanged = true;
            }
            if (!wantsMove) {
                player.motionX = 0;
                player.motionZ = 0;
            } else {
                // Сохраняем ввод, убираем резкий толчок сверх prev
                double dx = player.motionX - prevMotion.x;
                double dz = player.motionZ - prevMotion.z;
                if (Math.abs(dx) > 0.04) player.motionX = prevMotion.x * 0.85D + player.motionX * 0.15D;
                if (Math.abs(dz) > 0.04) player.motionZ = prevMotion.z * 0.85D + player.motionZ * 0.15D;
            }
        }
        // Корректируем позицию если дельта была явно внешней (без ввода но сдвиг >0.01)
        if (!wantsMove && !wantsVertical) {
            double d2 = delta.x * delta.x + delta.z * delta.z;
            if (d2 > 0.0001 && !inFlow) { // flow уже скомпенсирован, остальной сдвиг — коллизия
                player.setPosition(prevPos.x, player.posY, prevPos.z);
            }
        }
        player.velocityChanged = true;
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).connection.sendPacket(new net.minecraft.network.play.server.SPacketEntityVelocity(player));
        }
    }

    // ------------------------------------------------------------------
    // Абсолютный полёт
    // ------------------------------------------------------------------
    private void handleAbsoluteFlight(EntityPlayer player, Vec3d prevPos, Vec3d prevMotion) {
        // Включаем полёт
        if (!player.capabilities.isFlying) {
            player.capabilities.isFlying = true;
            player.sendPlayerAbilities();
        }
        player.fallDistance = 0;

        // Скорость как ходьба
        float walkSpeed = player.isSprinting() ? 0.13F : 0.10F;
        // Вычисляем ввод
        float fwd = player.moveForward;
        float strafe = player.moveStrafing;
        boolean jump = isJumping(player);
        boolean sneak = player.isSneaking();
        boolean wantsMove = Math.abs(fwd) > 0.01F || Math.abs(strafe) > 0.01F || jump || sneak;

        // Если нет ввода — зависаем (гасим любой дрейф)
        if (!wantsMove) {
            // Полный лок — отменяем любое смещение
            Vec3d curPos = new Vec3d(player.posX, player.posY, player.posZ);
            Vec3d delta = curPos.subtract(prevPos);
            if (delta.lengthSquared() > 1e-6) {
                // Списываем полёт уже списали, теперь просто возвращаем
                player.setPosition(prevPos.x, prevPos.y, prevPos.z);
                player.motionX = 0;
                player.motionY = 0;
                player.motionZ = 0;
                player.velocityChanged = true;
            } else {
                player.motionX *= 0.0D;
                player.motionY *= 0.0D;
                player.motionZ *= 0.0D;
            }
            // Также гасим соседей
            dampenNearby(player);
            return;
        }

        // Есть ввод — формируем полётную скорость вручную (перекрываем ваниль чтобы точно 0.1)
        double yaw = player.rotationYaw * 0.017453292F;
        double sin = Math.sin(yaw);
        double cos = Math.cos(yaw);
        double mx = (strafe * cos - fwd * sin) * walkSpeed;
        double mz = (fwd * cos + strafe * sin) * walkSpeed;
        double my = 0;
        if (jump && !sneak) my = walkSpeed * 0.7D;
        else if (sneak && !jump) my = -walkSpeed * 0.7D;
        else my = player.motionY * 0.0D; // в полёте зависаем по Y если не жмём

        // Применяем только вводную скорость — внешние толчки игнорируются
        player.motionX = mx;
        player.motionZ = mz;
        player.motionY = my;
        player.velocityChanged = true;
        dampenNearby(player);
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).connection.sendPacket(new net.minecraft.network.play.server.SPacketEntityVelocity(player));
        }
    }

    private void dampenNearby(EntityPlayer player) {
        World world = player.world;
        AxisAlignedBB aabb = player.getEntityBoundingBox().grow(0.5, 0.5, 0.5);
        List<Entity> nearby = world.getEntitiesWithinAABBExcludingEntity(player, aabb);
        for (Entity e : nearby) {
            if (e == null || e.isDead) continue;
            if (!e.canBePushed()) continue;
            e.motionX *= 0.0D;
            e.motionZ *= 0.0D;
            e.velocityChanged = true;
        }
    }

    private static boolean isJumping(EntityPlayer player) {
        try {
            java.lang.reflect.Field f = EntityLivingBase.class.getDeclaredField("isJumping");
            f.setAccessible(true);
            return f.getBoolean(player);
        } catch (Exception e) {
            try {
                java.lang.reflect.Field f2 = EntityLivingBase.class.getDeclaredField("field_70703_a");
                f2.setAccessible(true);
                return f2.getBoolean(player);
            } catch (Exception e2) {
                return false;
            }
        }
    }

    private static boolean isFlowingFluidPush(EntityPlayer player) {
        Vec3d flow = getFluidFlowVector(player);
        return flow.lengthSquared() > 1e-7;
    }

    private static Vec3d getFluidFlowVector(EntityPlayer player) {
        World world = player.world;
        AxisAlignedBB bb = player.getEntityBoundingBox().grow(0.3, 0.2, 0.3);
        int minX = (int) Math.floor(bb.minX);
        int maxX = (int) Math.floor(bb.maxX);
        int minY = (int) Math.floor(bb.minY);
        int maxY = (int) Math.floor(bb.maxY);
        int minZ = (int) Math.floor(bb.minZ);
        int maxZ = (int) Math.floor(bb.maxZ);
        Vec3d flow = new Vec3d(0, 0, 0);
        int count = 0;
        for (int x = minX; x <= maxX; x++) for (int y = minY; y <= maxY; y++) for (int z = minZ; z <= maxZ; z++) {
            BlockPos pos = new BlockPos(x, y, z);
            IBlockState st = world.getBlockState(pos);
            if (st.getMaterial() != Material.WATER && st.getMaterial() != Material.LAVA) continue;
            try {
                Integer lvl = st.getValue(BlockLiquid.LEVEL);
                if (lvl != null && lvl != 0) {
                    for (net.minecraft.util.EnumFacing f : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
                        BlockPos n = pos.offset(f);
                        IBlockState ns = world.getBlockState(n);
                        if (ns.getMaterial() == st.getMaterial()) {
                            try {
                                Integer nl = ns.getValue(BlockLiquid.LEVEL);
                                if (nl != null && nl > lvl) {
                                    flow = flow.add(new Vec3d(f.getFrontOffsetX() * 0.014D, 0, f.getFrontOffsetZ() * 0.014D));
                                } else if (nl != null && nl < lvl) {
                                    flow = flow.add(new Vec3d(-f.getFrontOffsetX() * 0.014D, 0, -f.getFrontOffsetZ() * 0.014D));
                                }
                            } catch (Exception ignored) {}
                        }
                    }
                    count++;
                }
            } catch (Exception ignored) {}
        }
        if (count == 0) return new Vec3d(0, 0, 0);
        return new Vec3d(flow.x / Math.max(1, count), flow.y / Math.max(1, count), flow.z / Math.max(1, count));
    }

    private static boolean isColliding(EntityPlayer player) {
        World world = player.world;
        AxisAlignedBB aabb = player.getEntityBoundingBox().grow(0.45, 0.05, 0.45);
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player, aabb);
        for (Entity e : list) {
            if (e == null || e.isDead) continue;
            if (e instanceof EntityPlayer && ((EntityPlayer) e).isSpectator()) continue;
            if (!e.canBePushed()) continue;
            return true;
        }
        return false;
    }
}
