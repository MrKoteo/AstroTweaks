package astrotweaks.procedure;

import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.command.ICommandSender;

import java.util.Map;
import java.util.HashMap;

import astrotweaks.ElementsAstrotweaksMod;

import astrotweaks.AstrotweaksModVariables;

@ElementsAstrotweaksMod.ModElement.Tag
public class ProcedureShowDeathsProc extends ElementsAstrotweaksMod.ModElement {
	public ProcedureShowDeathsProc(ElementsAstrotweaksMod instance) {
		super(instance, 397);
	}

	private static String getParam0(HashMap cmdparams) {
		if (cmdparams == null) return "";
		Object p = cmdparams.get("0");
		return p instanceof String ? (String) p : "";
	}

	private static void setShowDeaths(World world, boolean value) {
		AstrotweaksModVariables.MapVariables.get(world).showDeaths = value;
		AstrotweaksModVariables.MapVariables.get(world).syncData(world);
	}

	private static ICommandSender makeSender(final World world) {
		return new ICommandSender() {
			@Override public String getName() { return ""; }
			@Override public boolean canUseCommand(int permission, String command) { return true; }
			@Override public World getEntityWorld() { return world; }
			@Override public MinecraftServer getServer() { return world.getMinecraftServer(); }
			@Override public boolean sendCommandFeedback() { return false; }
			@Override public BlockPos getPosition() { return new BlockPos(0, 13, 0); }
			@Override public Vec3d getPositionVector() { return new Vec3d(0, 0, 0); }
		};
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("cmdparams") == null || dependencies.get("world") == null) {
			System.err.println("Failed to load dependencies!");
			return;
		}
		@SuppressWarnings("unchecked")
		HashMap cmdparams = (HashMap) dependencies.get("cmdparams");
		World world = (World) dependencies.get("world");

		String p0 = getParam0(cmdparams);
		if ("on".equals(p0) || "1".equals(p0)) {
			setShowDeaths(world, true);
		} else if ("off".equals(p0) || "0".equals(p0)) {
			setShowDeaths(world, false);
		} else {
			// toggle
			boolean cur = AstrotweaksModVariables.MapVariables.get(world).showDeaths;
			setShowDeaths(world, !cur);
		}

		if (!world.isRemote && world.getMinecraftServer() != null) {
			ICommandSender sender = makeSender(world);
			if (AstrotweaksModVariables.MapVariables.get(world).showDeaths) {
				world.getMinecraftServer().getCommandManager()
						.executeCommand(sender, "scoreboard objectives setdisplay sidebar deathCountX");
			} else {
				world.getMinecraftServer().getCommandManager()
						.executeCommand(sender, "scoreboard objectives setdisplay sidebar");
			}
		}
	}
}
