package astrotweaks.item.TotemOfGod;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TotemOfGodEvents {
	/*
	 * Стоимость поглощения одного очка урона (в FE).
	 * Итоговый расход: урон * COST_PER_DAMAGE = энергия.
	 */
	private static final long COST_PER_DAMAGE = 10_000L;
	/*
	 * Стоимость отмены смерти. Аддитивно добавляется к стоимости за HP:
	 * итог смерти = HP_снятые_при_смерти * COST_PER_DAMAGE + DEATH_COST.
	 */
	private static final long DEATH_COST = 10_000_000L;
	/*
	 * Для каждого смертельного удара в onLivingHurt запоминаем HP игрока на
	 * момент удара и сколько из них тотем уже поглотил. Это нужно, чтобы в
	 * onLivingDeath корректно посчитать "стоимость HP, снятых при смерти".
	 * Запись существует только в течение одного тика (удар -> смерть) и
	 * очищается либо самим событием смерти, либо следующим ударом.
	 */
	private static final Map<UUID, LethalBlow> LETHAL_BLOWS = new ConcurrentHashMap<>();

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLivingHurt(LivingHurtEvent event) {
		if (event.getEntityLiving().world.isRemote)  return;
		if (!(event.getEntityLiving() instanceof EntityPlayer))  return;

		EntityPlayer player = (EntityPlayer) event.getEntityLiving();

		float damage = event.getAmount();
		if (damage <= 0.0F)  return;

		float effectiveHp = player.getHealth() + player.getAbsorptionAmount();
		if (effectiveHp <= 0.0F)  return;

		UUID uuid = player.getUniqueID();

		/*
		 * Удар смертелен? Запоминаем HP на момент удара: если игрок всё же
		 * умрёт, onLivingDeath возместит стоимость этих HP + DEATH_COST.
		 */
		boolean lethal = damage >= effectiveHp;

		if (lethal) {
			LETHAL_BLOWS.put(uuid, new LethalBlow(effectiveHp));
		}

		long available = getTotalEnergy(player);
		if (available <= 0L)  return;

		double affordableHp = (double) available / (double) COST_PER_DAMAGE;

		/*
		 * Поглощаем не больше, чем игрок реально может потерять (effectiveHp),
		 * и не больше, чем покрывает заряд. "Оверкилл" сверх этих HP не
		 * уменьшает урон (игрок всё равно умрёт), поэтому на такие удары
		 * не тратим энергию зря — её приберёт onLivingDeath.
		 */
		double absorbable = Math.min(damage, Math.min((double) effectiveHp, affordableHp));
		if (absorbable <= 0.0D)  return;

		if (damage - absorbable >= effectiveHp) {
			return; // даже при полном поглощении удар остаётся смертельным — потратиться сможет onLivingDeath
		}

		long cost = (long) Math.ceil(absorbable * (double) COST_PER_DAMAGE);
		cost = Math.min(cost, available);

		long consumed = consumeFromTotems(player, cost);
		if (consumed <= 0L)  return;

		double actuallyAbsorbed = (double) consumed / (double) COST_PER_DAMAGE;
		float remaining = (float) Math.max(0.0D, (double) damage - actuallyAbsorbed);

		event.setAmount(remaining);

		if (lethal) {
			LethalBlow blow = LETHAL_BLOWS.get(uuid);
			if (blow != null) {
				if (remaining < effectiveHp) {
					// Поглощения хватило, чтобы игрок выжил — запись больше не нужна.
					LETHAL_BLOWS.remove(uuid);
				} else {
					// Игрок всё равно умирает: сохраняем, сколько HP уже оплачено поглощением.
					blow.absorbedHp = (float) Math.max(blow.absorbedHp, actuallyAbsorbed);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLivingDeath(LivingDeathEvent event) {
		if (event.getEntityLiving().world.isRemote)  return;

		if (!(event.getEntityLiving() instanceof EntityPlayer))  return;

		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		LethalBlow blow = LETHAL_BLOWS.remove(player.getUniqueID());

		/*
		 * Стоимость отмены смерти = стоимость HP, снятых при смерти,
		 * (если такой удар был зафиксирован и HP ещё не оплачены поглощением)
		 * + DEATH_COST за саму отмену смерти.
		 */
		long hpCost = 0L;

		if (blow != null) {
			double remainingHp = Math.max(0.0D, (double) blow.effectiveHp - (double) blow.absorbedHp);
			hpCost = (long) Math.ceil(remainingHp * (double) COST_PER_DAMAGE);
		}
		long totalCost = hpCost + DEATH_COST;

		if (consumeFromTotems(player, totalCost) == totalCost) {
			/*
			 * При некоторых вариантах смерти здоровье уже может быть равно 0.
			 * Возвращаем игроку хотя бы одно очко здоровья.
			 */
			if (player.getHealth() <= 0.0F) {
				player.setHealth(1.0F);
			}
			event.setCanceled(true);
		}
	}

	private static boolean isTotem(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() == TotemOfGod.TOTEM_OF_GOD;
	}
	private static long getTotalEnergy(EntityPlayer player) {
		return sumEnergy(player.inventory.mainInventory)
				+ sumEnergy(player.inventory.armorInventory)
				+ sumEnergy(player.inventory.offHandInventory);
	}
	private static long sumEnergy(List<ItemStack> inventory) {
		long total = 0L;
		for (ItemStack stack : inventory) {
			if (isTotem(stack)) {
				total += TotemOfGod.getEnergyStored(stack);
			}
		}
		return total;
	}

	/**
	 * Тратит энергию по всем тотемам игрока.
	 *
	 * @return сколько энергии реально удалось потратить.
	 */
	private static long consumeFromTotems(EntityPlayer player, long amount) {
		long remaining = amount;
		remaining = drain(player.inventory.mainInventory, remaining);

		if (remaining > 0L) {
			remaining = drain(player.inventory.offHandInventory, remaining);
		}
		return amount - remaining;
	}

	private static long drain(List<ItemStack> inventory, long remaining) {
		for (ItemStack stack : inventory) {
			if (remaining <= 0L) {
				break;
			}
			if (isTotem(stack)) {
				remaining -= TotemOfGod.extractEnergy(stack, remaining, false);
			}
		}
		return remaining;
	}

	/** HP игрока на момент смертельного удара и сколько из них уже оплачено. */
	private static final class LethalBlow {
		final float effectiveHp;
		float absorbedHp;
		LethalBlow(float effectiveHp) {
			this.effectiveHp = effectiveHp;
		}
	}
}
