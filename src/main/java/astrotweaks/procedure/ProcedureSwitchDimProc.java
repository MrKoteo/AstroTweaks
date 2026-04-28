package astrotweaks.procedure;

import net.minecraft.world.WorldServer;
import net.minecraft.world.Teleporter;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;
import java.util.HashMap;

import astrotweaks.world.DepthsDim;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public final class ProcedureSwitchDimProc extends ElementsAstrotweaksMod.ModElement {
	public ProcedureSwitchDimProc(ElementsAstrotweaksMod instance) {super(instance, 582);}

	private static boolean Slient = false;
	private static void sendMsg(EntityPlayerMP executor, String msg) {
	    if (executor != null && !Slient) executor.sendMessage(new TextComponentString(msg));
	    else System.out.println(msg);
	}

	public static void executeProcedure(Map<String, Object> dependencies, boolean sl) {
        if (dependencies.get("entity") == null || dependencies.get("cmdparams") == null) {
            System.err.println("Failed to exec proc!");
            return;
        }
        Slient = sl;

	    @SuppressWarnings("unchecked")
		Map<String, String> cmdparams = (Map<String, String>) dependencies.get("cmdparams");

	    Entity entity = (Entity) dependencies.get("entity");
	    EntityPlayerMP executor = null;
	    if (entity instanceof EntityPlayerMP) {
	        executor = (EntityPlayerMP) entity;
	    }
		if (entity.world.isRemote) return; // server side only

	    MinecraftServer mcServer = (executor != null) ? executor.getServer() : FMLCommonHandler.instance().getMinecraftServerInstance();
	    if (mcServer == null) {
	    	sendMsg(executor, "Server not found.");
		    //if (executor != null) executor.sendMessage(new TextComponentString("Server not found."));
		    //else System.out.println("Server not found.");
		    return;
		}

		String dimParam = getParam(cmdparams, 0);
		String playerNameParam = getParam(cmdparams, 1);
		String xParam = getParam(cmdparams, 2);
		String yParam = getParam(cmdparams, 3);
		String zParam = getParam(cmdparams, 4);

		if ("@s".equals(playerNameParam)) playerNameParam = null;

		// get target player
	    EntityPlayerMP targetPlayer = null;
	    if (playerNameParam != null && !playerNameParam.isEmpty()) {
	        targetPlayer = mcServer.getPlayerList().getPlayerByUsername(playerNameParam);
	        if (targetPlayer == null) {
	            String msg = "Player not found: " + playerNameParam;
	            //if (executor != null) executor.sendMessage(new TextComponentString(msg));
	            //else System.out.println(msg);
	            sendMsg(executor, msg);
	            return;
	        }
	    } else {
	        if (executor != null) targetPlayer = executor;
	        else {
	            System.out.println("You must specify a player name when running from console.");
	            return;
	        }
	    }

	    // check for tp to PlayerName
	    boolean teleportToPlayerMode = false;
	    String sourcePlayerName = null;
	    if (xParam != null && !xParam.isEmpty() && yParam.isEmpty() && zParam.isEmpty() && !isCoordinateToken(xParam)) {
	        // The third argument doesn't look like a coordinate, and the fourth and fifth are empty, which means it's the name of another player.
	        sourcePlayerName = xParam;
	        teleportToPlayerMode = true;
	    } else if (xParam != null && !xParam.isEmpty() && !isCoordinateToken(xParam)) {
	        // xParam is not a coordinate, but yParam or zParam are not empty - syntax error
	        String msg = "Invalid syntax: expected coordinates or player name.";
	        //if (executor != null) executor.sendMessage(new TextComponentString(msg));
	        //else System.out.println(msg);
	        sendMsg(executor, msg);
	        return;
	    }

	    Integer targetDim = null;
	    boolean hasCoords = false; // parse coords
	    double tx = 0, ty = 0, tz = 0;

	    if (teleportToPlayerMode) {
	        // tp to Player
	        EntityPlayerMP sourcePlayer = mcServer.getPlayerList().getPlayerByUsername(sourcePlayerName);
	        if (sourcePlayer == null) {
	            String msg = "Source player not found: " + sourcePlayerName;
	            //if (executor != null) executor.sendMessage(new TextComponentString(msg));
	            //else System.out.println(msg);
	            sendMsg(executor, msg);
	            return;
	        }
	        // Use DIM and Coords of sourcePlayer
	        targetDim = sourcePlayer.dimension;
	        tx = sourcePlayer.posX;
	        ty = sourcePlayer.posY;
	        tz = sourcePlayer.posZ;
	        hasCoords = true;
	        // optional message
	        String info = "Teleporting to " + sourcePlayerName + " at dim " + targetDim;
	        //if (executor != null) executor.sendMessage(new TextComponentString(info));
	        sendMsg(executor, info);
	    } else {
	        // Simple mode: parse DIM and Coords
	        targetDim = resolveDimensionId(dimParam);
	        if (targetDim == null) {
	            String msg = "Unknown dimension id: " + dimParam;
	            //if (executor != null) executor.sendMessage(new TextComponentString(msg));
	            //else System.out.println(msg);
	            sendMsg(executor, msg);
	            return;
	        }

	        // parse coords
	        try {
	            if (xParam != null && !xParam.isEmpty() &&
	                yParam != null && !yParam.isEmpty() &&
	                zParam != null && !zParam.isEmpty()) {
	                tx = parseCoord(xParam, targetPlayer.posX, false);
	                ty = parseCoord(yParam, targetPlayer.posY, true);
	                tz = parseCoord(zParam, targetPlayer.posZ, false);
	                hasCoords = true;
	            }
	        } catch (NumberFormatException e) {
	            String msg = "Invalid coordinates.";
	            //if (executor != null) executor.sendMessage(new TextComponentString(msg));
	            //else System.out.println(msg);
	            sendMsg(executor, msg);
	            return;
	        }
	    }
	    // save last pos
	    NBTTagCompound data = targetPlayer.getEntityData();
	    data.setDouble("lastDimPosX", targetPlayer.posX);
	    data.setDouble("lastDimPosY", targetPlayer.posY);
	    data.setDouble("lastDimPosZ", targetPlayer.posZ);
	    data.setInteger("lastDimId", targetPlayer.dimension);

		if (targetDim == targetPlayer.dimension) {
		    if (hasCoords) {
		        targetPlayer.setPositionAndUpdate(tx, ty, tz);
		        String msg = "Teleported player " + targetPlayer.getName() + " to dimension " + targetDim + " at " + tx + ", " + ty + ", " + tz;
		        //if (executor != null) executor.sendMessage(new TextComponentString(msg));
		        //else System.out.println(msg);
		        sendMsg(executor, msg);
		    } else {
		        String msg = "Player " + targetPlayer.getName() + " is already in dimension " + targetDim + ". No coordinates provided, nothing changed.";
		        //if (executor != null) executor.sendMessage(new TextComponentString(msg));
		        //else System.out.println(msg);
		        sendMsg(executor, msg);
		    }
		    return; // exit to avoid executing the switch dim code
		}

	    // use transferPlayerToDimension - it will load the world automatically
	    WorldServer playerWorld = (WorldServer) targetPlayer.world;
	    mcServer.getPlayerList().transferPlayerToDimension(targetPlayer, targetDim, new TeleporterDirectWrapper(playerWorld, targetDim));

	    // after the transfer we will receive WorldServer via the server (guaranteed loaded)
	    WorldServer targetWorld = mcServer.getWorld(targetDim);
	    if (targetWorld == null) {
	        String msg = "Failed to load target world.";
	        //if (targetPlayer != null) targetPlayer.sendMessage(new TextComponentString(msg));
	        //else System.out.println(msg);
	        sendMsg(executor, msg);
	        return;
	    }
		if (hasCoords) { // set pos (teleportTo of EntityPlayerMP)
		    targetPlayer.setPositionAndUpdate(tx, ty, tz);
		}
		String msg = "Teleported player " + targetPlayer.getName() + " to dimension " + targetDim + (hasCoords ? (" at " + tx + ", " + ty + ", " + tz) : ".");
	    //if (executor != null) executor.sendMessage(new TextComponentString(msg));
	    //else System.out.println(msg);
	    sendMsg(executor, msg);

	}

    private static boolean isCoordinateToken(String token) {
	    if (token == null || token.isEmpty()) return false;
	    char c = token.charAt(0);
	    // Allow: tilda, digits, minus, dot
	    return c == '~' || c == '-' || c == '.' || (c >= '0' && c <= '9');
	}
	// get param for index
	private static String getParam(Map<String, String> cmdparams, int index) {
	    if (cmdparams == null) return "";
	    return cmdparams.getOrDefault(Integer.toString(index), "");
	}

	private static double parseCoord(String token, double base, boolean isY) throws NumberFormatException {
	    token = token.trim();
	    if (token.startsWith("~")) {
	        String rest = token.length() == 1 ? "" : token.substring(1).trim();
	        if (rest.isEmpty()) return base;        // "~" -> base (no shift)
	        return base + Double.parseDouble(rest); // "~1" -> base+1 ; "~1.5" -> base+1.5
	    } else {
	        // absolute coords
	        if (token.contains(".") || token.contains(",")) {
	            return Double.parseDouble(token.replace(',', '.'));
	        } else {
	            // of center of block
	            if (!isY) {return Double.parseDouble(token) + 0.5; }
	            else {return Double.parseDouble(token); }
	        }
	    }
	}
	// Dimension ID resolution: support for aliases
	private static Integer resolveDimensionId(String raw) {
		if (raw == null || raw.isEmpty()) return null;
		raw = raw.trim();
		// specials
		if (raw.equals("-6000") || raw.equals("-2"))
			return DepthsDim.DIMID;
		if (raw.equals("cavern"))
			return DepthsDim.DIMID;
		try {
			return Integer.parseInt(raw);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	private static class TeleporterDirectWrapper extends Teleporter {
		private final int dim;
		private final MinecraftServer server;
		public TeleporterDirectWrapper(WorldServer world, int dimension) {
			// use the world for the current server dimension (the Teleporter constructor
			// requires a WorldServer; we'll take the player's world before the transition)
			super(world);
			this.dim = dimension;
			this.server = world.getMinecraftServer();
		}
		@Override public void placeInPortal(Entity entity, float yawRotation) {}
		@Override public boolean placeInExistingPortal(Entity entity, float yawRotation) {return true;}
		@Override public boolean makePortal(Entity entity) {return true;}
	}
}
