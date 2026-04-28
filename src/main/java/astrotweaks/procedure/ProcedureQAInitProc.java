/*
package astrotweaks.procedure;

import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import java.util.Map;

import astrotweaks.gui.GuiQAGUI;

import astrotweaks.ElementsAstrotweaksMod;

import astrotweaks.AstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class ProcedureQAInitProc extends ElementsAstrotweaksMod.ModElement {
	public ProcedureQAInitProc(ElementsAstrotweaksMod instance) {
		super(instance, 499);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		String[] keys = {"x", "y", "z", "world", "entity"};
		for (String key : keys) {
		    if (dependencies.get(key) == null) {
		        System.err.println("Failed to load dependency " + key + " for proc QAInitProc!");
		        return;
		    }
		}

		Entity entity = (Entity) dependencies.get("entity");
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		if (entity instanceof EntityPlayer)
			((EntityPlayer) entity).openGui(AstrotweaksMod.instance, GuiQAGUI.GUIID, world, x, y, z);
	}
}
*/