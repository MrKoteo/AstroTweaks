/*
package astrotweaks.procedure;

import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.command.ICommandSender;

import java.util.Map;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class ProcedureUnknownblockClick extends ElementsAstrotweaksMod.ModElement {
	public ProcedureUnknownblockClick(ElementsAstrotweaksMod instance) {
		super(instance, 359);
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
					return new BlockPos((int) x, (int) y, (int) z);
				}

				@Override
				public Vec3d getPositionVector() {
					return new Vec3d(x, y, z);
				}
			}, "tellraw @a[distance=..4] {\"text\":\"You don't know what it is yet!\",\"color\":\"red\"}");
		}
		if (entity instanceof EntityPlayer)
			((EntityPlayer) entity).closeScreen();
	}
}
*/