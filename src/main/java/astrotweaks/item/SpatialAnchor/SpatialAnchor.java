package astrotweaks.item.SpatialAnchor;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import com.google.common.collect.Multimap;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public final class SpatialAnchor {
    public static final long MAX_ENERGY = 1_000_000_000_000L;
    public static final int FE_MAX = Integer.MAX_VALUE;

    public static final long COST_KNOCKBACK = 2000L;
    //public static final long COST_FLUID = 20L;
    public static final long COST_COLLISION = 20L;
    public static final long COST_FLIGHT = 500L;

    public static final long COST_PASSIVE = 2L;

    
    private static final String ENERGY_KEY = "SpatialAnchorEnergy";
    private static final String ENABLED_KEY = "SpatialAnchorEnabled";
    private static final String FLIGHT_KEY = "SpatialAnchorFlight";

    public static final int FLAG_ACTIVE = 1;
    public static final int FLAG_FLIGHT = 2;

    public static final Item SPATIAL_ANCHOR = new ItemCustom()
            .setRegistryName("astrotweaks", "spatial_anchor")
            .setUnlocalizedName("spatial_anchor");

    private SpatialAnchor() {}

    // ------------------------------------------------------------------
    // Energy ops
    // ------------------------------------------------------------------

    public static long getEnergyStored(ItemStack stack) {
        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        return storage instanceof AnchorEnergyStorage ? ((AnchorEnergyStorage) storage).getInternalEnergy() : 0L;
    }
    public static void setEnergyStored(ItemStack stack, long amount) {
        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (storage instanceof AnchorEnergyStorage) {
            ((AnchorEnergyStorage) storage).setInternalEnergy(amount);
        }
    }
    public static long receiveEnergy(ItemStack stack, long amount, boolean simulate) {
        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        return storage instanceof AnchorEnergyStorage ? ((AnchorEnergyStorage) storage).receiveInternal(amount, simulate) : 0L;
    }
    public static long extractEnergy(ItemStack stack, long amount, boolean simulate) {
        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        return storage instanceof AnchorEnergyStorage ? ((AnchorEnergyStorage) storage).extractInternal(amount, simulate) : 0L;
    }
    public static float getCharge(ItemStack stack) {
        return (float) ((double) getEnergyStored(stack) / (double) MAX_ENERGY);
    }
    public static boolean isEnabled(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() != SPATIAL_ANCHOR) return false;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey(ENABLED_KEY)) return true; // default ON
        return tag.getBoolean(ENABLED_KEY);
    }
    public static void setEnabled(ItemStack stack, boolean enabled) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setBoolean(ENABLED_KEY, enabled);
    }
    public static boolean isFlightEnabled(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() != SPATIAL_ANCHOR) return false;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey(FLIGHT_KEY)) return false; // default OFF
        return tag.getBoolean(FLIGHT_KEY);
    }
    public static void setFlightEnabled(ItemStack stack, boolean enabled) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setBoolean(FLIGHT_KEY, enabled);
    }
    /** Есть ли у игрока хотя бы один якорь с включённым полётом и энергией */
    public static boolean hasFlightAnchor(EntityPlayer player) {
        if (player == null) return false;
        return hasFlightInList(player.inventory.mainInventory)
                || hasFlightInList(player.inventory.armorInventory)
                || hasFlightInList(player.inventory.offHandInventory);
    }
    private static boolean hasFlightInList(List<ItemStack> inv) {
        for (ItemStack s : inv) if (isAnchor(s) && isEnabled(s) && isFlightEnabled(s) && getEnergyStored(s) > 0) return true;
        return false;
    }
    public static boolean isAnchor(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == SPATIAL_ANCHOR;
    }
    public static boolean hasActiveAnchor(EntityPlayer player) {
        if (player == null) return false;
        return hasActiveInList(player.inventory.mainInventory)
                || hasActiveInList(player.inventory.armorInventory)
                || hasActiveInList(player.inventory.offHandInventory);
    }
    private static boolean hasActiveInList(List<ItemStack> inv) {
        for (ItemStack s : inv) if (isAnchor(s) && isEnabled(s) && getEnergyStored(s) > 0) return true;
        return false;
    }
    public static long getTotalEnergy(EntityPlayer player) {
        return sumEnergy(player.inventory.mainInventory)
                + sumEnergy(player.inventory.armorInventory)
                + sumEnergy(player.inventory.offHandInventory);
    }
    private static long sumEnergy(List<ItemStack> inv) {
        long total = 0;
        for (ItemStack s : inv) if (isAnchor(s) && isEnabled(s)) total += getEnergyStored(s);
        return total;
    }

    // ------------------------------------------------------------------
    // Fast scan — один проход по всем инвентарям за тик
    // ------------------------------------------------------------------

    /**
     * Один проход по всем инвентарям игрока (main + armor + offHand).
     * Возвращает битовую маску FLAG_ACTIVE / FLAG_FLIGHT.
     * Если {@code totalOut != null}, в totalOut[0] кладётся суммарная энергия
     * всех включённых якорей с энергией > 0.
     *
     * <p>Эквивалент цепочки hasActiveAnchor/hasFlightAnchor/getTotalEnergy,
     * но читает NBT один раз на стак и без Capability-диспетчеризации.
     */
    public static int scanPlayerAnchors(EntityPlayer player, long[] totalOut) {
        if (player == null) {
            if (totalOut != null) totalOut[0] = 0L;
            return 0;
        }
        int flags = 0;
        long total = 0;

        List<ItemStack> main = player.inventory.mainInventory;
        for (int i = 0, n = main.size(); i < n; i++) {
            int f = scanStack(main.get(i), totalOut != null);
            if (f != 0) {
                flags |= f;
                if (totalOut != null) total += lastScanEnergy;
            }
        }
        List<ItemStack> armor = player.inventory.armorInventory;
        for (int i = 0, n = armor.size(); i < n; i++) {
            int f = scanStack(armor.get(i), totalOut != null);
            if (f != 0) {
                flags |= f;
                if (totalOut != null) total += lastScanEnergy;
            }
        }
        List<ItemStack> off = player.inventory.offHandInventory;
        for (int i = 0, n = off.size(); i < n; i++) {
            int f = scanStack(off.get(i), totalOut != null);
            if (f != 0) {
                flags |= f;
                if (totalOut != null) total += lastScanEnergy;
            }
        }

        if (totalOut != null) totalOut[0] = total;
        return flags;
    }

    /** Энергия последнего успешно просканированного стака (используется только внутри scanPlayerAnchors). */
    private static long lastScanEnergy;

    /**
     * Сканирует один стак. Возвращает FLAG_ACTIVE / FLAG_FLIGHT / 0.
     * Если wantEnergy == true, энергия стака записывается в {@link #lastScanEnergy}.
     */
    private static int scanStack(ItemStack s, boolean wantEnergy) {
        if (s == null || s.isEmpty() || s.getItem() != SPATIAL_ANCHOR) return 0;
        NBTTagCompound tag = s.getTagCompound();
        if (tag == null) return 0;
        // default ENABLED = true: если ключа нет — включён
        if (tag.hasKey(ENABLED_KEY) && !tag.getBoolean(ENABLED_KEY)) return 0;
        long e = tag.getLong(ENERGY_KEY);
        if (e <= 0L) return 0;
        int f = FLAG_ACTIVE;
        if (tag.getBoolean(FLIGHT_KEY)) f |= FLAG_FLIGHT;
        if (wantEnergy) lastScanEnergy = e;
        return f;
    }

    /**
     * Tries to consume amount from enabled anchors in inventory.
     * @return true if full amount was consumed
     */
    public static boolean tryConsume(EntityPlayer player, long amount) {
        return tryConsume(player, amount, getTotalEnergy(player));
    }

    /**
     * Tries to consume amount from enabled anchors in inventory,
     * используя заранее известный суммарный объём энергии (из scanPlayerAnchors).
     * @return true if full amount was consumed
     */
    public static boolean tryConsume(EntityPlayer player, long amount, long knownTotal) {
        if (amount <= 0) return true;
        if (knownTotal < amount) return false;
        long remaining = amount;
        remaining = drain(player.inventory.mainInventory, remaining);
        if (remaining > 0) remaining = drain(player.inventory.armorInventory, remaining);
        if (remaining > 0) remaining = drain(player.inventory.offHandInventory, remaining);
        return remaining == 0;
    }

    private static long drain(List<ItemStack> inv, long remaining) {
        for (int i = 0, n = inv.size(); i < n && remaining > 0; i++) {
            ItemStack s = inv.get(i);
            if (isAnchor(s) && isEnabled(s)) {
                remaining -= extractEnergy(s, remaining, false);
            }
        }
        return remaining;
    }

    /**
     * Black hole gravity helper: tries to block accel.
     * Cost = ceil(accel*100). Returns true if blocked (energy consumed).
     */
    public static boolean tryBlockBlackHoleGravity(EntityPlayer player, double accel) {
        if (accel <= 0) return false;
        long cost = (long) Math.ceil(accel * 100.0D);
        if (cost < 1) cost = 1;
        return tryConsume(player, cost);
    }

    /**
     * Пассивное потребление: списывает COST_PASSIVE FE с каждого включённого якоря.
     */
    public static void tickPassiveDrain(EntityPlayer player) {
        drainPassive(player.inventory.mainInventory);
        drainPassive(player.inventory.offHandInventory);
    }
    private static void drainPassive(List<ItemStack> inv) {
        for (int i = 0, n = inv.size(); i < n; i++) {
            ItemStack s = inv.get(i);
            if (!isAnchor(s) || !isEnabled(s)) continue;
            extractEnergy(s, COST_PASSIVE, false);
        }
    }



    // ------------------------------------------------------------------
    // Item
    // ------------------------------------------------------------------

    public static class ItemCustom extends Item {
        public ItemCustom() {
            maxStackSize = 1;
            setCreativeTab(astrotweaks.creativetab.ATCreativeTabs.ASTRO_TWEAKS_CT);
        }

        @Override
        public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
            return new EnergyProvider(stack);
        }
        @Override
        public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
            if (!isInCreativeTab(tab)) return;
            items.add(new ItemStack(this));
            ItemStack charged = new ItemStack(this);
            receiveEnergy(charged, MAX_ENERGY, false);
            charged.getTagCompound(); // ensure tag
            // charged is enabled by default
            items.add(charged);
        }

        @Override
        public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
            Multimap<String, AttributeModifier> m = super.getItemAttributeModifiers(slot);
            if (slot == EntityEquipmentSlot.MAINHAND) {
                m.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                        new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Item modifier", -1.0D, 0));
                m.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                        new AttributeModifier(ATTACK_SPEED_MODIFIER, "Item modifier", -2.4D, 0));
            }
            return m;
        }

        @Override
        public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
            super.addInformation(stack, world, list, flag);
            long stored = getEnergyStored(stack);
            boolean enabled = isEnabled(stack);
            boolean flight = isFlightEnabled(stack);
            list.add(new TextComponentTranslation("item.spatial_anchor.tooltip").getFormattedText());
            list.add(new TextComponentTranslation("u.charge").getFormattedText()
                    + TextFormatting.RED + formatPercent(getCharge(stack)) + "%");
            list.add(new TextComponentTranslation("u.energy").getFormattedText()
                    + TextFormatting.GREEN + formatNumber(stored) + " / " + formatNumber(MAX_ENERGY) + " FE");
            list.add(new TextComponentTranslation("u.fe_buffer").getFormattedText()
                    + TextFormatting.DARK_GRAY + formatNumber(Math.min(stored, FE_MAX)) + " / " + formatNumber(FE_MAX));
            String stateKey = enabled ? "item.spatial_anchor.enabled" : "item.spatial_anchor.disabled";
            TextFormatting col = enabled ? TextFormatting.GREEN : TextFormatting.RED;
            list.add(new TextComponentTranslation(stateKey).getFormattedText() + " " + col + (enabled ? "ON" : "OFF"));
            String flightKey = flight ? "item.spatial_anchor.flight_on" : "item.spatial_anchor.flight_off";
            TextFormatting fcol = flight ? TextFormatting.AQUA : TextFormatting.GRAY;
            list.add(new TextComponentTranslation(flightKey).getFormattedText() + " " + fcol + (flight ? "ON" : "OFF"));
        }

        @Override
        public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
            ItemStack stack = player.getHeldItem(hand);
            if (stack.isEmpty() || stack.getItem() != SPATIAL_ANCHOR) return new ActionResult<>(EnumActionResult.PASS, stack);
            if (!world.isRemote) {
                if (player.isSneaking()) {
                    boolean cur = isFlightEnabled(stack);
                    setFlightEnabled(stack, !cur);
                    String msgKey = !cur ? "item.spatial_anchor.flight_enable" : "item.spatial_anchor.flight_disable";
                    player.sendStatusMessage(new TextComponentString(new TextComponentTranslation(msgKey).getFormattedText()), true);
                    if (!cur) {
                        // при включении полёта сразу даём разрешение, если есть энергия
                        if (hasActiveAnchor(player) && getTotalEnergy(player) >= COST_FLIGHT) {
                            player.capabilities.allowFlying = true;
                            player.sendPlayerAbilities();
                        }
                    } else {
                        // выкл — снимаем полёт если не креатив
                        if (!player.capabilities.isCreativeMode) {
                            player.capabilities.allowFlying = false;
                            player.capabilities.isFlying = false;
                            player.sendPlayerAbilities();
                        }
                    }
                } else {
                    boolean cur = isEnabled(stack);
                    setEnabled(stack, !cur);
                    String msgKey = !cur ? "item.spatial_anchor.toggle_on" : "item.spatial_anchor.toggle_off";
                    player.sendStatusMessage(new TextComponentString(new TextComponentTranslation(msgKey).getFormattedText()), true);
                    if (!cur && isFlightEnabled(stack)) {
                        // выключили якорь — гасим полёт
                        if (!player.capabilities.isCreativeMode) {
                            player.capabilities.allowFlying = false;
                            player.capabilities.isFlying = false;
                            player.sendPlayerAbilities();
                        }
                    }
                }
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        @Override
        public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
            if (!slotChanged && oldStack.getItem() == newStack.getItem()) {
                // Не проигрывать анимацию руки при частом снятии энергии (NBT меняется каждый тик)
                return false;
            }
            return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
        }

        @Override
        public boolean showDurabilityBar(ItemStack stack) { return true; }
        @Override
        public double getDurabilityForDisplay(ItemStack stack) {
            return 1.0D - ((double) getEnergyStored(stack) / (double) MAX_ENERGY);
        }
        @Override
        public int getRGBDurabilityForDisplay(ItemStack stack) {
            float charge = getCharge(stack);
            if (!isEnabled(stack)) return 0x777777;
            if (isFlightEnabled(stack)) return 0x00FFFF; // голубой когда полёт вкл
            int red = (int) ((1.0F - charge) * 255.0F);
            int green = (int) (charge * 255.0F);
            return (red << 16) | (green << 8);
        }
        private static String formatPercent(float charge) {
            return String.format(Locale.ROOT, "%.1f", charge * 100.0F);
        }
        private static String formatNumber(long v) {
            String digits = Long.toString(v);
            StringBuilder b = new StringBuilder();
            int c = 0;
            for (int i = digits.length() - 1; i >= 0; i--) {
                b.append(digits.charAt(i));
                if (++c % 3 == 0 && i > 0) b.append(',');
            }
            return b.reverse().toString();
        }
    }

    // ------------------------------------------------------------------
    // Capability
    // ------------------------------------------------------------------

    public static class EnergyProvider implements ICapabilitySerializable<NBTTagCompound> {
        private final AnchorEnergyStorage storage;
        public EnergyProvider(ItemStack stack) { this.storage = new AnchorEnergyStorage(stack); }
        @Override public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) { return capability == CapabilityEnergy.ENERGY; }
        @Override @SuppressWarnings("unchecked")
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) { return capability == CapabilityEnergy.ENERGY ? (T) storage : null; }
        @Override public NBTTagCompound serializeNBT() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setLong("Energy", storage.getInternalEnergy());
            return tag;
        }
        @Override public void deserializeNBT(NBTTagCompound nbt) {
            if (nbt != null) storage.setInternalEnergy(nbt.getLong("Energy"));
        }
    }

    public static class AnchorEnergyStorage implements IEnergyStorage {
        private final ItemStack stack;
        public AnchorEnergyStorage(ItemStack stack) { this.stack = stack; }

        private long readEnergy() {
            NBTTagCompound tag = stack.getTagCompound();
            return tag == null ? 0L : tag.getLong(ENERGY_KEY);
        }
        private void writeEnergy(long value) {
            value = Math.max(0L, Math.min(MAX_ENERGY, value));
            if (value == 0L) {
                NBTTagCompound tag = stack.getTagCompound();
                if (tag != null && tag.hasKey(ENERGY_KEY)) {
                    tag.removeTag(ENERGY_KEY);
                    // keep ENABLED tag if present
                    if (tag.hasNoTags()) {
                        // Don't delete ENABLED if we just removed energy but enabled tag present
                        // hasNoTags will be false if ENABLED remains
                    }
                    if (tag.hasNoTags()) stack.setTagCompound(null);
                }
                return;
            }
            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) {
                tag = new NBTTagCompound();
                stack.setTagCompound(tag);
            }
            tag.setLong(ENERGY_KEY, value);
        }

        public long getInternalEnergy() { return readEnergy(); }
        public void setInternalEnergy(long v) { writeEnergy(v); }
        public long receiveInternal(long amount, boolean simulate) {
            if (amount <= 0) return 0;
            long cur = readEnergy();
            long accepted = Math.min(amount, MAX_ENERGY - cur);
            if (!simulate && accepted > 0) writeEnergy(cur + accepted);
            return accepted;
        }
        public long extractInternal(long amount, boolean simulate) {
            if (amount <= 0) return 0;
            long cur = readEnergy();
            long extracted = Math.min(amount, cur);
            if (!simulate && extracted > 0) writeEnergy(cur - extracted);
            return extracted;
        }
        public static int toExternal(long internal) {
            if (internal <= 0) return 0;
            return (int) Math.min(internal, FE_MAX);
        }
        @Override public int receiveEnergy(int maxReceive, boolean simulate) { return (int) receiveInternal(maxReceive, simulate); }
        @Override public int extractEnergy(int maxExtract, boolean simulate) { return (int) extractInternal(maxExtract, simulate); }
        @Override public int getEnergyStored() { return toExternal(readEnergy()); }
        @Override public int getMaxEnergyStored() { return FE_MAX; }
        @Override public boolean canExtract() { return true; }
        @Override public boolean canReceive() { return true; }
    }
}
