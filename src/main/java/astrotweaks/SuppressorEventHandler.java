package astrotweaks.qts;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ChatType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;

import astrotweaks.qts.BlockQTPSupressor;
import astrotweaks.qts.SuppressorManager;


@Mod.EventBusSubscriber
public class SuppressorEventHandler {
	@SubscribeEvent
	public static void onEnderTeleport(EnderTeleportEvent event) {
	    Entity e = event.getEntity();
	    World w = e.world;
	    BlockPos from = e.getPosition();
	    BlockPos to = new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ());
	    if (SuppressorManager.isTeleportationBlocked(w, from, to)) {
	        event.setCanceled(true);

	        if (e instanceof EntityPlayerMP) {
	            EntityPlayerMP player = (EntityPlayerMP) e;
	            act_msg(player);
	        }
	    }
	}

    @SubscribeEvent
    public static void onTravelToDimension(EntityTravelToDimensionEvent event) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (server == null) return;

	    Entity e = event.getEntity();
	    World w = e.world;
	    BlockPos from = e.getPosition();
	    if (SuppressorManager.isPositionBlocked(w, from)) {
	        event.setCanceled(true);
	        if (e instanceof EntityPlayerMP) {
	            EntityPlayerMP player = (EntityPlayerMP) e;
	            //player.sendMessage(new TextComponentString(TextFormatting.RED + "Moving between dimensions is prohibited here!"));
	            //player.connection.sendPacket(new SPacketChat(new TextComponentString(TextFormatting.RED + "Moving between dimensions is prohibited here!"), ChatType.GAME_INFO));
	        	sendMessage("Moving between dimensions is prohibited here!", player);
	        }
	        return;
	    }
        int targetDim = event.getDimension();
        WorldServer targetWorld = null;

        targetWorld = server.getWorld(targetDim);

        if (targetWorld == null) {
            event.setCanceled(true);
        }
    }
    // Interception of /tp and /teleport commands
    @SubscribeEvent
    public static void onCommand(CommandEvent event) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (server == null) return;
    	
        String name = event.getCommand().getName().toLowerCase();
        if (!name.equals("tp") && !name.equals("teleport") && !name.equals("dim")) return;

        ICommandSender sender = event.getSender();


        // If the sender is in the zone, we block it
	    if (sender instanceof Entity) {
	        Entity es = (Entity) sender;
	        if (!es.world.isRemote && SuppressorManager.isPositionBlocked(es.world, es.getPosition())) {
	            event.setCanceled(true);
	            sendMessage("Teleportation is prohibited here!", (Entity) sender);
	            //act_msg(es);
	            return;
	        }
	    }
        String[] args = event.getParameters();
        try {
            // Scenarios:
            // /tp x y z
            // /tp <player>
            // /tp <targetPlayer> <destPlayer>
            // /tp <player> x y z
            if (args.length == 3) {
                // coords: check target coords in the sender's world (if the sender is an Entity)
                if (sender instanceof Entity) {
                    World w = ((Entity) sender).world;
                    int x = parseInt(args[0]);
                    int y = parseInt(args[1]);
                    int z = parseInt(args[2]);
	                if (SuppressorManager.isPositionBlocked(w, new BlockPos(x, y, z))) {
	                    event.setCanceled(true);
	                    sendMessage("Teleportation has been interrupted!", sender);
	                    return;
	                }
                }
            } else if (args.length == 1) {
                // /tp <player>  -> tp to player
                EntityPlayerMP target = server.getPlayerList().getPlayerByUsername(args[0]);
                if (target != null && !target.world.isRemote && SuppressorManager.isPositionBlocked(target.world, target.getPosition())) {
                    event.setCanceled(true);
                    sendMessage("Teleportation has been interrupted!", target);
                    return;
                }
            } else if (args.length == 2) {
                // /tp <player1> <player2>
                EntityPlayerMP dest = server.getPlayerList().getPlayerByUsername(args[1]);
                if (dest != null && !dest.world.isRemote && SuppressorManager.isPositionBlocked(dest.world, dest.getPosition())) {
                    event.setCanceled(true);
                    sendMessage("Teleportation has been interrupted!", dest);
                    return;
                }
            } else if (args.length == 4) {
                // /tp <player> x y z
                EntityPlayerMP destPlayer = server.getPlayerList().getPlayerByUsername(args[0]);
                if (destPlayer != null) {
                    int x = parseInt(args[1]);
                    int y = parseInt(args[2]);
                    int z = parseInt(args[3]);
                    if (SuppressorManager.isPositionBlocked(destPlayer.world, new BlockPos(x, y, z))) {
                        event.setCanceled(true);
						sendMessage("Teleportation has been interrupted!", destPlayer);
                        return;
                    }
                }
            }
        } catch (NumberFormatException ignore) {
            // if the parsing failed, we don't block it here (ignore)
        }
    }
	private static void sendMessage(String mes, ICommandSender sender) {
	    TextComponentString msg = new TextComponentString(TextFormatting.RED + mes);
	    if (sender instanceof EntityPlayerMP) {
	        ((EntityPlayerMP) sender).connection.sendPacket(new SPacketChat(msg, ChatType.GAME_INFO));
	    } else {
	        sender.sendMessage(msg);
	    }
	}
	private static void sendMessage(String mes, Entity entity) {
	    if (entity instanceof EntityPlayerMP) {
	        TextComponentString msg = new TextComponentString(TextFormatting.RED + mes);
	        ((EntityPlayerMP) entity).connection.sendPacket(new SPacketChat(msg, ChatType.GAME_INFO));
	    }
	}

	private static int parseInt(String s) throws NumberFormatException {
	    return Integer.parseInt(s);
	}
	private static void act_msg(EntityPlayerMP player) {
	    player.connection.sendPacket(
	        new SPacketChat(new TextComponentString(TextFormatting.RED + "Teleportation is prohibited here!"), ChatType.GAME_INFO)
	    );
	}
}