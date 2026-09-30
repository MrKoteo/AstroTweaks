package astrotweaks.block.black_hole.client;

import astrotweaks.block.black_hole.BlackHoleTileEntity;
import astrotweaks.block.black_hole.BlackHoleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Set;

/**
 * Локальная тяга чёрной дыры для себя в жидкости.
 *
 * <p>Проблема: сервер пешего игрока не знает его гребок (прыжок/ввод едут
 * на сервер только верхом), поэтому серверный motion — тонущий, а клиентский —
 * плывущий. Любой абсолютный {@code SPacketEntityVelocity} себе каждый тик
 * сносит swim-импульс воды (+0.04 вверх). Путей два: прямой sendPacket и
 * {@code EntityTrackerEntry} через {@code velocityChanged=true}
 * ({@code sendToTrackingAndSelf}). Поэтому для пловца сервер не шлёт ни того,
 * ни другого (см. {@code BlackHoleTileEntity.tickEntities}), а та же тяга
 * применяется здесь, ДО ванильного {@code travel()} в этом же тике: она
 * складывается с гребком и проходит штатное трение воды — жидкость работает
 * по своей логике, плюс притяжение BH с той же силой и по всем осям XYZ.
 *
 * <p>Перф: только локальный игрок, только в жидкости, ранний отсев по
 * квадрату дистанции до sqrt/pow, ноль аллокаций в цикле по дырам.
 */
@Mod.EventBusSubscriber(modid = "astrotweaks", value = Side.CLIENT)
public final class BlackHoleGravityClientHandler {

    private BlackHoleGravityClientHandler() {}

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.isGamePaused()) return;
        EntityPlayerSP player = mc.player;
        net.minecraft.world.World world = mc.world;
        if (player == null || world == null) return;
        if (player.isSpectator() || player.isCreative()) return;
        // Тот же предикат, что на сервере: флаг воды, иначе точечная проба блока.
        if (!BlackHoleUtils.isInLiquid(player)) return;

        Set<BlackHoleTileEntity> active = BlackHoleTileEntity.getActiveHoles();
        if (active.isEmpty()) return;

        // Тот же anchor, что на сервере: центр тела.
        double x = player.posX;
        double y = player.posY + player.height * 0.5D;
        double z = player.posZ;

        // Скан инвентаря якоря (~41 слот с NBT) — только если хоть одна дыра
        // в радиусе: обычное плавание вне зоны BH его вообще не платит.
        // Состояние якоря константно в пределах тика — проверяем один раз.
        boolean anchorChecked = false;

        boolean pulled = false;
        for (BlackHoleTileEntity bh : active) {
            if (bh.isInvalid() || bh.getWorld() != world) continue;
            BlockPos p = bh.getPos();
            if (!world.isBlockLoaded(p)) continue;
            double m = bh.getMass();

            double dx = (p.getX() + 0.5D) - x;
            double dy = (p.getY() + 0.5D) - y;
            double dz = (p.getZ() + 0.5D) - z;
            double distSq = dx * dx + dy * dy + dz * dz;

            // Дешёвый отсев до корня: за gravRange тяги всё равно нет
            // (MIN_ACCEL по определению равен getGravityRange).
            double gravRange = BlackHoleUtils.getGravityRange(m);
            if (distSq > gravRange * gravRange) continue;

            if (!anchorChecked) {
                anchorChecked = true;
                // Якорь блокирует тягу на сервере за FE — локально тоже не тянем.
                // Только проверка, без списания (списание — сервер, иначе двойная трата).
                try {
                    if (astrotweaks.item.SpatialAnchor.SpatialAnchor.hasActiveAnchor(player)) return;
                } catch (Exception ignored) {}
            }

            double accel = BlackHoleUtils.getAccelerationSq(m, distSq);

            // Near-horizon boost — дословно как на сервере.
            double horizon = BlackHoleUtils.getHorizonRadius(m);
            double boostRadius = BlackHoleUtils.getBoostRadius(m);
            double boostOuter = horizon + boostRadius;
            if (boostRadius > 0 && distSq > horizon * horizon && distSq < boostOuter * boostOuter) {
                double dist = Math.sqrt(distSq);
                double t = (boostOuter - dist) / boostRadius;
                double t3 = t * t * t;
                accel *= (1.0D + BlackHoleUtils.BOOST_MAX * t3);
            }
            if (accel < BlackHoleUtils.MIN_ACCEL) continue;

            double dist = Math.sqrt(distSq);
            if (dist < 0.05D) dist = 0.05D;

            // Формула дословно серверная (stride игрока = 1.0).
            double maxAccel = Math.min(accel, dist * BlackHoleUtils.DIST_PULL_CAP_FACTOR);
            if (maxAccel > BlackHoleUtils.MAX_ACCEL_PER_TICK) maxAccel = BlackHoleUtils.MAX_ACCEL_PER_TICK;

            double nx = dx / dist, ny = dy / dist, nz = dz / dist;
            player.motionX += nx * maxAccel * BlackHoleUtils.MOTION_FACTOR;
            player.motionY += ny * maxAccel * BlackHoleUtils.MOTION_FACTOR;
            player.motionZ += nz * maxAccel * BlackHoleUtils.MOTION_FACTOR;
            pulled = true;
        }

        if (pulled) {
            // Клэмп через квадрат: sqrt только когда потолок реально пробит.
            double speedSq = player.motionX * player.motionX
                    + player.motionY * player.motionY + player.motionZ * player.motionZ;
            if (speedSq > BlackHoleUtils.MAX_SPEED_SQ) {
                double s = BlackHoleUtils.MAX_SPEED / Math.sqrt(speedSq);
                player.motionX *= s;
                player.motionY *= s;
                player.motionZ *= s;
            }
        }
    }
}
