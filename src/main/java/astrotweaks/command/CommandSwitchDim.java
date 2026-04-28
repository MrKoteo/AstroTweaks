package astrotweaks.command;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import net.minecraft.util.math.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ICommand;
import net.minecraft.util.text.TextComponentString;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;

import astrotweaks.procedure.ProcedureSwitchDimProc;
import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class CommandSwitchDim extends ElementsAstrotweaksMod.ModElement {
	public CommandSwitchDim(ElementsAstrotweaksMod instance) {
		super(instance, 612);
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
		@Override public List getAliases() {return new ArrayList();}
		@Override public List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {return new ArrayList();}
		@Override public boolean isUsernameIndex(String[] args, int index) { return index == 1; }
		@Override public String getName() { return "dim"; }
		@Override
		public String getUsage(ICommandSender sender) {
			return "/dim <dimID> <playerName|@s> [x y z]|<playerName>";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] cmd) {
			// check perms (level 2 = OP)
			if (!sender.canUseCommand(2, getName())) {
				sender.sendMessage(new TextComponentString("\u0423\u0020\u0432\u0430\u0441\u0020\u043d\u0435\u0442\u0020\u043f\u0440\u0430\u0432\u0020\u043d\u0430\u0020\u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435\u0020\u044d\u0442\u043e\u0439\u0020\u043a\u043e\u043c\u0430\u043d\u0434\u044b\u002e"));
				return;
			}
			// get source Entity may be null FIXME:or console)
			Entity sourceEntity = sender.getCommandSenderEntity();
			// cmdparams
			HashMap<String, String> cmdparams = new HashMap<>();
			for (int i = 0; i < cmd.length; i++) {
				cmdparams.put(Integer.toString(i), cmd[i]);
			}
			Map<String, Object> $_dependencies = new HashMap<>();
			$_dependencies.put("entity", sourceEntity); // null for console
			$_dependencies.put("cmdparams", cmdparams);
			$_dependencies.put("server", server);
			ProcedureSwitchDimProc.executeProcedure($_dependencies, false);
		}
	}
}
