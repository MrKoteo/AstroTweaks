package astrotweaks.item.SpatialAnchor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SpatialAnchorEvents {

    /** Позиция/движение хранятся как double[3] — без аллокаций Vec3d каждый тик. */
    private static final Map<UUID, double[]> PREV_POS    = new ConcurrentHashMap<>();
    private static final Map<UUID, double[]> PREV_MOTION = new ConcurrentHashMap<>();


    /** Кэш reflection-поля isJumping — резолвится один раз при загрузке класса. */
    private static final java.lang.reflect.Field JUMP_FIELD;
    static {
        java.lang.reflect.Field f = null;
        try {
            f = EntityLivingBase.class.getDeclaredField("isJumping");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            try {
                f = EntityLivingBase.class.getDeclaredField("field_70703_a");
                f.setAccessible(true);
            } catch (NoSuchFieldException ignored) {
                // останется null — isJumping вернёт false
            }
        }
        JUMP_FIELD = f;
    }

    // ------------------------------------------------------------------
    // Knockback — оба режима
    // ------------------------------------------------------------------
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onKnockBack(LivingKnockBackEvent event) {
        if (event.getEntityLiving().world.isRemote) return;
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();

        int flags = SpatialAnchor.scanPlayerAnchors(player, null);
        if ((flags & SpatialAnchor.FLAG_ACTIVE) == 0) return;
        if ((flags & SpatialAnchor.FLAG_FLIGHT) != 0) {
            // В полёте COST_FLIGHT уже покрывает всё — блокируем без доп. списания
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

        int flags = SpatialAnchor.scanPlayerAnchors(player, null);
        if ((flags & SpatialAnchor.FLAG_ACTIVE) == 0) return;

        if ((flags & SpatialAnchor.FLAG_FLIGHT) != 0) {
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
        if (player.world.isRemote) return;

        int flags = SpatialAnchor.scanPlayerAnchors(player, null);
        if ((flags & SpatialAnchor.FLAG_ACTIVE) == 0) {
            UUID id = player.getUniqueID();
            PREV_POS.remove(id);
            PREV_MOTION.remove(id);
            // якорь выключен / сел — снимем team (оптимизированно, идемпотентно)
            SpatialAnchorTeams.ensureRemoved(player);
            return;
        }

        SpatialAnchor.tickPassiveDrain(player);

        UUID id = player.getUniqueID();

        double[] p = PREV_POS.get(id);
        if (p == null) { p = new double[3]; PREV_POS.put(id, p); }
        p[0] = player.posX; p[1] = player.posY; p[2] = player.posZ;

        double[] m = PREV_MOTION.get(id);
        if (m == null) { m = new double[3]; PREV_MOTION.put(id, m); }
        m[0] = player.motionX; m[1] = player.motionY; m[2] = player.motionZ;

        // Включаем allowFlying если flight-mode активен
        if ((flags & SpatialAnchor.FLAG_FLIGHT) != 0) {
            if (!player.capabilities.allowFlying) {
                player.capabilities.allowFlying = true;
                player.sendPlayerAbilities();
            }
            float target = player.isSprinting() ? 0.10F : 0.07F;
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
        if (player.world.isRemote) return;

        long[] total = new long[1];
        int flags = SpatialAnchor.scanPlayerAnchors(player, total);
        if ((flags & SpatialAnchor.FLAG_ACTIVE) == 0) {
            SpatialAnchorTeams.ensureRemoved(player);
            return;
        }

        double[] prevPos = PREV_POS.get(player.getUniqueID());
        if (prevPos == null) return;

        boolean flight = (flags & SpatialAnchor.FLAG_FLIGHT) != 0;

        if (flight) {
            // ---- Абсолютный якорь: полёт, COST_FLIGHT/tick ----
            if (!SpatialAnchor.tryConsume(player, SpatialAnchor.COST_FLIGHT, total[0])) {
                // кончилась энергия — выключаем полёт
                for (ItemStack s : player.inventory.mainInventory)
                    if (SpatialAnchor.isFlightEnabled(s)) { SpatialAnchor.setFlightEnabled(s, false); break; }
                for (ItemStack s : player.inventory.offHandInventory)
                    if (SpatialAnchor.isFlightEnabled(s)) SpatialAnchor.setFlightEnabled(s, false);
                for (ItemStack s : player.inventory.armorInventory)
                    if (SpatialAnchor.isFlightEnabled(s)) SpatialAnchor.setFlightEnabled(s, false);
                if (!player.capabilities.isCreativeMode) {
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                    player.sendPlayerAbilities();
                }
                // полёт слетел — коллизию теперь обрабатываем как обычный режим.
                // total[] устарел (полёт выключен, энергия ещё могла остаться),
                // поэтому в обычном режиме коллизии списываем без knownTotal.
                flight = false;
            } else {
                handleAbsoluteFlight(player, prevPos);
                // коллизии в полёте покрыты COST_FLIGHT — команда без доп. списания
                SpatialAnchorTeams.ensureInTeam(player);
                player.fallDistance = 0;
                return;
            }
        }

        // ---- Обычный режим: коллизии через scoreboard team ----
        updateCollisionTeam(player);
        player.fallDistance = 0;
    }


    // ------------------------------------------------------------------
    // Коллизии через Team — оптимизированно
    // ------------------------------------------------------------------
    private void updateCollisionTeam(EntityPlayer player) {
        // В полёте этот метод не вызывается (return выше), здесь только обычный режим
        boolean colliding = isColliding(player);
        if (colliding) {
            // Пытаемся списать COST_COLLISION, иначе снимаем защиту
            if (!SpatialAnchor.tryConsume(player, SpatialAnchor.COST_COLLISION)) {
                SpatialAnchorTeams.ensureRemoved(player);
                return;
            }
        }
        // Есть энергия (или нет соседей) — держим в no_collision команде.
        // ensureInTeam идемпотентно и кэширует Scoreboard→Team, не дёргает scoreboard лишний раз
        SpatialAnchorTeams.ensureInTeam(player);
    }

    // ------------------------------------------------------------------
    // Абсолютный полёт
    // ------------------------------------------------------------------
    private void handleAbsoluteFlight(EntityPlayer player, double[] prevPos) {
        if (!player.capabilities.isFlying) {
            player.capabilities.isFlying = true;
            player.sendPlayerAbilities();
        }
        player.fallDistance = 0;

        float walkSpeed = player.isSprinting() ? 0.10F : 0.07F;
        float fwd = player.moveForward;
        float strafe = player.moveStrafing;
        boolean jump = isJumping(player);
        boolean sneak = player.isSneaking();
        boolean wantsMove = Math.abs(fwd) > 0.01F || Math.abs(strafe) > 0.01F || jump || sneak;

        if (!wantsMove) {
            double dx = player.posX - prevPos[0];
            double dy = player.posY - prevPos[1];
            double dz = player.posZ - prevPos[2];
            if (dx * dx + dy * dy + dz * dz > 1e-6) {
                player.setPosition(prevPos[0], prevPos[1], prevPos[2]);
                player.motionX = 0;
                player.motionY = 0;
                player.motionZ = 0;
                player.velocityChanged = true;
            } else {
                player.motionX = 0;
                player.motionY = 0;
                player.motionZ = 0;
            }
            return;
        }

        double yaw = player.rotationYaw * 0.017453292F;
        double sin = Math.sin(yaw);
        double cos = Math.cos(yaw);
        double mx = (strafe * cos - fwd * sin) * walkSpeed;
        double mz = (fwd * cos + strafe * sin) * walkSpeed;
        double my;
        if (jump && !sneak) my = walkSpeed * 0.7D;
        else if (sneak && !jump) my = -walkSpeed * 0.7D;
        else my = 0;

        player.motionX = mx;
        player.motionZ = mz;
        player.motionY = my;
        player.velocityChanged = true;
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).connection.sendPacket(new net.minecraft.network.play.server.SPacketEntityVelocity(player));
        }
    }

    // ------------------------------------------------------------------
    // Logout / dimension — корректно снимаем команду и чистим кэши
    // ------------------------------------------------------------------
    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player.world.isRemote) return;
        SpatialAnchorTeams.onPlayerLogout((EntityPlayer) event.player);
        UUID id = event.player.getUniqueID();
        PREV_POS.remove(id);
        PREV_MOTION.remove(id);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player.world.isRemote) return;
        // на входной тик всё равно пересоздадим, но если игрок офлайн остался в нашей команде
        // без энергии — сразу снимем, чтобы не висел зря
        EntityPlayer p = (EntityPlayer) event.player;
        if ((SpatialAnchor.scanPlayerAnchors(p, null) & SpatialAnchor.FLAG_ACTIVE) == 0) {
            SpatialAnchorTeams.ensureRemoved(p);
        }
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.player.world.isRemote) return;
        EntityPlayer p = (EntityPlayer) event.player;
        // scoreboard глобальный, но на всякий — пересоздадим кэш для нового мира
        if ((SpatialAnchor.scanPlayerAnchors(p, null) & SpatialAnchor.FLAG_ACTIVE) == 0) {
            SpatialAnchorTeams.ensureRemoved(p);
        } else {
            // форсим перепривязку к команде нового мира (getOrCreateTeam закэширует новый Scoreboard)
            SpatialAnchorTeams.ensureInTeam(p);
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.player.world.isRemote) return;
        EntityPlayer p = (EntityPlayer) event.player;
        if ((SpatialAnchor.scanPlayerAnchors(p, null) & SpatialAnchor.FLAG_ACTIVE) == 0) {
            SpatialAnchorTeams.ensureRemoved(p);
        }
        UUID id = p.getUniqueID();
        PREV_POS.remove(id);
        PREV_MOTION.remove(id);
    }

    private static boolean isJumping(EntityPlayer player) {
        if (JUMP_FIELD == null) return false;
        try {
            return JUMP_FIELD.getBoolean(player);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isColliding(EntityPlayer player) {
        // Лёгкий скан — только pushable сущности рядом (0.45×0.05×0.45), без аллокаций вне цикла
        AxisAlignedBB aabb = player.getEntityBoundingBox().grow(0.45, 0.05, 0.45);
        List<Entity> list = player.world.getEntitiesWithinAABBExcludingEntity(player, aabb);
        for (int i = 0, n = list.size(); i < n; i++) {
            Entity e = list.get(i);
            if (e == null || e.isDead) continue;
            if (e instanceof EntityPlayer && ((EntityPlayer) e).isSpectator()) continue;
            if (!e.canBePushed()) continue;
            return true;
        }
        return false;
    }
}
