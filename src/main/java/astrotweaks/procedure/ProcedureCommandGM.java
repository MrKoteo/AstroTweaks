package astrotweaks.procedure;

import net.minecraft.world.World;
import net.minecraft.world.GameType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.command.ICommandSender;

import java.util.Map;

public final class ProcedureCommandGM {
    public static void executeProcedure(Map<String, Object> dependencies) {
        Entity entity = (Entity) dependencies.get("entity");
        Map<?, ?> cmdparams = (Map<?, ?>) dependencies.get("cmdparams");

        if (entity == null || cmdparams == null) {
            System.err.println("Failed to exec command!");
            return;
        }

        String param = getParam(cmdparams, 0);
        if (param.isEmpty()) return;

        switch (param) {
            case "0":
            case "s":
                setGameModeAndNotify(entity, GameType.SURVIVAL, "command.gm0.text");
                break;
            case "1":
            case "c":
                setGameModeAndNotify(entity, GameType.CREATIVE, "command.gm1.text");
                break;
            case "2":
            case "a":
                setGameModeAndNotify(entity, GameType.ADVENTURE, "command.gm2.text");
                break;
            case "3":
            case "se":
            case "x":
                setGameModeAndNotify(entity, GameType.SPECTATOR, "command.gm3.text");
                break;
        }
    }

    private static String getParam(Map<?, ?> params, int index) {
        Object param = params.get(String.valueOf(index));
        return param instanceof String ? (String) param : "";
    }

    private static void setGameModeAndNotify(Entity entity, GameType gameType, String modeKey) {
        if (entity instanceof EntityPlayer) {
            ((EntityPlayer) entity).setGameType(gameType);
            sendGameModeChangeMessage(entity, modeKey);
        } else {
            sendNoPermissionMessage(entity);
        }
    }

    private static void sendGameModeChangeMessage(Entity entity, String modeKey) {
        if (entity.world.isRemote || entity.world.getMinecraftServer() == null) return;

        String command = "tellraw @s [{\"translate\":\"command.gm.changed\",\"with\":[{\"translate\":\"" + modeKey + "\"}]}]";

        entity.world.getMinecraftServer().getCommandManager().executeCommand(new ICommandSender() {
            @Override public String getName() { return ""; }
            @Override public boolean canUseCommand(int perm, String cmd) { return true; }
            @Override public World getEntityWorld() { return entity.world; }
            @Override public MinecraftServer getServer() { return entity.world.getMinecraftServer(); }
            @Override public boolean sendCommandFeedback() { return false; }
            @Override public BlockPos getPosition() { return entity.getPosition(); }
            @Override public Vec3d getPositionVector() { return new Vec3d(entity.posX, entity.posY, entity.posZ); }
            @Override public Entity getCommandSenderEntity() { return entity; }
        }, command);
    }

    private static void sendNoPermissionMessage(Entity entity) {
        if (entity.world.isRemote || entity.world.getMinecraftServer() == null) return;

        String command = "tellraw @s [{\"translate\":\"command.gm.no_permission\"}]";

        entity.world.getMinecraftServer().getCommandManager().executeCommand(new ICommandSender() {
            @Override public String getName() { return ""; }
            @Override public boolean canUseCommand(int perm, String cmd) { return true; }
            @Override public World getEntityWorld() { return entity.world; }
            @Override public MinecraftServer getServer() { return entity.world.getMinecraftServer(); }
            @Override public boolean sendCommandFeedback() { return false; }
            @Override public BlockPos getPosition() { return entity.getPosition(); }
            @Override public Vec3d getPositionVector() { return new Vec3d(entity.posX, entity.posY, entity.posZ); }
            @Override public Entity getCommandSenderEntity() { return entity; }
        }, command);
    }
}
