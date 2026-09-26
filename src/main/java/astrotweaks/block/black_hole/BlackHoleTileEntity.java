package astrotweaks.block.black_hole;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;



public class BlackHoleTileEntity extends TileEntity implements ITickable {

    public static final String TAG_MASS = "mass";

    /** Глобальный реестр активных BH — O(число BH) вместо O(все TE) в ивентах.
     * Strong set, но время жизни записей привязано к чанку: validate/onLoad
     * добавляют, invalidate/onChunkUnload удаляют. Без удаления на выгрузке
     * stale-экземпляры накапливались бы и участвовали в BH-vs-BH tug как
     * фантомные вторые дыры в той же точке (dist ~ 0 -> смертельный дренаж). */
    private static final java.util.Set<BlackHoleTileEntity> ACTIVE_HOLES =
            java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap<>());

    public static java.util.Set<BlackHoleTileEntity> getActiveHoles() { return ACTIVE_HOLES; }

    private double mass = BlackHoleUtils.DEFAULT_MASS;
    private int syncCooldown = 0;
    private int nbtCooldown = 0;
    private double lastPersistedMass = Double.NaN;
    private double lastSyncedMass = Double.NaN;

    private final BlackHoleRegionManager regionManager = new BlackHoleRegionManager(this);

    public BlackHoleRegionManager getRegionManager() { return regionManager; }

    public double getMass() { return mass; }

    public void setMass(double m) {
        double clamped = BlackHoleUtils.clampMass(m);
        this.mass = clamped;
        markDirty();
        lastPersistedMass = clamped;
        lastSyncedMass = clamped;
        nbtCooldown = BlackHoleUtils.getNbtInterval(clamped);
        syncCooldown = 0;
    }

    public void addMass(double delta) {
        if (delta == 0.0D) return;
        // Блочное поедание — суммированная дельта за тик, форсим NBT/sync через общий механизм,
        // но не каждый вызов, чтобы не спамить при 1024 блоках/тик
        this.mass = BlackHoleUtils.clampMass(this.mass + delta);
        // не вызываем markDirty/sync здесь — update() сделает адаптивно
    }

    /**
     * Continuous per-tick delta (evaporation, BH-vs-BH tug). Clamped like
     * setMass, but does NOT force an immediate client sync/NBT — адаптивные интервалы.
     */
    public void addMassPassive(double delta) {
        if (delta == 0.0D) return;
        this.mass = BlackHoleUtils.clampMass(this.mass + delta);
        // адаптивная синхронизация/NBT в update()
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey(TAG_MASS)) {
            this.mass = nbt.getDouble(TAG_MASS);
        }
        if (nbt.hasKey("Mass")) {
            this.mass = nbt.getDouble("Mass");
        }
        this.mass = BlackHoleUtils.clampMass(this.mass);
        lastPersistedMass = this.mass;
        lastSyncedMass = this.mass;
        nbtCooldown = BlackHoleUtils.getNbtInterval(this.mass);
        syncCooldown = BlackHoleUtils.getSyncInterval(this.mass);
        // syncCooldown не трогаем — периодический sync сам подхватит новую массу
        // (setMass форсит sync=0 отдельно)
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setDouble(TAG_MASS, mass);
        return nbt;
    }
    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
        if (world != null) world.markBlockRangeForRenderUpdate(pos, pos);
    }
    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        readFromNBT(tag);
    }

    @Override
    public void validate() {
        super.validate();
        ACTIVE_HOLES.add(this);
    }

    /**
     * Forge зовёт onLoad и при установке блока, и при загрузке чанка с диска
     * (validate на дисковом пути тоже вызывается, но дублирование безопасно —
     * set семантика). Страховка на случай путей регистрации без validate.
     */
    @Override
    public void onLoad() {
        ACTIVE_HOLES.add(this);
    }

    @Override
    public void invalidate() {
        ACTIVE_HOLES.remove(this);
        super.invalidate();
    }

    @Override
    public void onChunkUnload() {
        // Выгрузка чанка НЕ инвалидирует TE в ванили — без этого stale-экземпляр
        // навсегда оставался бы в ACTIVE_HOLES (strong set) и после перезагрузки
        // чанка воевал бы со свежим экземпляром в BH-vs-BH tug на дистанции ~0.
        ACTIVE_HOLES.remove(this);
        // Форсим сохранение последних пассивных изменений перед выгрузкой, чтобы не потерять массу
        if (!world.isRemote && Double.doubleToLongBits(mass) != Double.doubleToLongBits(lastPersistedMass)) {
            markDirty();
            lastPersistedMass = mass;
        }
        super.onChunkUnload();
    }

    // Кэш для getRenderBoundingBox — избегаем 2 pow + аллокацию AABB каждый кадр
    private double cachedBBMass = Double.NaN;
    private AxisAlignedBB cachedBB;
    // Кэш для tickEntities AABB
    private double cachedGravRange = Double.NaN;
    private AxisAlignedBB cachedAABB;

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        double m = mass;
        if (m != cachedBBMass || cachedBB == null) {
            cachedBBMass = m;
            double h = BlackHoleUtils.getVisualHorizonRadius(m);
            double t = BlackHoleUtils.getHaloThickness(h);
            double rad = Math.max(h + t * 2 + 1.0D, 2.0D) + 1.0D;
            cachedBB = new AxisAlignedBB(pos).grow(rad, rad, rad);
        }
        return cachedBB;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0 || pass == 1;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return super.getMaxRenderDistanceSquared();
    }

    /**
     * Уничтожает ядро чёрной дыры (блок + TE). Безопасен при повторном вызове.
     * Вызывать только на сервере.
     */
    public void destroyBlackHole() {
        if (world == null || world.isRemote || isInvalid()) return;
        // setBlockToAir -> BlackHoleBlock.breakBlock -> world.removeTileEntity(pos) -> invalidate()
        world.setBlockToAir(pos);
    }

    // =================================================================
    // Server tick - gravity & block eating (region-driven)
    // =================================================================
    @Override
    public void update() {
        if (world == null || world.isRemote) return;

        if (Double.isNaN(lastPersistedMass)) {
            lastPersistedMass = mass;
            nbtCooldown = BlackHoleUtils.getNbtInterval(mass);
        }
        if (Double.isNaN(lastSyncedMass)) {
            lastSyncedMass = mass;
            if (syncCooldown <= 0) syncCooldown = BlackHoleUtils.getSyncInterval(mass);
        }

        // Смерть от испарения/поглощения: если масса дошла до пола — сносим ядро.
        if (mass <= BlackHoleUtils.MIN_MASS) {
            destroyBlackHole();
            return;
        }

        // Evaporation: slow mass bleed, weaker for heavier holes.
        double m = mass;
        if (m > BlackHoleUtils.MIN_MASS) {
            double evap = BlackHoleUtils.getEvaporationPerTick(m);
            if (evap > 0.0D) {
                addMassPassive(-evap);
                if (mass <= BlackHoleUtils.MIN_MASS) {
                    destroyBlackHole();
                    return;
                }
            }
        }

        // Entities: split across 2 ticks by entity-id parity.
        tickEntities();

        // Blocks: budgeted, region-driven.
        regionManager.tick();

        // --- Адаптивная синхронизация: чем больше масса — тем реже пакеты, но при быстром изменении — чаще ---
        boolean massChanged = Double.doubleToLongBits(mass) != Double.doubleToLongBits(lastSyncedMass);
        double syncRel = 0;
        if (massChanged) {
            syncRel = Math.abs(mass - lastSyncedMass) / Math.max(1.0D, Math.abs(lastSyncedMass));
        }
        // Для маленьких BH (<100k) горизонт меняется заметно каждый тик, форсим при 0.5% изменении
        boolean forceSyncByDelta = massChanged && syncRel >= 0.005D;
        if (--syncCooldown <= 0 || forceSyncByDelta) {
            syncCooldown = BlackHoleUtils.getSyncInterval(mass);
            lastSyncedMass = mass;
            net.minecraft.block.state.IBlockState st = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, st, st, 3);
        }

        // --- Адаптивное сохранение NBT: реже для больших масс (IO-экономия) ---
        // setMass(/blockdata) уже вызвал markDirty и сбросил nbtCooldown, поэтому здесь только пассивные дельты.
        if (--nbtCooldown <= 0) nbtCooldown = 0;
        if (Double.doubleToLongBits(mass) != Double.doubleToLongBits(lastPersistedMass)) {
            double abs = Math.abs(mass - lastPersistedMass);
            double rel = abs / Math.max(1.0D, Math.abs(lastPersistedMass));
            boolean forceByDelta = rel >= BlackHoleUtils.NBT_DIRTY_RELATIVE_THRESHOLD;
            if (nbtCooldown <= 0 || forceByDelta) {
                markDirty();
                lastPersistedMass = mass;
                nbtCooldown = BlackHoleUtils.getNbtInterval(mass);
            }
        } else {
            // масса не менялась — просто продлеваем интервал, не спамим markDirty
            if (nbtCooldown <= 0) nbtCooldown = BlackHoleUtils.getNbtInterval(mass);
        }
    }

    private void tickEntities() {
        int parity2 = (int)(world.getTotalWorldTime() & 1);
        int parity4 = (int)(world.getTotalWorldTime() & 3);

        double horizon   = BlackHoleUtils.getHorizonRadius(mass);
        double boostRadius = BlackHoleUtils.getBoostRadius(mass);
        double boostOuter = horizon + boostRadius;
        double gravRange = BlackHoleUtils.getGravityRange(mass);
        if (gravRange < 0.5) return;

        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;
        // TE never moves after placement, so the box is rebuilt only when the range changes.
        if (cachedAABB == null || gravRange != cachedGravRange) {
            cachedGravRange = gravRange;
            cachedAABB = new AxisAlignedBB(
                    cx - gravRange, cy - gravRange, cz - gravRange,
                    cx + gravRange, cy + gravRange, cz + gravRange);
        }
        AxisAlignedBB aabb = cachedAABB;

        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, aabb);

        for (Entity e : entities) {
            if (e == null || e.isDead) continue;
            if (e instanceof EntityPlayer && ((EntityPlayer)e).isSpectator()) continue;
            boolean isPlayer = e instanceof EntityPlayerMP;
            // Stride by type: items/XP across 4 ticks, others across 2 ticks
            boolean isItemOrXp = e instanceof EntityItem || e instanceof EntityXPOrb;
            if (!isPlayer) {
                if (isItemOrXp) {
                    if ((e.getEntityId() & 3) != parity4) continue;
                } else {
                    if ((e.getEntityId() & 1) != parity2) continue;
                }
            }

            // Blacklist
            boolean blacklisted = false;
            for (Class<? extends Entity> cls : BlackHoleUtils.ENTITY_BLACKLIST)
                if (cls.isInstance(e)) { blacklisted = true; break; }
            if (blacklisted) continue;

            if (e instanceof EntityPlayer) {
                EntityPlayer p = (EntityPlayer) e;
                if (p.isCreative() || p.isSpectator()) continue;
            }
            if (e.isDead) continue;

            double ey = e.posY + e.height * 0.5;
            if (e instanceof EntityItem || e instanceof EntityXPOrb) ey = e.posY + 0.25;

            // Compensate stride: items/XP run every 4 ticks (x4), others every 2 ticks (x2)
            double stride = isItemOrXp ? 4.0 : 2.0;

            double dx = cx - e.posX;
            double dy = cy - ey;
            double dz = cz - e.posZ;
            double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
            if (dist < 0.05) dist = 0.05;

            double accelRaw = BlackHoleUtils.getAcceleration(mass, dist);
            // Near-horizon boost: 10x at horizon, cubic falloff to 1x at Ro
            double accel = accelRaw;
            if (boostRadius > 0 && dist > horizon && dist < boostOuter) {
                double t = (boostOuter - dist) / boostRadius;
                double t3 = t * t * t;
                accel = accelRaw * (1.0D + BlackHoleUtils.BOOST_MAX * t3);
            }
            boolean insideHorizon = dist <= horizon;

            // --- Suffocation (applied BEFORE horizon block, so guaranteed inside) ---
            // Threshold lowered 0.5 -> 0.4 per request.
            // Inside horizon -> always true for any living entity. Creative /
            // spectator players were filtered out above, so gm 0/2 are covered.
            if (e instanceof EntityLivingBase && (insideHorizon || accel > 0.4)) {
                EntityLivingBase living = (EntityLivingBase) e;
                // ~1.5 air units per real tick, scaled by stride so faster/slower
                // tick rates give the same effective drain rate.
                int airDelta = (int) Math.max(1, Math.round(1.5 * stride));
                int air = living.getAir() - airDelta;
                if (air < -20) { air = 0; living.attackEntityFrom(DamageSource.DROWN, 1.0F); }
                living.setAir(air);
                if (e.isDead) continue;
            }

            // --- Horizon absorption ---
            if (insideHorizon) {
                try {
                    if (e instanceof EntityItem) {
                        int count = Math.max(1, ((EntityItem) e).getItem().getCount());
                        mass = BlackHoleUtils.clampMass(mass + count * BlackHoleUtils.MASS_PER_ITEM);
                        e.setDead();
                    } else if (e instanceof EntityXPOrb) {
                        mass = BlackHoleUtils.clampMass(mass + BlackHoleUtils.MASS_PER_XP);
                        e.setDead();
                    } else if (e instanceof EntityPlayer) {
                        e.attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
                        if (e.isDead) { mass = BlackHoleUtils.clampMass(mass + BlackHoleUtils.MASS_PER_PLAYER); }
                    } else {
                        try { e.attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE); }
                        catch (Exception ignored) {}
                        if (!e.isDead) e.setDead();
                        mass = BlackHoleUtils.clampMass(mass + BlackHoleUtils.MASS_PER_ENTITY);
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
                continue;
            }

            if (dist > gravRange) continue;
            if (accel < BlackHoleUtils.MIN_ACCEL) continue;

            // --- SpatialAnchor: блокировка гравитации за FE (accel*100) ---
            if (e instanceof EntityPlayer) {
                EntityPlayer p = (EntityPlayer) e;
                try {
                    //if (astrotweaks.item.SpatialAnchor.SpatialAnchor.hasFlightAnchor(p)) {
                        // В абсолютном полёте гравитация уже покрыта 100 FE/tick — просто игнорим
                        //continue;
                    //}
                    if (astrotweaks.item.SpatialAnchor.SpatialAnchor.hasActiveAnchor(p)) {
                        long cost = (long) Math.ceil(accel * 200.0D);
                        if (cost < 1L) cost = 1L;
                        if (astrotweaks.item.SpatialAnchor.SpatialAnchor.tryConsume(p, cost)) {
                            continue;
                        }
                    }
                } catch (Exception ignored) {}
            }

            // --- Motion ---
            double maxAccel = Math.min(accel, dist * 0.45) * stride;
            if (maxAccel > 5.0) maxAccel = 5.0;

            double nx = dx / dist, ny = dy / dist, nz = dz / dist;
            e.motionX += nx * maxAccel * 0.35;
            e.motionY += ny * maxAccel * 0.35;
            e.motionZ += nz * maxAccel * 0.35;

            double speed = Math.sqrt(e.motionX*e.motionX + e.motionY*e.motionY + e.motionZ*e.motionZ);
            double maxSpeed = 4.5;
            if (speed > maxSpeed) {
                double s = maxSpeed / speed;
                e.motionX *= s; e.motionY *= s; e.motionZ *= s;
            }
            e.fallDistance = 0;
            e.velocityChanged = true;

            if (e instanceof EntityPlayerMP) {
                // Players aren't covered by vanilla entity velocity tracking, and
                // the local client doesn't interpolate server-set motion the way
                // it does for remote entities. Push every processed tick.
                // Threshold removed so tiny accumulations aren't silently dropped.
                try {
                    ((EntityPlayerMP) e).connection.sendPacket(new net.minecraft.network.play.server.SPacketEntityVelocity(e));
                } catch (Exception ignored) {}
            }
        }
        // massChanged обрабатывается адаптивно в update() — без немедленного markDirty/sync
    }
}
