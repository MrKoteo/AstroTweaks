/*
package astrotweaks.procedure;

import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import java.util.Map;

import astrotweaks.gui.GuiMTGUI;
import astrotweaks.ElementsAstrotweaksMod;

import astrotweaks.AstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class ProcedureMTInitProc extends ElementsAstrotweaksMod.ModElement {
	public ProcedureMTInitProc(ElementsAstrotweaksMod instance) {
		super(instance, 374);
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
		if (entity instanceof EntityPlayer) ((EntityPlayer) entity).openGui(AstrotweaksMod.instance, GuiMTGUI.GUIID, world, x, y, z);
	}
}
*/