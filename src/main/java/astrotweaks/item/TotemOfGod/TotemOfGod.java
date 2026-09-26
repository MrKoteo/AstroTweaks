package astrotweaks.item.TotemOfGod;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import com.google.common.collect.Multimap;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;


public final class TotemOfGod {
	/**
	 * Полная внутренняя ёмкость тотема (10^15).
	 * Внутренняя единица хранения совпадает с 1 FE (1:1), поэтому
	 * 1_000_000_000_000_000 тут — это ровно столько же FE.
	 */
	public static final long MAX_ENERGY = 1_000_000_000_000_000L;

	/**
	 * Максимум, который может вернуть {@link IEnergyStorage} наружу.
	 * Forge Energy работает с int, поэтому зарядники за раз принимают
	 * не больше этого значения; сама ёмкость при этом 1:1 = {@link #MAX_ENERGY}.
	 */
	public static final int FE_MAX = Integer.MAX_VALUE;

	/** Стоимость одного ПКМ-лечения (в FE). */
	public static final long HEAL_COST = 350_000L;

	/** Ключ заряда в NBT предмета. */
	private static final String ENERGY_KEY = "TotemEnergy";

	public static final Item TOTEM_OF_GOD = new ItemCustom()
			.setRegistryName("astrotweaks", "totem_of_god")
			.setUnlocalizedName("totem_of_god");

	private TotemOfGod() {}

	// ------------------------------------------------------------------
	// Операции с зарядом (внутренние единицы == FE)
	// ------------------------------------------------------------------

	public static long getEnergyStored(ItemStack stack) {
		IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
		return storage instanceof TotemEnergyStorage ? ((TotemEnergyStorage) storage).getInternalEnergy() : 0L;
	}

	public static void setEnergyStored(ItemStack stack, long amount) {
		IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);

		if (storage instanceof TotemEnergyStorage) {
			((TotemEnergyStorage) storage).setInternalEnergy(amount);
		}
	}

	/** Возвращает фактически принятое количество энергии. */
	public static long receiveEnergy(ItemStack stack, long amount, boolean simulate) {
		IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
		return storage instanceof TotemEnergyStorage ? ((TotemEnergyStorage) storage).receiveInternal(amount, simulate) : 0L;
	}
	/** Возвращает фактически извлечённое количество энергии. */
	public static long extractEnergy(ItemStack stack, long amount, boolean simulate) {
		IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
		return storage instanceof TotemEnergyStorage ? ((TotemEnergyStorage) storage).extractInternal(amount, simulate) : 0L;
	}
	/** Доля заряда от 0.0 до 1.0. */
	public static float getCharge(ItemStack stack) {
		return (float) ((double) getEnergyStored(stack) / (double) MAX_ENERGY);
	}

	// ------------------------------------------------------------------
	// Предмет
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

		/*
		 * Заряд хранится в NBT предмета (см. TotemEnergyStorage), поэтому
		 * при каждом изменении меняется и сам tag стека. Это заставляет
		 * сервер пересылать предмет клиенту через detectAndSendChanges,
		 * благодаря чему тултип и полоска "прочности" обновляются сразу.
		 */

		/* CT: разряженный и полностью заряженный варианты. */
		@Override
		public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
			if (!isInCreativeTab(tab)) {
				return;
			}
			items.add(new ItemStack(this));

			ItemStack charged = new ItemStack(this);
			receiveEnergy(charged, MAX_ENERGY, false);
			items.add(charged);
		}

		@Override
		public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
			Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);

			if (slot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(
					SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
					new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Item modifier", -1.0D, 0)
				);
				multimap.put(
					SharedMonsterAttributes.ATTACK_SPEED.getName(),
					new AttributeModifier(ATTACK_SPEED_MODIFIER, "Item modifier", -2.4D, 0)
				);
			}
			return multimap;
		}

		private static final String text1 = new TextComponentTranslation("item.totem_of_god.tooltip").getFormattedText();
		private static final String text2 = new TextComponentTranslation("u.charge").getFormattedText();
		private static final String text3 = new TextComponentTranslation("u.energy").getFormattedText();
		private static final String text4 = new TextComponentTranslation("u.fe_buffer").getFormattedText();


		@Override
		public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(stack, world, list, flag);

			long stored = getEnergyStored(stack);
			list.add(text1);
			list.add(text2 + TextFormatting.RED + formatPercent(getCharge(stack)) + "%");
			list.add(text3 + TextFormatting.GREEN + formatNumber(stored) + " / " + formatNumber(MAX_ENERGY) + " FE");
			list.add(text4 + TextFormatting.DARK_GRAY + formatNumber(Math.min(stored, FE_MAX)) + " / " + formatNumber(FE_MAX));
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
			ItemStack stack = player.getHeldItem(hand);

			if (!world.isRemote && extractEnergy(stack, HEAL_COST, true) == HEAL_COST) {
				extractEnergy(stack, HEAL_COST, false);
				player.addPotionEffect(new PotionEffect(MobEffects.INSTANT_HEALTH, 1, 2, false, false));
				player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 3, false, false));
				player.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 20, 1, false, false));

				player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 1200*5, 0, false, false));
				player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 1200*5, 0, false, false));

				player.removePotionEffect(MobEffects.POISON);
				player.removePotionEffect(MobEffects.WEAKNESS);
				player.removePotionEffect(MobEffects.NAUSEA);
				player.removePotionEffect(MobEffects.WITHER);
				player.removePotionEffect(MobEffects.BLINDNESS);
				player.removePotionEffect(MobEffects.HUNGER);
				player.removePotionEffect(MobEffects.MINING_FATIGUE);
				player.removePotionEffect(MobEffects.INSTANT_DAMAGE);
				player.removePotionEffect(MobEffects.LEVITATION);
				player.removePotionEffect(MobEffects.SLOWNESS);
				player.removePotionEffect(MobEffects.UNLUCK);


				
				SimpleDifficultyCompat.applyHealEffects(player);
			}
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}

		/* Полоска "прочности": 0% = красная, 100% = зелёная. */
		@Override
		public boolean showDurabilityBar(ItemStack stack) {
			return true;
		}
		@Override
		public double getDurabilityForDisplay(ItemStack stack) {
			return 1.0D - ((double) getEnergyStored(stack) / (double) MAX_ENERGY);
		}

		@Override
		public int getRGBDurabilityForDisplay(ItemStack stack) {
			float charge = getCharge(stack);
			int red = (int) ((1.0F - charge) * 255.0F);
			int green = (int) (charge * 255.0F);
			return (red << 16) | (green << 8);
		}

		private static String formatPercent(float charge) {
			return String.format(Locale.ROOT, "%.1f", charge * 100.0F);
		}
		private static String formatNumber(long value) {
			String digits = Long.toString(value);
			StringBuilder builder = new StringBuilder();
			int counter = 0;
			for (int i = digits.length() - 1; i >= 0; i--) {
				builder.append(digits.charAt(i));
				if (++counter % 3 == 0 && i > 0) {
					builder.append(',');
				}
			}
			return builder.reverse().toString();
		}
	}

	// ------------------------------------------------------------------
	// Хранилище энергии
	// ------------------------------------------------------------------

	public static class EnergyProvider implements ICapabilitySerializable<NBTTagCompound> {
		private final TotemEnergyStorage storage;

		public EnergyProvider(ItemStack stack) {
			this.storage = new TotemEnergyStorage(stack);
		}

		@Override
		public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
			return capability == CapabilityEnergy.ENERGY;
		}
		@Override
		@SuppressWarnings("unchecked")
		public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
			return capability == CapabilityEnergy.ENERGY ? (T) storage : null;
		}
		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong("Energy", storage.getInternalEnergy());
			return tag;
		}
		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			if (nbt != null) {
				storage.setInternalEnergy(nbt.getLong("Energy"));
			}
		}
	}

	/**
	 * Внутреннее хранение на long (до {@link #MAX_ENERGY}), единица = 1 FE.
	 * Заряд лежит напрямую в NBT предмета, поэтому каждая трата/зарядка
	 * меняет tag стека — сервер сразу пересылает предмет клиенту, и
	 * тултип/полоска прочности обновляются без ручной синхронизации.
	 */
	public static class TotemEnergyStorage implements IEnergyStorage {
		private final ItemStack stack;

		public TotemEnergyStorage(ItemStack stack) {
			this.stack = stack;
		}

		// --- чтение/запись заряда в NBT предмета ---

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
					if (tag.hasNoTags()) {
						stack.setTagCompound(null);
					}
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

		// --- точный внутренний доступ ---

		public long getInternalEnergy() {
			return readEnergy();
		}
		public void setInternalEnergy(long value) {
			writeEnergy(value);
		}

		public long receiveInternal(long amount, boolean simulate) {
			if (amount <= 0L) {
				return 0L;
			}

			long current = readEnergy();
			long accepted = Math.min(amount, MAX_ENERGY - current);

			if (!simulate && accepted > 0L) {
				writeEnergy(current + accepted);
			}
			return accepted;
		}

		public long extractInternal(long amount, boolean simulate) {
			if (amount <= 0L) {
				return 0L;
			}

			long current = readEnergy();
			long extracted = Math.min(amount, current);

			if (!simulate && extracted > 0L) {
				writeEnergy(current - extracted);
			}
			return extracted;
		}

		// --- интерфейс Forge Energy: 1 внутренняя единица == 1 FE ---

		/** Сколько FE "видит" зарядник при текущем заряде (int-ограничение). */
		public static int toExternal(long internal) {
			if (internal <= 0L) {
				return 0;
			}
			return (int) Math.min(internal, FE_MAX);
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			return (int) receiveInternal(maxReceive, simulate);
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			return (int) extractInternal(maxExtract, simulate);
		}

		@Override
		public int getEnergyStored() {
			return toExternal(readEnergy());
		}

		@Override
		public int getMaxEnergyStored() {
			return FE_MAX;
		}

		@Override
		public boolean canExtract() {
			return true;
		}

		@Override
		public boolean canReceive() {
			return true;
		}
	}
}
