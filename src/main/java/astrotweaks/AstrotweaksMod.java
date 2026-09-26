
package astrotweaks;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraft.util.ResourceLocation;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;

import astrotweaks.gui.GUIHandler;
import astrotweaks.world.BushDecorator;
import astrotweaks.world.NaturesPower.GrassGrowth;
import astrotweaks.event.EventBreakBlock;
import astrotweaks.gameplay.RealisticBreak;
import astrotweaks.recipe.RecipeHandler;




@Mod(modid = AstrotweaksMod.MODID, version = AstrotweaksMod.VERSION, dependencies = "after:simpledifficulty")
public class AstrotweaksMod {

	public static final String MODID = "astrotweaks";
	public static final String VERSION = "Beta-6.3";


	public static final SimpleNetworkWrapper PACKET_HANDLER = NetworkRegistry.INSTANCE.newSimpleChannel("astrotweaks:a");
	static {
		PACKET_HANDLER.registerMessage(ModVariables.WorldSavedDataSyncMessageHandler.class, ModVariables.WorldSavedDataSyncMessage.class, 0, Side.SERVER);
		PACKET_HANDLER.registerMessage(ModVariables.WorldSavedDataSyncMessageHandler.class, ModVariables.WorldSavedDataSyncMessage.class, 0, Side.CLIENT);
	}
	// MessageMultiverse (1) и TDARK-пакеты (20/21) регистрируются в preInit ТОЛЬКО при включённом MULTIVERSE/TDARK
	@SidedProxy(clientSide = "astrotweaks.ClientProxyAstrotweaksMod", serverSide = "astrotweaks.ServerProxyAstrotweaksMod")
	public static IProxyAstrotweaksMod proxy;
	@Mod.Instance(MODID)
	public static AstrotweaksMod instance;

	// ####################################################################################################

	public AstrotweaksMod() {
		ConfigManager.loadConfig();
	}

	@Mod.EventHandler
	public void construction(FMLConstructionEvent event) {
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);

		astrotweaks.ModVariables.preInit();


		

		//GameRegistry.registerWorldGenerator(elements, 5);
		//GameRegistry.registerFuelHandler(elements);

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GUIHandler.GuiHandler());

		proxy.preInit(event);

		if (ModVariables.Enable_Depths_Dimension) {
			astrotweaks.world.DepthsDim.preInit();
		}


		if (ModVariables.Enable_SnowVillages) {
	        astrotweaks.world.SnowVillage.preInit();
		}
		if (ModVariables.Enable_ForestVillages) {
	        astrotweaks.world.ForestVillage.preInit();
		}

		if (ModVariables.Enable_Ground_Elements) astrotweaks.world.DecorateGroundElements.register();
		astrotweaks.world.BlockWorldGen.register();

		// UPDATE class vars
		astrotweaks.block.BlockGroundRock1.updVars();
		astrotweaks.block.BlockGroundRock2.updVars();
		astrotweaks.block.BlockGroundStick.updVars();
		astrotweaks.world.BushDecorator.updVars();

		GrassGrowth.updVars();
		astrotweaks.world.NaturesPower.BlockMossing.updVars();
		astrotweaks.tech.qts.BlockQTPSupressor.updVars();


		GameRegistry.registerTileEntity(astrotweaks.block.mirage.MirageTileEntity.class, MODID + ":te_m");






		// BUS  events
		MinecraftForge.EVENT_BUS.register(new astrotweaks.event.EventLoadWorld());

		// Multiverse: all events live on the Forge bus (FML bus == Forge bus in 1.12.2).
		if (ModVariables.MULTIVERSE) {
			AstrotweaksMod.PACKET_HANDLER.registerMessage(astrotweaks.Multiverse.MessageMultiverse.ClientHandler.class, astrotweaks.Multiverse.MessageMultiverse.class, 1, Side.CLIENT);
			MinecraftForge.EVENT_BUS.register(new astrotweaks.Multiverse.MultiverseEvents());
		}
		if (ModVariables.MULTIVERSE && ModVariables.Enable_TDARK) {
			AstrotweaksMod.PACKET_HANDLER.registerMessage(astrotweaks.tech.tdark.TDArkGUI.TDArkActionMessageHandler.class, astrotweaks.tech.tdark.TDArkGUI.TDArkActionMessage.class, 20, Side.SERVER);
			AstrotweaksMod.PACKET_HANDLER.registerMessage(astrotweaks.tech.tdark.TDArkGUI.GUIButtonPressedMessageHandler.class, astrotweaks.tech.tdark.TDArkGUI.GUIButtonPressedMessage.class, 21, Side.SERVER);
		}


		if (ModVariables.Extra_Fuels) MinecraftForge.EVENT_BUS.register(new astrotweaks.recipe.CombinedFuelHandler());
		if (ModVariables.Enable_Depths_Dimension) MinecraftForge.EVENT_BUS.register(new astrotweaks.world.CavernMobModifier());
		if (ModVariables.Enable_StepUp) MinecraftForge.EVENT_BUS.register(new astrotweaks.gameplay.StepUp());
		if (ModVariables.Food_Negative_Effects) MinecraftForge.EVENT_BUS.register(new astrotweaks.procedure.FoodEffectHandler());
		if (ModVariables.GG_ENABLED) MinecraftForge.EVENT_BUS.register(new GrassGrowth());
		if (ModVariables.BM_ENABLED) MinecraftForge.EVENT_BUS.register(new astrotweaks.world.NaturesPower.BlockMossing());
    	if (ModVariables.Enable_Depths_Dim_Bedrock_TP) MinecraftForge.EVENT_BUS.register(new astrotweaks.procedure.DepthsEnter());
		if (ModVariables.No_Potion_Icons) MinecraftForge.EVENT_BUS.register(new astrotweaks.gameplay.NoEffectIcons());
		
		MinecraftForge.EVENT_BUS.register(astrotweaks.block.mirage.MirageRemovalQueue.class);

		MinecraftForge.EVENT_BUS.register(new astrotweaks.item.TotemOfGod.TotemOfGodEvents());
		MinecraftForge.EVENT_BUS.register(new astrotweaks.item.SpatialAnchor.SpatialAnchorEvents());






	}




	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
		astrotweaks.ModVariables.init();

		astrotweaks.creativetab.ATCreativeTabs.init();

		astrotweaks.oredict.UOredictRegistrar.init();
		astrotweaks.oredict.OreDictQuantsT.init();
		astrotweaks.recipe.RecipeSmeltingAll.init();

		
		MinecraftForge.EVENT_BUS.register(new EventBreakBlock());



		if (ModVariables.Enable_Bushes) {
			BushDecorator.init();
			MinecraftForge.TERRAIN_GEN_BUS.register(new BushDecorator());
		}

		astrotweaks.recipe.GavelRecipeRegistry.initDefaults();


		if (ModVariables.Enable_RealisticBreak) {
			astrotweaks.gameplay.RealisticBreak.postInit();
			MinecraftForge.EVENT_BUS.register(new RealisticBreak());
		}





	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);

		astrotweaks.ModVariables.postInit();







	}



	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		astrotweaks.command.ATCommands.init(event);
		proxy.serverLoad(event);
	}

	@Mod.EventHandler
	public void serverStop(FMLServerStoppingEvent event) {
		net.minecraft.server.MinecraftServer server = net.minecraftforge.fml.common.FMLCommonHandler.instance().getMinecraftServerInstance();
		if (server != null) {
			astrotweaks.Multiverse.LevelManager.getInstance().unloadAndUnregisterAll(server);
		}
	}

	
	@SubscribeEvent
	public void onRegisterRecipes(RegistryEvent.Register<IRecipe> event) {
	    RecipeHandler.loadRecipes();
	    IForgeRegistry<IRecipe> reg = event.getRegistry();
	    int idx = 1;
	    for (IRecipe r : RecipeHandler.RECIPES_TO_REGISTER) {
	        if (r.getRegistryName() == null) {
	            r.setRegistryName(new ResourceLocation("astrotweaks", "cr_" + idx));
	        }
	        reg.register(r);
	        idx++;
	    }
		RecipeHandler.RECIPES_TO_REGISTER.clear(); // удаляем мусор из памяти
	}

	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		// ЕДИНСТВЕННАЯ точка регистрации блоков. Порядок строго фиксирован,
		// чтобы registryID не зависели от порядка загрузки классов (одинаковый ID у всех игроков/перезаходов):
		// 1) основные блоки, 2) технологии, 3) mined-блоки, 4) блок портала мультиверса
		astrotweaks.block.ATBlocks.registerBlocks(event);
		astrotweaks.tech.ATTechnologies.registerBlocks(event);
		astrotweaks.block.MinedBlocks.registerBlocks(event);
		astrotweaks.Multiverse.NetherPortalReg.registerBlocks(event);
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		// ЕДИНСТВЕННАЯ точка регистрации предметов. Порядок строго фиксирован:
		// 1) предметы, 2) процессоры, 3) ItemBlock'и основных блоков, 4) ItemBlock'и технологий, 5) mined-предметы
		astrotweaks.item.ATItems.registerItems(event);
		astrotweaks.item.ItemProcessors.registerItems(event);
		astrotweaks.block.ATBlocks.registerItems(event);
		astrotweaks.tech.ATTechnologies.registerItems(event);
		astrotweaks.block.MinedBlocks.registerItems(event);
	}


	@Mod.EventHandler
	public void onLoadComplete(FMLLoadCompleteEvent event) {
		if (ModVariables.Remove_METS_engineer)
			astrotweaks.tweaks.RemVillagerTrades.onLoadComplete();


		ClearRegArrays();
	}







	@SubscribeEvent
	public void onPlayerLoggedIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
		if (!event.player.world.isRemote) {
			WorldSavedData mapdata = ModVariables.MapVariables.get(event.player.world);
			WorldSavedData worlddata = ModVariables.WorldVariables.get(event.player.world);
			if (mapdata != null)
				AstrotweaksMod.PACKET_HANDLER.sendTo(new ModVariables.WorldSavedDataSyncMessage(0, mapdata), (EntityPlayerMP) event.player);
			if (worlddata != null)
				AstrotweaksMod.PACKET_HANDLER.sendTo(new ModVariables.WorldSavedDataSyncMessage(1, worlddata), (EntityPlayerMP) event.player);
		}
	}

	@SubscribeEvent
	public void registerBiomes(RegistryEvent.Register<Biome> event) {
		if (ModVariables.Enable_Depths_Dimension) event.getRegistry().register(astrotweaks.world.biome.BiomeCavern.CAVERN);


	}

	//@SubscribeEvent
	//public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		//event.getRegistry().registerAll(elements.getEntities().stream().map(Supplier::get).toArray(EntityEntry[]::new));
	//}

	//@SubscribeEvent
	//public void registerPotions(RegistryEvent.Register<Potion> event) {
		//event.getRegistry().registerAll(elements.getPotions().stream().map(Supplier::get).toArray(Potion[]::new));
	//}

	//@SubscribeEvent
	//public void registerSounds(RegistryEvent.Register<net.minecraft.util.SoundEvent> event) {
	//	elements.registerSounds(event);
	//}

	//@SubscribeEvent
	//@SideOnly(Side.CLIENT)
	//public void registerModels(ModelRegistryEvent event) {
		//elements.getElements().forEach(element -> element.registerModels(event));
	//}
	//static {
	//	FluidRegistry.enableUniversalBucket();
	//}


	// GC не очищает неипользуемые переменные классов, поэтому чистим их вручную т.к. они больше не нужны после регистрации
	public static void ClearRegArrays() {
		// Список рецептов к регистрации
		

		astrotweaks.block.ATBlocks.ClearRegList();
		astrotweaks.item.ATItems.ClearRegList();


	}


}
