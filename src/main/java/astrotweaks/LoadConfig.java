package astrotweaks.util;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import java.io.File;

import astrotweaks.AstrotweaksModVariables;
import astrotweaks.ModVariables;
//import astrotweaks.ElementsAstrotweaksMod;



public class LoadConfig {
	private static final org.apache.logging.log4j.Logger LOGGER = FMLLog.getLogger();
	public LoadConfig() {}


	//@Override
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);

		//System.out.println("abobba 1");

		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		try {
			config.load();

			// Boolean with safe fallback
			AstrotweaksModVariables.AstroTech_Environment = safeGetBoolean(config, "AstroTech_Environment", "Astro_Tech", false, "AstroTech Environment (y/n)");
			AstrotweaksModVariables.EnableProgressionSystem = safeGetBoolean(config, "EnableProgressionSystem", "Astro_Tech", false, "Enable Progression System (y/n)");

			ModVariables.Enable_SnowVillages = safeGetBoolean(config, "Enable_SnowVillages", "generation", true, "Enable Snow Villages generation (y/n)");
			ModVariables.Enable_ForestVillages = safeGetBoolean(config, "Enable_ForestVillages", "generation", true, "Enable Forest Villages generation (y/n)");
			//ModVariables.Enable_BirchVillages = safeGetBoolean(config, "Enable_BirchVillages", "generation", true, "Enable Birch Forest Villages generation (y/n)");

			AstrotweaksModVariables.Overworld_Quartz_Generation = safeGetBoolean(config, "Overworld_Quartz_Generation", "generation", true, "Enable Overworld Quartz Generation (y/n)");
			AstrotweaksModVariables.Ruby_Generation = safeGetBoolean(config, "Ruby_Generation", "generation", true, "Enable Ruby Generation (y/n)");
			ModVariables.Overworld_Minerals_Generation = safeGetBoolean(config, "Overworld_Minerals_Generation", "generation", true, "Enable Overworld Minerals Generation (y/n)");
			
			
			ModVariables.Enable_Ground_Elements = safeGetBoolean(config, "Enable_Ground_Elements", "generation", true, "Enable Ground elements generation (y/n)");
			// Double values with validation of range and parsing
			ModVariables.Stick_Gen_Attempts = safeGetDouble(config, "Stick_Gen_Attempts", "generation", ModVariables.Stick_Gen_Attempts /*def*/, 0.0 /*min*/, 999.0 /*max*/, "Number of attempts to generate an element (double num) [default: 2.0]");
			String[] StickBiomes = config.get("generation", "Stick_Gen_Biomes", STICK_BIOME_LIST, 
				"List of biome IDs (e.g., minecraft:plains) where sticks generate. " + "Empty list uses default biomes.").getStringList();
			ModVariables.Stick_Gen_Min_Y = safeGetInt(config, "Stick_Gen_Min_Y", "generation", ModVariables.Stick_Gen_Min_Y, 1, 255, "Minimum Y level for stick generation (1-255)");
			ModVariables.Stick_Gen_Max_Y = safeGetInt(config, "Stick_Gen_Max_Y", "generation", ModVariables.Stick_Gen_Max_Y, 1, 255, "Maximum Y level for stick generation (1-255)");

			ModVariables.Enable_Bushes = safeGetBoolean(config, "Enable_Bushes", "generation", true, "Enable bush generation in Overworld (y/n)");




			
			
			ModVariables.Rock_Gen_Attempts = safeGetDouble(config, "Rock_Gen_Attempts", "generation", ModVariables.Rock_Gen_Attempts /*def*/, 0.0 /*min*/, 999.0 /*max*/, "Number of attempts to generate an element (double num) [default: 0.3]");
			String[] RockBiomes = config.get("generation", "Rock_Gen_Biomes", ROCK_BIOME_LIST, 
				"List of biome IDs (e.g., minecraft:plains) where rocks generate. " + "Empty list uses default biomes.").getStringList();
			ModVariables.Rock_Gen_Min_Y = safeGetInt(config, "Rock_Gen_Min_Y", "generation", ModVariables.Rock_Gen_Min_Y, 1, 255, "Minimum Y level for rock generation (1-255)");
			ModVariables.Rock_Gen_Max_Y = safeGetInt(config, "Rock_Gen_Max_Y", "generation", ModVariables.Rock_Gen_Max_Y, 1, 255, "Maximum Y level for rock generation (1-255)");



			ModVariables.Extra_Fuels = safeGetBoolean(config, "Register_Extra_Fuels", "misc", true, "Should to register more fuels for furnace? (y/n)");
			ModVariables.Better_Smelting = safeGetBoolean(config, "Better_Smelting", "misc", true, "Enable more smelting recipes? (y/n)");
			AstrotweaksModVariables.Money_Can_Smelt = safeGetBoolean(config, "Money_Can_Smelt", "misc", true, "Can coins be melted down (y/n)");
			AstrotweaksModVariables.Money_Can_Craft = safeGetBoolean(config, "Money_Can_Craft", "misc", true, "Can copper coins be crafted at the MoneyTable from a copper plate (y/n)");
			AstrotweaksModVariables.Food_Negative_Effects = safeGetBoolean(config, "Food_Negative_Effects", "misc", true, "Will poisonous food have more violent effects? (y/n)");

			AstrotweaksModVariables.Enable_Depths_Dimension = safeGetBoolean(config, "Enable_Depths_Dimension", "world", true, "Should register Depths dimension? (y/n)");
			AstrotweaksModVariables.Enable_Depths_Dim_Bedrock_TP = safeGetBoolean(config, "Enable_Depths_Dim_Bedrock_TP", "world", true, "Allow access to the Depths via Bedrock? (y/n)");



			ModVariables.QM_is_fully_unbreakable = safeGetBoolean(config, "QM_is_fully_unbreakable", "Game mechanics", true, "Prohibit the player from breaking the QM_block");

			ModVariables.Enable_RealisticBreak = safeGetBoolean(config, "Enable_RealisticBreak", "Game mechanics", false, "More realistic conditions for destruction of blocks");



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
			
			for (String s : StickBiomes) {
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
	    "minecraft:redwood_taiga", "minecraft:redwood_taiga_hills", "minecraft:extreme_hills_with_trees", "minecraft:savanna", 
	    "minecraft:savanna_rock",
		"minecraft:mutated_forest","minecraft:mutated_taiga","minecraft:mutated_swampland","minecraft:mutated_jungle",
		"minecraft:mutated_jungle_edge","minecraft:mutated_birch_forest","minecraft:mutated_birch_forest_hills","minecraft:mutated_roofed_forest","minecraft:mutated_redwood_taiga",
		"minecraft:mutated_redwood_taiga_hills","minecraft:mutated_extreme_hills_with_trees","minecraft:mutated_savanna","minecraft:mutated_savanna_rock"
	};
	private static final String[] ROCK_BIOME_LIST = new String[]{
	    "minecraft:plains", "minecraft:extreme_hills", "minecraft:forest", "minecraft:taiga", "minecraft:river", "minecraft:beaches", 
	    "minecraft:forest_hills", "minecraft:taiga_hills", "minecraft:smaller_extreme_hills", "minecraft:jungle", "minecraft:jungle_hills", 
	    "minecraft:jungle_edge", "minecraft:stone_beach", "minecraft:birch_forest", "minecraft:birch_forest_hills", "minecraft:roofed_forest",
	    "minecraft:redwood_taiga", "minecraft:redwood_taiga_hills", "minecraft:extreme_hills_with_trees", "minecraft:savanna", 
	    "minecraft:savanna_rock",
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

	private boolean safeGetBoolean(Configuration config, String name, String category, boolean def, String comment) {
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

	private double safeGetDouble(Configuration config, String name, String category, double def, double min, double max, String comment) {
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
	
	private int safeGetInt(Configuration config, String name, String category, int def, int min, int max, String comment) {
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


}
