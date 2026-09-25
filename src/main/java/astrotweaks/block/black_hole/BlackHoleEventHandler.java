package astrotweaks.block.black_hole;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;



@Mod.EventBusSubscriber(modid = "astrotweaks")
public class BlackHoleEventHandler {

    /**
     * Generous pre-filter radius for per-block events: block capture range plus
     * region granularity margin. Far holes skip the map lookup entirely.
     */
    private static final double BLOCK_EVENT_RANGE = BlackHoleUtils.MAX_BLOCK_CAPTURE_RANGE + 24.0D;
    private static final double BLOCK_EVENT_RANGE_SQ = BLOCK_EVENT_RANGE * BLOCK_EVENT_RANGE;

    private static boolean inBlockEventRange(BlackHoleTileEntity bh, BlockPos pos) {
        double dx = (pos.getX() + 0.5) - (bh.getPos().getX() + 0.5);
        double dy = (pos.getY() + 0.5) - (bh.getPos().getY() + 0.5);
        double dz = (pos.getZ() + 0.5) - (bh.getPos().getZ() + 0.5);
        return dx * dx + dy * dy + dz * dz <= BLOCK_EVENT_RANGE_SQ;
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.PlaceEvent event) {
        World world = event.getWorld();
        if (world == null || world.isRemote) return;
        java.util.Set<BlackHoleTileEntity> active = BlackHoleTileEntity.getActiveHoles();
        if (active.isEmpty()) return;
        BlockPos pos = event.getPos();
        // Прямая итерация CHM-множества: weakly-consistent, без CME и без копии.
        for (BlackHoleTileEntity bh : active) {
            if (bh.isInvalid() || bh.getWorld() != world) continue;
            if (!inBlockEventRange(bh, pos)) continue;
            bh.getRegionManager().onBlockPlaced(pos);
        }
    }

    @SubscribeEvent
    public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        World world = event.getWorld();
        if (world == null || world.isRemote) return;
        java.util.Set<BlackHoleTileEntity> active = BlackHoleTileEntity.getActiveHoles();
        if (active.isEmpty()) return;

        // Fire for liquids and vegetation (tallgrass, flowers, bushes, vine etc.).
        // Covers: bucket place (setBlockState → notify), water spreading, and vegetation growth.
        IBlockState state = world.getBlockState(event.getPos());
        if (!state.getMaterial().isLiquid() && !BlackHoleRegionManager.isVegetation(state)) return;

        BlockPos pos = event.getPos();
        for (BlackHoleTileEntity bh : active) {
            if (bh.isInvalid() || bh.getWorld() != world) continue;
            if (!inBlockEventRange(bh, pos)) continue;
            bh.getRegionManager().onBlockPlaced(pos);
        }
    }

    /**
     * BH-vs-BH mass tug, once per world tick (not per hole, to stay O(holes^2)).
     * Every hole drains TUG_RATE * mass * (1 + grav) per tick from each other
     * hole inside its own gravity range; the drained amount is credited to the
     * drainer. Both directions are evaluated, so a smaller hole caught by a
     * bigger one loses net mass with acceleration until it is pinned at the
     * floor. Masses are re-read live, so the snowball effect applies within
     * the same tick; order between holes does not matter for the outcome.
     * Passive deltas (no forced sync): the 20-tick TE sync covers the drift.
     */
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        World world = event.world;
        if (world == null || world.isRemote) return;
        java.util.Set<BlackHoleTileEntity> active = BlackHoleTileEntity.getActiveHoles();
        if (active.size() < 2) return;

        List<BlackHoleTileEntity> holes = null;
        for (BlackHoleTileEntity bh : active) {
            if (bh.isInvalid() || bh.getWorld() != world) continue;
            // Stale-экземпляр после выгрузки чанка: позицию уже занял другой TE.
            // Чистим только при доказанной замене (другой non-null TE), отсутствие
            // записи (null) — переходное состояние загрузки, не трогаем.
            TileEntity current = world.getTileEntity(bh.getPos());
            if (current != null && current != bh) {
                active.remove(bh);
                continue;
            }
            if (holes == null) holes = new ArrayList<>();
            holes.add(bh);
        }
        if (holes == null || holes.size() < 2) return;

        final double minMass = BlackHoleUtils.MIN_MASS;
        for (int i = 0; i < holes.size(); i++) {
            BlackHoleTileEntity a = holes.get(i);
            if (a.isInvalid()) continue;
            double massA = a.getMass();
            if (!(massA > minMass)) continue; // floored hole pulls nothing worth moving
            double rangeA = BlackHoleUtils.getGravityRange(massA);
            double ax = a.getPos().getX() + 0.5;
            double ay = a.getPos().getY() + 0.5;
            double az = a.getPos().getZ() + 0.5;
            for (int j = 0; j < holes.size(); j++) {
                if (j == i) continue;
                BlackHoleTileEntity b = holes.get(j);
                if (b.isInvalid()) continue;
                double dx = (b.getPos().getX() + 0.5) - ax;
                double dy = (b.getPos().getY() + 0.5) - ay;
                double dz = (b.getPos().getZ() + 0.5) - az;
                double distSq = dx * dx + dy * dy + dz * dz;
                // Одна и та же клетка = одна и та же дыра: дубликат регистрации
                // (stale после выгрузки) никогда не образует легитимную пару.
                // Без этого tug на dist ~ 0 даёт смертельный дренаж за секунды.
                if (distSq < 1e-6) continue;
                if (distSq > rangeA * rangeA) continue; // outside A's influence
                double grav = BlackHoleUtils.getAccelerationSq(massA, distSq);
                double take = BlackHoleUtils.TUG_RATE * massA * (1.0D + grav);
                if (!(take > 1e-9D)) continue; // noise guard, avoids dirty-churn
                double avail = b.getMass() - minMass;
                if (!(avail > 0.0D)) {
                    // Жертва уже на полу — съедена до конца, сносим ядро
                    b.destroyBlackHole();
                    continue;
                }
                if (take > avail) take = avail;
                if (!(take > 0.0D)) continue;
                b.addMassPassive(-take);
                a.addMassPassive(take);
                // Если после слива жертва дошла до порога — уничтожаем ядро
                if (!b.isInvalid() && b.getMass() <= minMass) {
                    b.destroyBlackHole();
                }
                // Refresh live: growth widens the range for the next victim.
                massA = a.getMass();
                rangeA = BlackHoleUtils.getGravityRange(massA);
            }
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        World world = event.getWorld();
        if (world == null || world.isRemote) return;
        java.util.Set<BlackHoleTileEntity> active = BlackHoleTileEntity.getActiveHoles();
        if (active.isEmpty()) return;
        Chunk chunk = event.getChunk();
        for (BlackHoleTileEntity bh : active) {
            if (bh.isInvalid() || bh.getWorld() != world) continue;
            bh.getRegionManager().onChunkLoaded(chunk);
        }
    }
}
