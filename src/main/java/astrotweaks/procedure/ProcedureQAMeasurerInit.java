package astrotweaks.procedure;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.HashMap;

import astrotweaks.item.ItemNullQuant;
import astrotweaks.item.ItemGammaQuant;
import astrotweaks.item.ItemDeltaQuant;
import astrotweaks.item.ItemBetaQuant;
import astrotweaks.item.ItemAlphaQuant;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class ProcedureQAMeasurerInit extends ElementsAstrotweaksMod.ModElement {
	public ProcedureQAMeasurerInit(ElementsAstrotweaksMod instance) {
		super(instance, 497);
	}

	private static ItemStack getStack(World world, BlockPos pos, int slot) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityLockableLoot) {
			return ((TileEntityLockableLoot) te).getStackInSlot(slot);
		}
		return ItemStack.EMPTY;
	}

	private static int getCount(World world, BlockPos pos, int slot) {
		ItemStack s = getStack(world, pos, slot);
		return s.isEmpty() ? 0 : s.getCount();
	}

	private static void setStackCount(World world, BlockPos pos, int slot, ItemStack stack, int count) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityLockableLoot) {
			stack.setCount(count);
			((TileEntityLockableLoot) te).setInventorySlotContents(slot, stack);
		}
	}

	private static boolean tryInsertLoop(World world, BlockPos pos, int startSlot, ItemStack prototype) {

		int slot = startSlot;
		for (int i = 0; i < 4; i++, slot++) {
			if (slot > 12) {
				return false;
			}
			ItemStack cur = getStack(world, pos, slot);
			int curCount = cur.isEmpty() ? 0 : cur.getCount();
			boolean sameItem = !cur.isEmpty() && cur.getItem() == prototype.getItem();
			if ((sameItem || curCount == 0) && curCount < 64) {

				int newCount = curCount + 1;
				setStackCount(world, pos, slot, new ItemStack(prototype.getItem()), newCount);
				return true;
			}
		}
		return false;
	}

	private enum QuantType {
		ALPHA(new ItemStack(ItemAlphaQuant.block), 0.24),
		BETA(new ItemStack(ItemBetaQuant.block), 0.50),
		GAMMA(new ItemStack(ItemGammaQuant.block), 0.75),
		DELTA(new ItemStack(ItemDeltaQuant.block), 1.00);

		final ItemStack prototype;
		final double threshold;
		QuantType(ItemStack prototype, double threshold) {
			this.prototype = prototype;
			this.threshold = threshold;
		}
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("x") == null || dependencies.get("y") == null || dependencies.get("z") == null
				|| dependencies.get("world") == null) {
			System.err.println("Failed to load dependencies for procedure QAMeasurerInit!");
			return;
		}
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		BlockPos pos = new BlockPos(x, y, z);


		ItemStack slot8 = getStack(world, pos, 8);
		if (slot8.isEmpty() || slot8.getItem() != ItemNullQuant.block) {
			return;
		}

		double rand = Math.random();

		QuantType chosen = null;
		for (QuantType qt : QuantType.values()) {
			if (rand < qt.threshold) {
				chosen = qt;
				break;
			}
		}
		if (chosen == null) {
			return;
		}


		boolean success = tryInsertLoop(world, pos, 9, chosen.prototype);

		if (success) {

			TileEntity te = world.getTileEntity(pos);
			if (te instanceof TileEntityLockableLoot) {
				((TileEntityLockableLoot) te).decrStackSize(8, 1);
			}

			Map<String, Object> $_dependencies = new HashMap<>();
			$_dependencies.put("world", world);
			$_dependencies.put("x", x);
			$_dependencies.put("y", y);
			$_dependencies.put("z", z);
			ProcedureQAUsePower.executeProcedure($_dependencies);
		}
	}
}
