package astrotweaks.procedure;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.command.ICommandSender;

import java.util.Map;

import astrotweaks.ElementsAstrotweaksMod;

import astrotweaks.AstrotweaksModVariables;

@ElementsAstrotweaksMod.ModElement.Tag
public class ProcedureLoadWorld extends ElementsAstrotweaksMod.ModElement {
	public ProcedureLoadWorld(ElementsAstrotweaksMod instance) {
		super(instance, 317);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure LoadWorld!");
			return;
		}
		World world = (World) dependencies.get("world");
		if ((AstrotweaksModVariables.AstroTech_Environment)) {
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
				}, "scoreboard objectives add deathCountX deathCount \u0421\u043C\u0435\u0440\u0442\u0438");
			}
		}
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		World world = event.getWorld();
		java.util.HashMap<String, Object> dependencies = new java.util.HashMap<>();
		dependencies.put("world", world);
		dependencies.put("event", event);
		this.executeProcedure(dependencies);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
