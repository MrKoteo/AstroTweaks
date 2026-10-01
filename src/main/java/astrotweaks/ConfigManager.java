package astrotweaks;


import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import java.io.File;



public class ConfigManager {
	private static final org.apache.logging.log4j.Logger LOGGER = FMLLog.getLogger();
	private static Configuration config;
	public ConfigManager() {}


    // Вызвать один раз на этапе construction
    public static void loadConfig() {
        File configFile = new File("config/" + AstrotweaksMod.MODID + ".cfg");
        config = new Configuration(configFile);
        try {
            config.load();
            loadValues();  // читаем все переменные
        } catch (Exception e) {
            LOGGER.error("Failed to load config", e);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

	public static void loadValues() {

		//Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		try {
			config.load();

			//# Категории

			// Astro_Tech 		- то что неотъемлемо связано с модпаком
			// general 			- глобальные функции
			// Game mechanics 	- игровые механики
			// Worldgen 		- генерация мира
			// World			- В целом то, что расширяет мир
			// mods 			- изменение других модов
			// tweaks			- различные твики и QoLF
			// Natures Power	- То что меняет мир с течением времени, like рост травы, мха, итд.
			// Black_Hole		- настройки чёрной дыры
			//
			// misc 			- Всё что не вошло в обычные категории
			//
			//
			//



			//// # Astro_Tech
			ModVariables.AstroTech_Environment = safeGetBoolean(config, "AstroTech_Environment", "Astro_Tech", false, "AstroTech Environment (y/n)");
			ModVariables.EnableProgressionSystem = safeGetBoolean(config, "EnableProgressionSystem", "Astro_Tech", false, "Enable Progression System (y/n)");
			



			//// # general
			ModVariables.doRegisterMinedBlocks = safeGetBoolean(config, "doRegisterMinedBlocks", "general", ModVariables.doRegisterMinedBlocks, "Register mined(trapped) blocks? (y/n)");
			ModVariables.Extra_Fuels = safeGetBoolean(config, "Register_Extra_Fuels", "general", ModVariables.Extra_Fuels, "Should to register more fuels for furnace? (y/n)");
			ModVariables.CUSTOM_GAME_TITLE = safeGetString(config, "CUSTOM_GAME_TITLE", "general", ModVariables.CUSTOM_GAME_TITLE, "Set the Custom Game Title ");




			//// # Game mechanics
			ModVariables.Enable_RealisticBreak = safeGetBoolean(config, "Enable_RealisticBreak", "Game mechanics", ModVariables.Enable_RealisticBreak, "More realistic conditions for destruction of blocks");
			ModVariables.Enable_StepUp = safeGetBoolean(config, "StepUp", "Game mechanics", ModVariables.Enable_StepUp, "1-block high step, without auto-jump");
			ModVariables.Enable_Dirt2Path = safeGetBoolean(config, "Dirt2Path", "Game mechanics", ModVariables.Enable_Dirt2Path, "Allow convert dirt/podzol/mycelium to GrassPath");
			ModVariables.Food_Negative_Effects = safeGetBoolean(config, "Food_Negative_Effects", "Game mechanics", ModVariables.Food_Negative_Effects, "Will poisonous food have more violent effects? (y/n)");
			ModVariables.Raw_Meat_Negative_Effects = safeGetBoolean(config, "Raw_Meat_Negative_Effects", "Game mechanics", ModVariables.Raw_Meat_Negative_Effects, "Will raw meat be less edible? (y/n)");



			//// # Worldgen
			ModVariables.Enable_SnowVillages = safeGetBoolean(config, "Enable_SnowVillages", "Worldgen", ModVariables.Enable_SnowVillages, "Enable Snow Villages generation (y/n)");
			ModVariables.Enable_ForestVillages = safeGetBoolean(config, "Enable_ForestVillages", "Worldgen", ModVariables.Enable_ForestVillages, "Enable Forest Villages generation (y/n)");
			//ModVariables.Enable_BirchVillages = safeGetBoolean(config, "Enable_BirchVillages", "Worldgen", true, "Enable Birch Forest Villages generation (y/n)");
			ModVariables.OW_Quartz_Gen = safeGetBoolean(config, "Overworld_Quartz_Generation", "Worldgen", ModVariables.OW_Quartz_Gen, "Enable Overworld Quartz Generation (y/n)");
			ModVariables.OW_Ruby_Gen = safeGetBoolean(config, "Ruby_Generation", "Worldgen", ModVariables.OW_Ruby_Gen, "Enable Ruby Generation (y/n)");
			ModVariables.OW_Minerals_Gen = safeGetBoolean(config, "Overworld_Minerals_Generation", "Worldgen", ModVariables.OW_Minerals_Gen, "Enable Overworld Minerals Generation (y/n)");
			ModVariables.Enable_Bushes = safeGetBoolean(config, "Enable_Bushes", "Worldgen", ModVariables.Enable_Bushes, "Enable bush generation in Overworld (y/n)");

			ModVariables.bush1gen = safeGetDouble(config, "bush1_genAttempts", "Worldgen", ModVariables.bush1gen /*def*/, 0.0 /*min*/, 99.0 /*max*/, "Number of attempts to generate an element [default: " + ModVariables.bush1gen + "]");
			ModVariables.bush2gen = safeGetDouble(config, "bush2_genAttempts", "Worldgen", ModVariables.bush2gen /*def*/, 0.0 /*min*/, 99.0 /*max*/, "Number of attempts to generate an element [default: " + ModVariables.bush2gen + "]");
			ModVariables.bush3gen = safeGetDouble(config, "bush3_genAttempts", "Worldgen", ModVariables.bush3gen /*def*/, 0.0 /*min*/, 99.0 /*max*/, "Number of attempts to generate an element [default: " + ModVariables.bush3gen + "]");
			ModVariables.bush4gen = safeGetDouble(config, "bush4_genAttempts", "Worldgen", ModVariables.bush4gen /*def*/, 0.0 /*min*/, 99.0 /*max*/, "Number of attempts to generate an element [default: " + ModVariables.bush4gen + "]");
			ModVariables.bush5gen = safeGetDouble(config, "bush5_genAttempts", "Worldgen", ModVariables.bush5gen /*def*/, 0.0 /*min*/, 99.0 /*max*/, "Number of attempts to generate an element [default: " + ModVariables.bush5gen + "]");
			ModVariables.bush6gen = safeGetDouble(config, "bush6_genAttempts", "Worldgen", ModVariables.bush6gen /*def*/, 0.0 /*min*/, 99.0 /*max*/, "Number of attempts to generate an element [default: " + ModVariables.bush6gen + "]");
			ModVariables.bush7gen = safeGetDouble(config, "bush7_genAttempts", "Worldgen", ModVariables.bush7gen /*def*/, 0.0 /*min*/, 99.0 /*max*/, "Number of attempts to generate an element [default: " + ModVariables.bush7gen + "]");
			ModVariables.fern1gen = safeGetDouble(config, "fern1_genAttempts", "Worldgen", ModVariables.fern1gen /*def*/, 0.0 /*min*/, 99.0 /*max*/, "Number of attempts to generate an element [default: " + ModVariables.fern1gen + "]");


			ModVariables.Enable_Ground_Elements = safeGetBoolean(config, "Enable_Ground_Elements", "Worldgen", ModVariables.Enable_Ground_Elements, "Enable Ground elements generation (y/n)");
			// Double values with validation of range and parsing
			ModVariables.Stick_Gen_Attempts = safeGetDouble(config, "Stick_Gen_Attempts", "Worldgen", ModVariables.Stick_Gen_Attempts /*def*/, 0.0 /*min*/, 999.0 /*max*/, "Number of attempts to generate an element (double num) [default: 2.0]");
			String[] StickBiomes = config.get("Worldgen", "Stick_Gen_Biomes", STICK_BIOME_LIST, 
				"List of biome IDs (e.g., minecraft:plains) where sticks generate. " + "Empty list uses default biomes.").getStringList();
			ModVariables.Stick_Gen_Min_Y = safeGetInt(config, "Stick_Gen_Min_Y", "Worldgen", ModVariables.Stick_Gen_Min_Y, 1, 255, "Minimum Y level for stick generation (1-255)");
			ModVariables.Stick_Gen_Max_Y = safeGetInt(config, "Stick_Gen_Max_Y", "Worldgen", ModVariables.Stick_Gen_Max_Y, 1, 255, "Maximum Y level for stick generation (1-255)");
			ModVariables.Rock_Gen_Attempts = safeGetDouble(config, "Rock_Gen_Attempts", "Worldgen", ModVariables.Rock_Gen_Attempts /*def*/, 0.0 /*min*/, 999.0 /*max*/, "Number of attempts to generate an element (double num) [default: 0.3]");
			String[] RockBiomes = config.get("Worldgen", "Rock_Gen_Biomes", ROCK_BIOME_LIST, 
				"List of biome IDs (e.g., minecraft:plains) where rocks generate. " + "Empty list uses default biomes.").getStringList();
			ModVariables.Rock_Gen_Min_Y = safeGetInt(config, "Rock_Gen_Min_Y", "Worldgen", ModVariables.Rock_Gen_Min_Y, 1, 255, "Minimum Y level for rock generation (1-255)");
			ModVariables.Rock_Gen_Max_Y = safeGetInt(config, "Rock_Gen_Max_Y", "Worldgen", ModVariables.Rock_Gen_Max_Y, 1, 255, "Maximum Y level for rock generation (1-255)");
			

			//// # World
			ModVariables.Enable_Depths_Dimension = safeGetBoolean(config, "Enable_Depths_Dimension", "World", ModVariables.Enable_Depths_Dimension, "Should register Depths dimension? (y/n)");
			ModVariables.Enable_Depths_Dim_Bedrock_TP = safeGetBoolean(config, "Enable_Depths_Dim_Bedrock_TP", "World", ModVariables.Enable_Depths_Dim_Bedrock_TP, "Allow access to the Depths via Bedrock? (y/n)");
			ModVariables.MULTIVERSE = safeGetBoolean(config, "MULTIVERSE", "World", ModVariables.MULTIVERSE, "Enable the Multiverse system (y/n)");
			ModVariables.MULTIVERSE_MAX_UNIVERSES = safeGetInt(config, "MULTIVERSE_MAX_UNIVERSES", "World", ModVariables.MULTIVERSE_MAX_UNIVERSES, 1, 1000, "Maximum number of universes. Last slot is the recycle slot (1-1000)");
			ModVariables.Enable_uVOID = safeGetBoolean(config, "Enable_uVOID", "World", ModVariables.Enable_uVOID, "Enable Cross-world University with DimID == -1000000 (y/n)");

			
			//// # Tweaks
			ModVariables.NoRedFlash = safeGetBoolean(config, "No_Red_Flash", "Tweaks", ModVariables.NoRedFlash, "Remove entities red flash when taking damage (y/n)");
			ModVariables.No_Potion_Icons = safeGetBoolean(config, "No_Potion_Icons", "Tweaks", ModVariables.No_Potion_Icons, "Disable Potion Icons in the top right of screen (y/n)");
			ModVariables.ServerPingFix = safeGetBoolean(config, "ServerPingFix", "Tweaks", ModVariables.ServerPingFix, "Like in mod \"FIX MY PINGGGGGG\" (y/n)");
			ModVariables.Better_Smelting = safeGetBoolean(config, "Better_Smelting", "Tweaks", ModVariables.Better_Smelting, "Enable more smelting recipes? (y/n)");
			ModVariables.ExplosionDamageMult = safeGetDouble(config, "Explosion_Damage_Multiplier", "Tweaks", ModVariables.ExplosionDamageMult, 0.0, 100.0, "Explosive damage modifier. (DAMAGE * Multiplier)");



			//// # Nature's Power
			ModVariables.GG_ENABLED = safeGetBoolean(config, "GG_ENABLED", "Natures Power", ModVariables.GG_ENABLED, "Enable Grass growth? (y/n)");
			ModVariables.GG_MIN_DELAY_TICK = safeGetInt(config, "Grass_Growth_MIN_Delay", "Natures Power", ModVariables.GG_MIN_DELAY_TICK, 1, 1728000, "Minimum delay (ticks) for grass regrowth (10-1728000)");
			ModVariables.GG_MAX_DELAY_TICK = safeGetInt(config, "Grass_Growth_MAX_Delay", "Natures Power", ModVariables.GG_MAX_DELAY_TICK, 2, 1728000, "Maximum delay (ticks) for grass regrowth (10-1728000)");
			ModVariables.GG_MAX_OPER_PER_TICK = safeGetInt(config, "GG_MAX_PER_TICK", "Natures Power", ModVariables.GG_MAX_OPER_PER_TICK, 1, 2048, "Maximum operations per tick (1-2048)");
			ModVariables.GG_Density = safeGetInt(config, "Grass_Density", "Natures Power", ModVariables.GG_Density, 1, 25, "Maximum grass density (1-25)");
			ModVariables.GG_Tall_Density = safeGetInt(config, "Tall_Grass_Density", "Natures Power", ModVariables.GG_Tall_Density, 1, 25, "Maximum tall grass (double_plant:2) density (1-25)");
			ModVariables.GG_Giant_Density = safeGetInt(config, "Giant_Graass_Density", "Natures Power", ModVariables.GG_Giant_Density, 1, 25, "Maximum giant grass density (1-25)");

			ModVariables.BM_ENABLED = safeGetBoolean(config, "BM_ENABLED", "Natures Power", ModVariables.BM_ENABLED, "Enable Blocks mossing? (y/n)");
			ModVariables.BM_MIN_DELAY_TICK = safeGetInt(config, "Block_Mossing_MIN_Delay", "Natures Power", ModVariables.BM_MIN_DELAY_TICK, 1, 1728000, "Minimum delay (ticks) for blocks mossing (10-1728000)");
			ModVariables.BM_MAX_DELAY_TICK = safeGetInt(config, "Block_Mossing_MAX_Delay", "Natures Power", ModVariables.BM_MAX_DELAY_TICK, 2, 1728000, "Maximum delay (ticks) for blocks mossing (10-1728000)");
			ModVariables.BM_MAX_OPER_PER_TICK = safeGetInt(config, "BM_MAX_PER_TICK", "Natures Power", ModVariables.BM_MAX_OPER_PER_TICK, 1, 2048, "Maximum operations per tick (1-2048)");








			//// # Technologies
			ModVariables.Money_Can_Smelt = safeGetBoolean(config, "Money_Can_Smelt", "tech", ModVariables.Money_Can_Smelt, "Can coins be melted down (y/n)");
			ModVariables.Money_Can_Craft = safeGetBoolean(config, "Money_Can_Craft", "tech", ModVariables.Money_Can_Craft, "Can copper coins be crafted at the MoneyTable from a copper plate (y/n)");
			ModVariables.Money_Can_Conversion = safeGetBoolean(config, "Money_Can_Conversion", "tech", ModVariables.Money_Can_Conversion, "Can coins be converted in Money Table? (y/n)");
			ModVariables.Money_ConvCount = safeGetInt(config, "Money_ConvCount", "tech", ModVariables.Money_ConvCount, 1, 50, "Maximum number of coins that can be processed at one conv (1-50)");
			ModVariables.QM_is_fully_unbreakable = safeGetBoolean(config, "QM_is_fully_unbreakable", "tech", ModVariables.QM_is_fully_unbreakable, "Prohibit the player from breaking the QM_block");
			ModVariables.QTS_Max_Range = safeGetInt(config, "QTS_Max_Range", "tech", ModVariables.QTS_Max_Range, 1, 16384, "Maximum range of Quantum TP Supressor (1-16384)");
			ModVariables.Enable_TDARK = safeGetBoolean(config, "Enable_TDARK", "tech", ModVariables.Enable_TDARK, "Enable the TDARK + CT tech (requires MULTIVERSE) (y/n)");
			ModVariables.SD_Max_Range = safeGetInt(config, "SD_Max_Range", "tech", ModVariables.SD_Max_Range, 1, 1024, "Maximum range of Spatial Dome (1-1024)");



			//// # Black_Hole
			ModVariables.BH_BUDGET_PER_TICK = safeGetInt(config, "BH_BUDGET_PER_TICK", "Black_Hole", ModVariables.BH_BUDGET_PER_TICK, 1, 65536, "Maximum number of blocks that can be processed per tick (1-65536)");
			ModVariables.BH_RESCAN_DELAY_TICKS = safeGetInt(config, "BH_RESCAN_DELAY_TICKS", "Black_Hole", ModVariables.BH_RESCAN_DELAY_TICKS, 20, 1728000, "Delay before rescanning the BH area (20-1728000)");
			
			ModVariables.BH_MIN_ACCEL = safeGetDouble(config, "BH_MIN_ACCEL", "Black_Hole", ModVariables.BH_MIN_ACCEL, 0.0001, 10.0, "Minimum gravitational acceleration per tick (0.0001-10.0)");
			ModVariables.BH_MAX_ACCEL = safeGetDouble(config, "BH_MAX_ACCEL", "Black_Hole", ModVariables.BH_MAX_ACCEL, 0.0002, 100.0, "Maximum gravitational acceleration per tick (0.0002-100.0)");
			ModVariables.BH_MAX_SPEED = safeGetDouble(config, "BH_MAX_SPEED", "Black_Hole", ModVariables.BH_MAX_SPEED, 0.5, 64.0, "The maximum entity speed (blocks) per tick. (0.5-64.0)");
			ModVariables.BH_SUFFOCATION_ACCEL = safeGetDouble(config, "BH_SUFFOCATION_ACCEL", "Black_Hole", ModVariables.BH_SUFFOCATION_ACCEL, 0.0, 100.0, "Gravitational acceleration threshold (b/t) for suffocation damage (0.0-100.0)");
			ModVariables.BH_MAX_GRAVITY_RANGE = safeGetDouble(config, "BH_MAX_GRAVITY_RANGE", "Black_Hole", ModVariables.BH_MAX_GRAVITY_RANGE, 0.0, 16384.0, "Maximum range of the gravitational pull in blocks (1.0-16384.0)");
			ModVariables.BH_MAX_BLOCK_CAPTURE_RANGE = safeGetDouble(config, "BH_MAX_BLOCK_CAPTURE_RANGE", "Black_Hole", ModVariables.BH_MAX_BLOCK_CAPTURE_RANGE, 0.0, 16384.0, "Maximum range at which blocks can be captured in blocks (1.0-16384.0)");
			ModVariables.BH_MAX_MASS = safeGetDouble(config, "BH_MAX_MASS", "Black_Hole", ModVariables.BH_MAX_MASS, 1.0E3D, 1.0E18D, "Maximum mass the black hole can accumulate (1.0E3-1.0E18)");

			ModVariables.BH_MASS_PER_ITEM = safeGetDouble(config, "BH_MASS_PER_ITEM", "Black_Hole", ModVariables.BH_MASS_PER_ITEM, 0.0, 1.0E6D, "Mass added per consumed item (0.0-1.0E6)");
			ModVariables.BH_MASS_PER_ENTITY = safeGetDouble(config, "BH_MASS_PER_ENTITY", "Black_Hole", ModVariables.BH_MASS_PER_ENTITY, 0.0, 1.0E6D, "Mass added per consumed entity (0.0-1.0E6)");
			ModVariables.BH_MASS_PER_XP = safeGetDouble(config, "BH_MASS_PER_XP", "Black_Hole", ModVariables.BH_MASS_PER_XP, 0.0, 1.0E6D, "Mass added per consumed XP orb (0.0-1.0E6)");
			ModVariables.BH_MASS_PER_PLAYER = safeGetDouble(config, "BH_MASS_PER_PLAYER", "Black_Hole", ModVariables.BH_MASS_PER_PLAYER, 0.0, 1.0E6D, "Mass added per consumed player (0.0-1.0E6)");
			ModVariables.BH_MASS_PER_LIQUID = safeGetDouble(config, "BH_MASS_PER_LIQUID", "Black_Hole", ModVariables.BH_MASS_PER_LIQUID, 0.0, 1.0E6D, "Mass added per consumed liquid block (0.0-1.0E6)");







			//// # Mods
			ModVariables.Remove_METS_engineer = safeGetBoolean(config, "Remove_METS_engineer", "mods", ModVariables.Remove_METS_engineer, "Remove trades of Engineer villager (MoreElectricTools)");
			ModVariables.Rem_Gravestone_Note = safeGetBoolean(config, "Remove_Gravestone_Note", "mods", ModVariables.Rem_Gravestone_Note, "Removes a Gravestone paper when dropped by a player");





			//// # misc
			ModVariables.Extra_Drops_Grass = safeGetBoolean(config, "Extra_Drops_Grass", "misc", ModVariables.Extra_Drops_Grass, "Should process additional drops from grass? (y/n)");
			ModVariables.Extra_Drops_All = safeGetBoolean(config, "Extra_Drops_All", "misc", ModVariables.Extra_Drops_All, "Should extra drops be processed in general? (y/n)");








































			Set<ResourceLocation> sgb = new HashSet<>();
			Set<Biome> sgbObj = new HashSet<>();
			
			for (String s : StickBiomes) {
			    ResourceLocation rl = new ResourceLocation(s);
			    sgb.add(rl);
			    Biome b = Biome.REGISTRY.getObject(rl);
			    if (b != null) {
			        sgbObj.add(b);
			    }
			}
			
			ModVariables.Stick_Gen_Biomes = Collections.unmodifiableSet(sgb);
			ModVariables.Stick_Gen_Biomes_Cached = Collections.unmodifiableSet(sgbObj);
			///
			Set<ResourceLocation> rgb = new HashSet<>();
			Set<Biome> rgbObj = new HashSet<>();
			
			for (String s : RockBiomes) {
			    ResourceLocation rl = new ResourceLocation(s);
			    rgb.add(rl);
			    Biome b = Biome.REGISTRY.getObject(rl);
			    if (b != null) {
			        rgbObj.add(b);
			    }
			}
			ModVariables.Rock_Gen_Biomes = Collections.unmodifiableSet(rgb);
			ModVariables.Rock_Gen_Biomes_Cached = Collections.unmodifiableSet(rgbObj);



		} finally {
			if (config.hasChanged()) {
				config.save();
			}
		}
	}
	
	private static final String[] STICK_BIOME_LIST = new String[]{
	    "minecraft:forest", "minecraft:taiga", "minecraft:swampland",
	    "minecraft:forest_hills", "minecraft:taiga_hills", "minecraft:smaller_extreme_hills", "minecraft:jungle", "minecraft:jungle_hills", 
	    "minecraft:jungle_edge", "minecraft:birch_forest", "minecraft:birch_forest_hills", "minecraft:roofed_forest",
	    "minecraft:redwood_taiga", "minecraft:redwood_taiga_hills", "minecraft:extreme_hills_with_trees", "minecraft:savanna", "minecraft:savanna_rock",
		"minecraft:mutated_forest","minecraft:mutated_taiga","minecraft:mutated_swampland","minecraft:mutated_jungle",
		"minecraft:mutated_jungle_edge","minecraft:mutated_birch_forest","minecraft:mutated_birch_forest_hills","minecraft:mutated_roofed_forest","minecraft:mutated_redwood_taiga",
		"minecraft:mutated_redwood_taiga_hills","minecraft:mutated_extreme_hills_with_trees","minecraft:mutated_savanna","minecraft:mutated_savanna_rock"
	};
	private static final String[] ROCK_BIOME_LIST = new String[]{
	    "minecraft:plains", "minecraft:extreme_hills", "minecraft:forest", "minecraft:taiga", "minecraft:river", "minecraft:beaches", 
	    "minecraft:forest_hills", "minecraft:taiga_hills", "minecraft:smaller_extreme_hills", "minecraft:jungle", "minecraft:jungle_hills", 
	    "minecraft:jungle_edge", "minecraft:stone_beach", "minecraft:birch_forest", "minecraft:birch_forest_hills", "minecraft:roofed_forest",
	    "minecraft:redwood_taiga", "minecraft:redwood_taiga_hills", "minecraft:extreme_hills_with_trees", "minecraft:savanna", "minecraft:savanna_rock",
		"minecraft:mutated_plains","minecraft:mutated_extreme_hills","minecraft:mutated_forest","minecraft:mutated_taiga","minecraft:mutated_swampland","minecraft:mutated_jungle",
		"minecraft:mutated_jungle_edge","minecraft:mutated_birch_forest","minecraft:mutated_birch_forest_hills","minecraft:mutated_roofed_forest","minecraft:mutated_redwood_taiga",
		"minecraft:mutated_redwood_taiga_hills","minecraft:mutated_extreme_hills_with_trees","minecraft:mutated_savanna","minecraft:mutated_savanna_rock"
	};

/*
		"minecraft:mutated_plains","minecraft:mutated_extreme_hills","minecraft:mutated_forest","minecraft:mutated_taiga","minecraft:mutated_swampland","minecraft:mutated_jungle",
		"minecraft:mutated_jungle_edge","minecraft:mutated_birch_forest","minecraft:mutated_birch_forest_hills","minecraft:mutated_roofed_forest","minecraft:mutated_redwood_taiga",
		"minecraft:mutated_redwood_taiga_hills","minecraft:mutated_extreme_hills_with_trees","minecraft:mutated_savanna","minecraft:mutated_savanna_rock"
*/


	// --- helper methods ---

	private static boolean safeGetBoolean(Configuration config, String name, String category, boolean def, String comment) {
		try {
			// read as string first to detect malformed booleans like "yes" or "tru"
			String raw = config.get(category, name, Boolean.toString(def), comment).getString();
			// allow "true"/"false" (case-insensitive), also support "1"/"0" as convenience
			if (raw.equalsIgnoreCase("true") || raw.equals("1")) {
				return true;
			} else if (raw.equalsIgnoreCase("false") || raw.equals("0")) {
				return false;
			} else {
				LOGGER.warn("Config '{}' in category '{}' has invalid boolean value '{}'. Using default: {}", name, category, raw, def);
				// overwrite invalid value with default so it is saved back
				config.get(category, name, def).set(def);
				return def;
			}
		} catch (Exception e) {
			LOGGER.error("Error reading boolean config '{}.{}': {}", category, name, e.getMessage());
			config.get(category, name, def).set(def);
			return def;
		}
	}

	private static double safeGetDouble(Configuration config, String name, String category, double def, double min, double max, String comment) {
		try {
			String raw = config.get(category, name, Double.toString(def), comment).getString();
			double val;
			try {
				val = Double.parseDouble(raw);
			} catch (NumberFormatException nfe) {
				LOGGER.warn("Config '{}' in category '{}' has invalid double value '{}'. Using default: {}", name, category, raw, def);
				config.get(category, name, def).set(def);
				return def;
			}
			if (val < min || val > max) {
				LOGGER.warn("Config '{}' in category '{}' out of range ({}-{}): {}. Using default: {}", name, category, min, max, val, def);
				config.get(category, name, def).set(def);
				return def;
			}
			return val;
		} catch (Exception e) {
			LOGGER.error("Error reading double config '{}.{}': {}", category, name, e.getMessage());
			config.get(category, name, def).set(def);
			return def;
		}
	}

	private static int safeGetInt(Configuration config, String name, String category, int def, int min, int max, String comment) {
	    try {
	        String raw = config.get(category, name, Integer.toString(def), comment).getString();
	        int val;
	        try {
	            val = Integer.parseInt(raw);
	        } catch (NumberFormatException nfe) {
	            LOGGER.warn("Config '{}' in category '{}' has invalid integer value '{}'. Using default: {}", name, category, raw, def);
	            config.get(category, name, def).set(def);
	            return def;
	        }
	        if (val < min || val > max) {
	            LOGGER.warn("Config '{}' in category '{}' out of range ({}-{}): {}. Using default: {}", name, category, min, max, val, def);
	            config.get(category, name, def).set(def);
	            return def;
	        }
	        return val;
	    } catch (Exception e) {
	        LOGGER.error("Error reading integer config '{}.{}': {}", category, name, e.getMessage());
	        config.get(category, name, def).set(def);
	        return def;
	    }
	}

	private static String safeGetString(Configuration config, String name, String category, String def, String comment) {
	    try {
	        String raw = config.get(category, name, def == null ? "" : def, comment).getString();
	        return raw == null ? def : raw;
	    } catch (Exception e) {
	        LOGGER.error("Error reading string config '{}.{}': {}", category, name, e.getMessage());
	        config.get(category, name, def).set(def);
	        return def;
	    }
	}
}
