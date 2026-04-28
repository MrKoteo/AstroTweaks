package astrotweaks.procedure;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.item.ItemStack;

import java.util.Random;
import java.util.Map;

import astrotweaks.item.ItemPowerUnitX;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class ProcedureQAUsePower extends ElementsAstrotweaksMod.ModElement {
	public ProcedureQAUsePower(ElementsAstrotweaksMod instance) {
		super(instance, 406);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure QAUsePower!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure QAUsePower!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure QAUsePower!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure QAUsePower!");
			return;
		}
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		boolean processed = false;
		processed = (boolean) (false);
		if ((((new Object() {
			public ItemStack getItemStack(BlockPos pos, int sltid) {
				TileEntity inv = world.getTileEntity(pos);
				if (inv instanceof TileEntityLockableLoot)
					return ((TileEntityLockableLoot) inv).getStackInSlot(sltid);
				return ItemStack.EMPTY;
			}
		}.getItemStack(new BlockPos((int) x, (int) y, (int) z), (int) (0))).getItem() == new ItemStack(ItemPowerUnitX.block, (int) (1)).getItem())
				&& (!(processed)))) {
			processed = (boolean) (true);
			{
				TileEntity inv = world.getTileEntity(new BlockPos((int) x, (int) y, (int) z));
				if (inv != null && (inv instanceof TileEntityLockableLoot)) {
					ItemStack stack = ((TileEntityLockableLoot) inv).getStackInSlot((int) (0));
					if (stack != null) {
						if (stack.attemptDamageItem((int) 1, new Random(), null)) {
							stack.shrink(1);
							stack.setItemDamage(0);
						}
					}
				}
			}
		} else if ((((new Object() {
			public ItemStack getItemStack(BlockPos pos, int sltid) {
				TileEntity inv = world.getTileEntity(pos);
				if (inv instanceof TileEntityLockableLoot)
					return ((TileEntityLockableLoot) inv).getStackInSlot(sltid);
				return ItemStack.EMPTY;
			}
		}.getItemStack(new BlockPos((int) x, (int) y, (int) z), (int) (1))).getItem() == new ItemStack(ItemPowerUnitX.block, (int) (1)).getItem())
				&& (!(processed)))) {
			processed = (boolean) (true);
			{
				TileEntity inv = world.getTileEntity(new BlockPos((int) x, (int) y, (int) z));
				if (inv != null && (inv instanceof TileEntityLockableLoot)) {
					ItemStack stack = ((TileEntityLockableLoot) inv).getStackInSlot((int) (1));
					if (stack != null) {
						if (stack.attemptDamageItem((int) 1, new Random(), null)) {
							stack.shrink(1);
							stack.setItemDamage(0);
						}
					}
				}
			}
		} else {
			processed = (boolean) (false);
		}
	}
}
