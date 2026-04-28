package astrotweaks.command;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.Entity;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ICommand;
import net.minecraft.command.CommandException;
import net.minecraft.util.text.TextComponentString;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import astrotweaks.procedure.ProcedureRsummonProc;
import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class CommandRsummon extends ElementsAstrotweaksMod.ModElement {
    public CommandRsummon(ElementsAstrotweaksMod instance) {
        super(instance, 552);
    }
    @Override
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandHandler());
    }

    public static class CommandHandler implements ICommand {
        @Override
        public int compareTo(ICommand c) {
            return getName().compareTo(c.getName());
        }
        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
            return true;
        }
        @Override
        public List<String> getAliases() {
            return new ArrayList<>();
        }
        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
            return new ArrayList<>();
        }
        @Override
        public boolean isUsernameIndex(String[] args, int index) {
            return false;
        }
        @Override
        public String getName() {
            return "rsummon";
        }
        @Override
        public String getUsage(ICommandSender sender) {
            return "/rsummon <entity_id> [x y z] [count] {nbt}";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (!sender.canUseCommand(2, getName())) {
		        sender.sendMessage(new TextComponentString("\u0423\u0020\u0432\u0430\u0441\u0020\u043d\u0435\u0442\u0020\u043f\u0440\u0430\u0432\u0020\u043d\u0430\u0020\u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435\u0020\u044d\u0442\u043e\u0439\u0020\u043a\u043e\u043c\u0430\u043d\u0434\u044b\u002e"));
		        return;
		    }
            if (args.length < 1) {
                sender.sendMessage(new TextComponentString("Usage: " + getUsage(sender)));
                return;
            }
            World world = sender.getEntityWorld();
            Entity entity = sender.getCommandSenderEntity();
            Vec3d posVec = sender.getPositionVector();
            double currentX = posVec.x;
            double currentY = posVec.y;
            double currentZ = posVec.z;

            String entityId = args[0];
            int index = 1;

            double spawnX = currentX;
            double spawnY = currentY;
            double spawnZ = currentZ;
            boolean hasPos = false;

            // Check if next 3 args look like coordinates
            if (args.length >= index + 3 && isCoordinate(args[index]) && isCoordinate(args[index + 1]) && isCoordinate(args[index + 2])) {
                try {
                    spawnX = parseCoordinate(args[index], currentX);
                    spawnY = parseCoordinate(args[index + 1], currentY);
                    spawnZ = parseCoordinate(args[index + 2], currentZ);
                    hasPos = true;
                } catch (NumberFormatException e) {
                    throw new CommandException("Invalid coords: " + e.getMessage());
                }
                index += 3;
            }

            // Optional: Center on block if no pos specified or relative (~ without offset)
            // Comment out if not needed
            if (!hasPos) {
                spawnX = Math.floor(spawnX) + 0.5;
                spawnZ = Math.floor(spawnZ) + 0.5;
                // spawnY remains as is (bottom of entity)
            }

            int count = 1;
            if (args.length > index && isInteger(args[index])) {
                try {
                    count = Integer.parseInt(args[index]);
                    if (count < 1) count = 1; // Prevent negative/zero
                    index++;
                } catch (NumberFormatException e) {
                    // Ignore and keep count=1
                }
            }

            String nbt = "";
            if (args.length > index) {
                nbt = String.join(" ", Arrays.copyOfRange(args, index, args.length));
            }

            // Collect dependencies
            Map<String, Object> dependencies = new HashMap<>();
            dependencies.put("world", world);
            dependencies.put("entity", entity);
            dependencies.put("entity_id", entityId);
            dependencies.put("x", spawnX);
            dependencies.put("y", spawnY);
            dependencies.put("z", spawnZ);
            dependencies.put("count", count);
            dependencies.put("nbt", nbt);

            // Call procedure
            ProcedureRsummonProc.executeProcedure(dependencies);
        }

        // Helper: Check if string looks like a coordinate (number, ~offset, ^offset)
        private boolean isCoordinate(String s) {
            if (s.startsWith("~") || s.startsWith("^")) {
                String rest = s.substring(1);
                return rest.isEmpty() || isNumeric(rest);
            }
            return isNumeric(s);
        }

        // Helper: Parse coordinate with ~ support
        private double parseCoordinate(String s, double current) throws NumberFormatException {
            if (s.startsWith("~")) {
                String rest = s.substring(1);
                double offset = rest.isEmpty() ? 0 : Double.parseDouble(rest);
                return current + offset;
            } else if (s.startsWith("^")) {
                throw new NumberFormatException("Look-based (^) coords not supported!");
            } else {
                return Double.parseDouble(s);
            }
        }

        // Helper: Check if string is numeric (double)
        private boolean isNumeric(String s) {
            try {
                Double.parseDouble(s);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        // Helper: Check if string is integer (for count)
        private boolean isInteger(String s) {
            try {
                Integer.parseInt(s);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
}