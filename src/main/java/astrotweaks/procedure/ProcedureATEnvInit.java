package astrotweaks.procedure;

import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.command.ICommandSender;

import java.util.Map;

import astrotweaks.ElementsAstrotweaksMod;

import astrotweaks.AstrotweaksModVariables;

@ElementsAstrotweaksMod.ModElement.Tag
public class ProcedureATEnvInit extends ElementsAstrotweaksMod.ModElement {
	public ProcedureATEnvInit(ElementsAstrotweaksMod instance) {
		super(instance, 354);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure ATEnvInit!");
			return;
		}
		World world = (World) dependencies.get("world");
		if ((AstrotweaksModVariables.AstroTech_Environment)) {
			AstrotweaksModVariables.AstroTech_Environment = (boolean) (true);
			if (!world.isRemote && world.getMinecraftServer() != null) {
				world.getMinecraftServer().getCommandManager().executeCommand(new ICommandSender() {
					@Override
					public String getName() {
						return "";
					}

					@Override
					public boolean canUseCommand(int permission, String command) {
						return true;
					}

					@Override
					public World getEntityWorld() {
						return world;
					}

					@Override
					public MinecraftServer getServer() {
						return world.getMinecraftServer();
					}

					@Override
					public boolean sendCommandFeedback() {
						return false;
					}

					@Override
					public BlockPos getPosition() {
						return new BlockPos((int) 0, (int) 0, (int) 0);
					}

					@Override
					public Vec3d getPositionVector() {
						return new Vec3d(0, 0, 0);
					}
				}, "gamerule announceAdvancements true");
			}
			if (!world.isRemote && world.getMinecraftServer() != null) {
				world.getMinecraftServer().getCommandManager().executeCommand(new ICommandSender() {
					@Override
					public String getName() {
						return "";
					}

					@Override
					public boolean canUseCommand(int permission, String command) {
						return true;
					}

					@Override
					public World getEntityWorld() {
						return world;
					}

					@Override
					public MinecraftServer getServer() {
						return world.getMinecraftServer();
					}

					@Override
					public boolean sendCommandFeedback() {
						return false;
					}

					@Override
					public BlockPos getPosition() {
						return new BlockPos((int) 0, (int) 0, (int) 0);
					}

					@Override
					public Vec3d getPositionVector() {
						return new Vec3d(0, 0, 0);
					}
				}, "gamerule disableElytraMovementCheck true");
			}
		}
	}
}
