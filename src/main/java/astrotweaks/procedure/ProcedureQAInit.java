package astrotweaks.procedure;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.HashMap;

import com.google.common.collect.ImmutableMap;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class ProcedureQAInit extends ElementsAstrotweaksMod.ModElement {
	public ProcedureQAInit(ElementsAstrotweaksMod instance) {
		super(instance, 329);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure QAInit!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure QAInit!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure QAInit!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure QAInit!");
			return;
		}
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		boolean power = false;
		power = (boolean) ProcedureQAHavePower.executeProcedure(ImmutableMap.of("x", x, "y", y, "z", z, "world", world));
		if ((power)) {
			if (((new Object() {
				public int getAmount(BlockPos pos, int sltid) {
					TileEntity inv = world.getTileEntity(pos);
					if (inv instanceof TileEntityLockableLoot) {
						ItemStack stack = ((TileEntityLockableLoot) inv).getStackInSlot(sltid);
						if (stack != null)
							return stack.getCount();
					}
					return 0;
				}
			}.getAmount(new BlockPos((int) x, (int) y, (int) z), (int) (8))) != 0)) {
				{
					Map<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("world", world);
					$_dependencies.put("x", x);
					$_dependencies.put("y", y);
					$_dependencies.put("z", z);
					ProcedureQAMeasurerInit.executeProcedure($_dependencies);
				}
			}
			if (((new Object() {
				public int getAmount(BlockPos pos, int sltid) {
					TileEntity inv = world.getTileEntity(pos);
					if (inv instanceof TileEntityLockableLoot) {
						ItemStack stack = ((TileEntityLockableLoot) inv).getStackInSlot(sltid);
						if (stack != null)
							return stack.getCount();
					}
					return 0;
				}
			}.getAmount(new BlockPos((int) x, (int) y, (int) z), (int) (15))) != 0)) {
				{
					Map<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("x", x);
					$_dependencies.put("y", y);
					$_dependencies.put("z", z);
					ProcedureQAReactorInit.executeProcedure($_dependencies);
				}
			}
			if (((new Object() {
				public int getAmount(BlockPos pos, int sltid) {
					TileEntity inv = world.getTileEntity(pos);
					if (inv instanceof TileEntityLockableLoot) {
						ItemStack stack = ((TileEntityLockableLoot) inv).getStackInSlot(sltid);
						if (stack != null)
							return stack.getCount();
					}
					return 0;
				}
			}.getAmount(new BlockPos((int) x, (int) y, (int) z), (int) (13))) != 0)) {
				{
					Map<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("world", world);
					$_dependencies.put("x", x);
					$_dependencies.put("y", y);
					$_dependencies.put("z", z);
					ProcedureQAEmitterInit.executeProcedure($_dependencies);
				}
			}
		}
	}
}
