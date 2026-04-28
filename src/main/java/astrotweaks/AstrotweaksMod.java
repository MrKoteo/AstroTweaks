
package astrotweaks;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraft.util.ResourceLocation;
import java.util.UUID;

import net.minecraft.world.biome.Biome;
import net.minecraft.potion.Potion;
import net.minecraft.item.Item;
import net.minecraft.block.Block;

import astrotweaks.world.SnowVillage;
import astrotweaks.world.ForestVillage;
import astrotweaks.world.DepthsDim;
import astrotweaks.util.LoadConfig;
import astrotweaks.world.CavernMobModifier;
import astrotweaks.util.CombinedFuelHandler;
import astrotweaks.util.Handler;


import astrotweaks.world.BushDecorator;
import astrotweaks.world.RealisticBreak;

import astrotweaks.recipe.RecipeHandler;
import astrotweaks.oredict.UOredictRegistrar;

import astrotweaks.ModVariables;

import java.util.function.Supplier;

@Mod(modid = AstrotweaksMod.MODID, version = AstrotweaksMod.VERSION)
public class AstrotweaksMod {
	public static final String MODID = "astrotweaks";
	public static final String VERSION = "b-5.1";
	public static final SimpleNetworkWrapper PACKET_HANDLER = NetworkRegistry.INSTANCE.newSimpleChannel("astrotweaks:a");
	@SidedProxy(clientSide = "astrotweaks.ClientProxyAstrotweaksMod", serverSide = "astrotweaks.ServerProxyAstrotweaksMod")
	public static IProxyAstrotweaksMod proxy;
	@Mod.Instance(MODID)
	public static AstrotweaksMod instance;
	public ElementsAstrotweaksMod elements = new ElementsAstrotweaksMod();
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);

		modVariables = new ModVariables();
        modVariables.preInit(event);
        
		GameRegistry.registerWorldGenerator(elements, 5);
		GameRegistry.registerFuelHandler(elements);

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new Handler.GuiHandler());
		elements.preInit(event);
		MinecraftForge.EVENT_BUS.register(elements);
		elements.getElements().forEach(element -> element.preInit(event));
		proxy.preInit(event);

		if (AstrotweaksModVariables.Enable_Depths_Dimension) {
			depthsDim = new DepthsDim();
			depthsDim.preInit(event);
		}


        loadConfig = new LoadConfig();
        loadConfig.preInit(event);



		if (ModVariables.Enable_SnowVillages) {
	        snowVillage = new SnowVillage();
	        snowVillage.preInit(event);
		}
		if (ModVariables.Enable_ForestVillages) {
			forestVillage = new ForestVillage();
	        forestVillage.preInit(event);
		}




	}

	private SnowVillage snowVillage;
	private ForestVillage forestVillage;
	private DepthsDim depthsDim;
	private LoadConfig loadConfig;
	private ModVariables modVariables;
	private CombinedFuelHandler cfh;
	private RealisticBreak realBreak;


	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		elements.getElements().forEach(element -> element.init(event));
		proxy.init(event);
		UOredictRegistrar.init(event);
	

		if (AstrotweaksModVariables.Enable_Depths_Dimension) MinecraftForge.EVENT_BUS.register(new CavernMobModifier());

		if (ModVariables.Enable_Bushes) {
			BushDecorator.init();
			MinecraftForge.TERRAIN_GEN_BUS.register(new BushDecorator());
		}

	    

	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);

		if (ModVariables.Extra_Fuels) {
			cfh = new CombinedFuelHandler();
			cfh.postInit(event);
		}
		if (ModVariables.Enable_RealisticBreak) {
			realBreak = new RealisticBreak();
	        realBreak.postInit(event);
		}


		
	}

	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		elements.getElements().forEach(element -> element.serverLoad(event));
		proxy.serverLoad(event);
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
	    RecipeHandler.RECIPES_TO_REGISTER.clear();
	}



	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(elements.getBlocks().stream().map(Supplier::get).toArray(Block[]::new));
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(elements.getItems().stream().map(Supplier::get).toArray(Item[]::new));
	}

	@SubscribeEvent
	public void registerBiomes(RegistryEvent.Register<Biome> event) {
		event.getRegistry().registerAll(elements.getBiomes().stream().map(Supplier::get).toArray(Biome[]::new));
	}

	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		event.getRegistry().registerAll(elements.getEntities().stream().map(Supplier::get).toArray(EntityEntry[]::new));
	}

	@SubscribeEvent
	public void registerPotions(RegistryEvent.Register<Potion> event) {
		event.getRegistry().registerAll(elements.getPotions().stream().map(Supplier::get).toArray(Potion[]::new));
	}

	//@SubscribeEvent
	//public void registerSounds(RegistryEvent.Register<net.minecraft.util.SoundEvent> event) {
	//	elements.registerSounds(event);
	//}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		elements.getElements().forEach(element -> element.registerModels(event));
	}
	//static {
	//	FluidRegistry.enableUniversalBucket();
	//}
}
