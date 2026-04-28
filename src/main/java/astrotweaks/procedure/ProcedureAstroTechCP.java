package astrotweaks.procedure;

import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.command.ICommandSender;

import java.util.Map;
import java.util.HashMap;

import astrotweaks.ElementsAstrotweaksMod;

import astrotweaks.AstrotweaksModVariables;

@ElementsAstrotweaksMod.ModElement.Tag
public class ProcedureAstroTechCP extends ElementsAstrotweaksMod.ModElement {
	public ProcedureAstroTechCP(ElementsAstrotweaksMod instance) {
		super(instance, 355);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure AstroTechCP!");
			return;
		}
		if (dependencies.get("cmdparams") == null) {
			System.err.println("Failed to load dependency cmdparams for procedure AstroTechCP!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure AstroTechCP!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		HashMap cmdparams = (HashMap) dependencies.get("cmdparams");
		World world = (World) dependencies.get("world");
		if ((AstrotweaksModVariables.AstroTech_Environment)) {
			if ((((new Object() {
				public String getText() {
					String param = (String) cmdparams.get("0");
					if (param != null) {
						return param;
					}
					return "";
				}
			}.getText())).equals("info"))) {
				{
					Entity _ent = entity;
					if (!_ent.world.isRemote && _ent.world.getMinecraftServer() != null) {
						_ent.world.getMinecraftServer().getCommandManager().executeCommand(new ICommandSender() {
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
								return _ent.world;
							}

							@Override
							public MinecraftServer getServer() {
								return _ent.world.getMinecraftServer();
							}

							@Override
							public boolean sendCommandFeedback() {
								return false;
							}

							@Override
							public BlockPos getPosition() {
								return _ent.getPosition();
							}

							@Override
							public Vec3d getPositionVector() {
								return new Vec3d(_ent.posX, _ent.posY, _ent.posZ);
							}

							@Override
							public Entity getCommandSenderEntity() {
								return _ent;
							}
						}, "tellraw @p [{\"text\":\"\u0414\u043E\u0431\u0440\u043E \u043F\u043E\u0436\u0430\u043B\u043E\u0432\u0430\u0442\u044C \u0432 \u043C\u0438\u0440 AstroTech!\",\"color\":\"light_purple\",\"bold\":true}]");
					}
				}
				{
					Entity _ent = entity;
					if (!_ent.world.isRemote && _ent.world.getMinecraftServer() != null) {
						_ent.world.getMinecraftServer().getCommandManager().executeCommand(new ICommandSender() {
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
								return _ent.world;
							}

							@Override
							public MinecraftServer getServer() {
								return _ent.world.getMinecraftServer();
							}

							@Override
							public boolean sendCommandFeedback() {
								return false;
							}

							@Override
							public BlockPos getPosition() {
								return _ent.getPosition();
							}

							@Override
							public Vec3d getPositionVector() {
								return new Vec3d(_ent.posX, _ent.posY, _ent.posZ);
							}

							@Override
							public Entity getCommandSenderEntity() {
								return _ent;
							}
						}, "tellraw @p [{\"text\":\"\u0412 \u044D\u0442\u043E\u043C \u043C\u0438\u0440\u0435 \u0438\u0441\u043F\u043E\u043B\u044C\u0437\u0443\u0435\u0442\u0441\u044F \u043E\u0441\u043E\u0431\u0430\u044F \u0441\u0438\u0441\u0442\u0435\u043C\u0430 \u0440\u0430\u0437\u0432\u0438\u0442\u0438\u044F, \u043F\u043E\u044D\u0442\u043E\u043C\u0443\",\"color\":\"aqua\"}]");
					}
				}
				{
					Entity _ent = entity;
					if (!_ent.world.isRemote && _ent.world.getMinecraftServer() != null) {
						_ent.world.getMinecraftServer().getCommandManager().executeCommand(new ICommandSender() {
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
								return _ent.world;
							}

							@Override
							public MinecraftServer getServer() {
								return _ent.world.getMinecraftServer();
							}

							@Override
							public boolean sendCommandFeedback() {
								return false;
							}

							@Override
							public BlockPos getPosition() {
								return _ent.getPosition();
							}

							@Override
							public Vec3d getPositionVector() {
								return new Vec3d(_ent.posX, _ent.posY, _ent.posZ);
							}

							@Override
							public Entity getCommandSenderEntity() {
								return _ent;
							}
						}, "tellraw @p [{\"text\":\" \u043A\u0440\u0430\u0439\u043D\u0435 \u0440\u0435\u043A\u043E\u043C\u0435\u043D\u0434\u0443\u0435\u0442\u0441\u044F \u0440\u0430\u0437\u0432\u0438\u0432\u0430\u0442\u044C\u0441\u044F \u0441\u043E\u0433\u043B\u0430\u0441\u043D\u043E \u0434\u0435\u0440\u0435\u0432\u0443 \u0440\u0430\u0437\u0432\u0438\u0442\u0438\u044F (\u043A\u0432\u0435\u0441\u0442\u043E\u0432)\",\"color\":\"aqua\"}]");
					}
				}
				{
					Entity _ent = entity;
					if (!_ent.world.isRemote && _ent.world.getMinecraftServer() != null) {
						_ent.world.getMinecraftServer().getCommandManager().executeCommand(new ICommandSender() {
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
								return _ent.world;
							}

							@Override
							public MinecraftServer getServer() {
								return _ent.world.getMinecraftServer();
							}

							@Override
							public boolean sendCommandFeedback() {
								return false;
							}

							@Override
							public BlockPos getPosition() {
								return _ent.getPosition();
							}

							@Override
							public Vec3d getPositionVector() {
								return new Vec3d(_ent.posX, _ent.posY, _ent.posZ);
							}

							@Override
							public Entity getCommandSenderEntity() {
								return _ent;
							}
						}, "tellraw @p [{\"text\":\"\"}]");
					}
				}
				{
					Entity _ent = entity;
					if (!_ent.world.isRemote && _ent.world.getMinecraftServer() != null) {
						_ent.world.getMinecraftServer().getCommandManager().executeCommand(new ICommandSender() {
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
								return _ent.world;
							}

							@Override
							public MinecraftServer getServer() {
								return _ent.world.getMinecraftServer();
							}

							@Override
							public boolean sendCommandFeedback() {
								return false;
							}

							@Override
							public BlockPos getPosition() {
								return _ent.getPosition();
							}

							@Override
							public Vec3d getPositionVector() {
								return new Vec3d(_ent.posX, _ent.posY, _ent.posZ);
							}

							@Override
							public Entity getCommandSenderEntity() {
								return _ent;
							}
						}, "tellraw @p [{\"text\":\"[i]\",\"color\":\"white\",\"bold\":true},{\"text\":\" \u0422\u0430\u043A\u0436\u0435 \u0432 \u043E\u043F\u0438\u0441\u0430\u043D\u0438\u0438 \u043A\u0432\u0435\u0441\u0442\u043E\u0432 \u0431\u044B\u0432\u0430\u0435\u0442 \u043F\u043E\u043B\u0435\u0437\u043D\u0430\u044F \u0438\u043D\u0444\u043E\u0440\u043C\u0430\u0446\u0438\u044F!\",\"color\":\"white\",\"bold\":false}]");
					}
				}
				{
					Entity _ent = entity;
					if (!_ent.world.isRemote && _ent.world.getMinecraftServer() != null) {
						_ent.world.getMinecraftServer().getCommandManager().executeCommand(new ICommandSender() {
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
								return _ent.world;
							}

							@Override
							public MinecraftServer getServer() {
								return _ent.world.getMinecraftServer();
							}

							@Override
							public boolean sendCommandFeedback() {
								return false;
							}

							@Override
							public BlockPos getPosition() {
								return _ent.getPosition();
							}

							@Override
							public Vec3d getPositionVector() {
								return new Vec3d(_ent.posX, _ent.posY, _ent.posZ);
							}

							@Override
							public Entity getCommandSenderEntity() {
								return _ent;
							}
						}, "tellraw @p [{\"text\":\" \u041A\u0440\u0430\u0442\u043A\u0430\u044F \u0438\u043D\u0441\u0442\u0440\u0443\u043A\u0446\u0438\u044F \u0435\u0441\u0442\u044C \u0432 \u0441\u043F\u0435\u0446\u0438\u0430\u043B\u044C\u043D\u043E\u043C \u0440\u0430\u0437\u0434\u0435\u043B\u0435 \u043A\u0432\u0435\u0441\u0442\u043E\u0432.\",\"color\":\"white\",\"bold\":false}]");
					}
				}
				{
					Entity _ent = entity;
					if (!_ent.world.isRemote && _ent.world.getMinecraftServer() != null) {
						_ent.world.getMinecraftServer().getCommandManager().executeCommand(new ICommandSender() {
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
								return _ent.world;
							}

							@Override
							public MinecraftServer getServer() {
								return _ent.world.getMinecraftServer();
							}

							@Override
							public boolean sendCommandFeedback() {
								return false;
							}

							@Override
							public BlockPos getPosition() {
								return _ent.getPosition();
							}

							@Override
							public Vec3d getPositionVector() {
								return new Vec3d(_ent.posX, _ent.posY, _ent.posZ);
							}

							@Override
							public Entity getCommandSenderEntity() {
								return _ent;
							}
						}, "tellraw @p [{\"text\":\"\"}]");
					}
				}
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).addExperience((int) 1);
			} else if ((((new Object() {
				public String getText() {
					String param = (String) cmdparams.get("0");
					if (param != null) {
						return param;
					}
					return "";
				}
			}.getText())).equals("on"))) {
				{
					Map<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("world", world);
					ProcedureATEnvInit.executeProcedure($_dependencies);
				}
			} else {
				{
					Entity _ent = entity;
					if (!_ent.world.isRemote && _ent.world.getMinecraftServer() != null) {
						_ent.world.getMinecraftServer().getCommandManager().executeCommand(new ICommandSender() {
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
								return _ent.world;
							}

							@Override
							public MinecraftServer getServer() {
								return _ent.world.getMinecraftServer();
							}

							@Override
							public boolean sendCommandFeedback() {
								return false;
							}

							@Override
							public BlockPos getPosition() {
								return _ent.getPosition();
							}

							@Override
							public Vec3d getPositionVector() {
								return new Vec3d(_ent.posX, _ent.posY, _ent.posZ);
							}

							@Override
							public Entity getCommandSenderEntity() {
								return _ent;
							}
						}, "tellraw @s [{\"text\":\"Invalid command argument!\",\"color\":\"red\"}]");
					}
				}
			}
		} else {
			{
				Entity _ent = entity;
				if (!_ent.world.isRemote && _ent.world.getMinecraftServer() != null) {
					_ent.world.getMinecraftServer().getCommandManager().executeCommand(new ICommandSender() {
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
							return _ent.world;
						}

						@Override
						public MinecraftServer getServer() {
							return _ent.world.getMinecraftServer();
						}

						@Override
						public boolean sendCommandFeedback() {
							return false;
						}

						@Override
						public BlockPos getPosition() {
							return _ent.getPosition();
						}

						@Override
						public Vec3d getPositionVector() {
							return new Vec3d(_ent.posX, _ent.posY, _ent.posZ);
						}

						@Override
						public Entity getCommandSenderEntity() {
							return _ent;
						}
					}, "tellraw @s [{\"text\":\"No modules\",\"color\":\"red\"}]");
				}
			}
		}
	}
}
