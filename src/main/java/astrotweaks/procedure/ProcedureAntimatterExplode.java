/*
package astrotweaks.procedure;

import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

import astrotweaks.item.ItemVoidAntimatter;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class ProcedureAntimatterExplode extends ElementsAstrotweaksMod.ModElement {
	public ProcedureAntimatterExplode(ElementsAstrotweaksMod instance) {
		super(instance, 566);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		String[] keys = {"x", "y", "z", "world", "entity"};
		for (String key : keys) {
		    if (dependencies.get(key) == null) {
		        System.err.println("Failed to load dependency " + key + "!");
		        return;
		    }
		}
		
		Entity entity = (Entity) dependencies.get("entity");
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		if (((((entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHeldItemOffhand() : ItemStack.EMPTY)
				.getItem() == new ItemStack(ItemVoidAntimatter.block, (int) (1)).getItem())
				&& (((entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHeldItemMainhand() : ItemStack.EMPTY)
						.getItem() == new ItemStack(ItemVoidAntimatter.block, (int) (1)).getItem()))) {
			if (entity instanceof EntityLivingBase) {
				ItemStack _setstack = new ItemStack(Blocks.AIR, (int) (1));
				_setstack.setCount(1);
				((EntityLivingBase) entity).setHeldItem(EnumHand.OFF_HAND, _setstack);
				if (entity instanceof EntityPlayerMP)
					((EntityPlayerMP) entity).inventory.markDirty();
			}
			if (entity instanceof EntityLivingBase) {
				ItemStack _setstack = new ItemStack(Blocks.AIR, (int) (1));
				_setstack.setCount(1);
				((EntityLivingBase) entity).setHeldItem(EnumHand.MAIN_HAND, _setstack);
				if (entity instanceof EntityPlayerMP)
					((EntityPlayerMP) entity).inventory.markDirty();
			}
		} else if ((((entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHeldItemMainhand() : ItemStack.EMPTY)
				.getItem() == new ItemStack(ItemVoidAntimatter.block, (int) (1)).getItem())) {
			if (entity instanceof EntityLivingBase) {
				ItemStack _setstack = new ItemStack(Blocks.AIR, (int) (1));
				_setstack.setCount(1);
				((EntityLivingBase) entity).setHeldItem(EnumHand.MAIN_HAND, _setstack);
				if (entity instanceof EntityPlayerMP)
					((EntityPlayerMP) entity).inventory.markDirty();
			}
		}
		if (!world.isRemote) {
			world.createExplosion(null, (int) x, (int) y, (int) z, (float) 13, true);
		}
	}
}
*/