
package astrotweaks.command;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ICommand;
import net.minecraft.command.CommandHandler;
import net.minecraft.util.text.TextComponentString;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;

import astrotweaks.procedure.ProcedureATCP;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class CommandATCC extends ElementsAstrotweaksMod.ModElement {
	public CommandATCC(ElementsAstrotweaksMod instance) {
		super(instance, 681);
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
		public boolean checkPermission(MinecraftServer server, ICommandSender var1) {
			return true;
		}

		@Override
		public List getAliases() {
			return new ArrayList();
		}

		@Override
		public List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		@Override
		public String getName() {
			return "at_xpbox";
		}

		@Override
		public String getUsage(ICommandSender var1) {
			return "/at_xpbox [<arguments>]";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] cmd) {
			if (!(sender.getCommandSenderEntity() instanceof EntityPlayer)) {
			    sender.sendMessage(new TextComponentString("This command only for players!"));
			    return;
			}
			if (!sender.canUseCommand(2, getName())) {
		        sender.sendMessage(new TextComponentString("\u0423\u0020\u0432\u0430\u0441\u0020\u043d\u0435\u0442\u0020\u043f\u0440\u0430\u0432\u0020\u043d\u0430\u0020\u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435\u0020\u044d\u0442\u043e\u0439\u0020\u043a\u043e\u043c\u0430\u043d\u0434\u044b\u002e"));
		        return;
		    }
		
			int x = sender.getPosition().getX();
			int y = sender.getPosition().getY();
			int z = sender.getPosition().getZ();
			Entity entity = sender.getCommandSenderEntity();
			if (entity != null) {
				World world = entity.world;
				HashMap<String, String> cmdparams = new HashMap<>();
				int[] index = {0};
				Arrays.stream(cmd).forEach(param -> {
					cmdparams.put(Integer.toString(index[0]), param);
					index[0]++;
				});
				{
					Map<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("entity", entity);
					$_dependencies.put("cmdparams", cmdparams);
					ProcedureATCP.executeProcedure($_dependencies);
				}
			}
		}
	}
}
