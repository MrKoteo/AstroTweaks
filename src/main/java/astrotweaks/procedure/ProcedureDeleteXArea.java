/*
package astrotweaks.procedure;

import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.command.ICommandSender;

import java.util.Map;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class ProcedureDeleteXArea extends ElementsAstrotweaksMod.ModElement {
	public ProcedureDeleteXArea(ElementsAstrotweaksMod instance) {
		super(instance, 699);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		String[] keys = {"x", "y", "z", "world"};
		for (String key : keys) {
		    if (dependencies.get(key) == null) {
		        System.err.println("Failed to load dependency " + key + "!");
		        return;
		    }
		}

		final int x = (int) dependencies.get("x");
		final int y = (int) dependencies.get("y");
		final int z = (int) dependencies.get("z");
		final World world = (World) dependencies.get("world");

		if (world.isRemote || world.getMinecraftServer() == null) return;

		final ICommandSender sender = new ICommandSender() {
			@Override public String getName() { return ""; }
			@Override public boolean canUseCommand(int perm, String cmd) { return true; }
			@Override public World getEntityWorld() { return world; }
			@Override public MinecraftServer getServer() { return world.getMinecraftServer(); }
			@Override public boolean sendCommandFeedback() { return false; }
			@Override public BlockPos getPosition() { return new BlockPos(x, y, z); }
			@Override public Vec3d getPositionVector() { return new Vec3d(x, y, z); }
		};

		final String[] cmds = {
			"fill ~10 ~10 ~10 ~-10 ~9 ~-10 air 0 replace",
			"fill ~10 ~8 ~10 ~-10 ~6 ~-10 air 0 replace",
			"fill ~10 ~5 ~10 ~-10 ~3 ~-10 air 0 replace",
			"fill ~10 ~2 ~10 ~-10 ~-2 ~-10 air 0 replace",
			"fill ~10 ~-3 ~10 ~-10 ~-5 ~-10 air 0 replace",
			"fill ~10 ~-6 ~10 ~-10 ~-8 ~-10 air 0 replace",
			"fill ~10 ~-9 ~10 ~-10 ~-10 ~-10 air 0 replace"
		};

		for (String c : cmds) world.getMinecraftServer().getCommandManager().executeCommand(sender, c);
	}
}
*/