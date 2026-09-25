package astrotweaks.item;

import java.util.List;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import astrotweaks.creativetab.ATCreativeTabs;
import astrotweaks.item.TotemOfGod.*;;


public final class ATItems {

    public static final String MOD_ID = "astrotweaks";

    //private static int Money_ConvCount;


    // ===== Existing items =====
    public static final Item BRASS_X = createItem("brass_x", "brass_x", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null);
    public static final Item CARBON_STICK = createItem( "carbon_stick", "carbon_stick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item CORDAGE_FIBER = createItem( "cordage_fiber", "cordage_fiber", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item PLATINUM_COIN = createItem( "platinum_coin", "platinum_coin", ATCreativeTabs.ASTRO_TWEAKS_CT, 50, (stack, world, tooltip, flag) -> tooltip.add("1000 coins \u00A4") );
    public static final Item GOLD_COIN = createItem( "gold_coin", "gold_coin", ATCreativeTabs.ASTRO_TWEAKS_CT, 50, (stack, world, tooltip, flag) -> tooltip.add("100 coins \u00A4") );
    public static final Item COPPER_COIN = createItem( "copper_coin", "copper_coin", ATCreativeTabs.ASTRO_TWEAKS_CT, 50, (stack, world, tooltip, flag) -> tooltip.add("10 coins \u00A4") );
    public static final Item SILVER_COIN = createItem( "silver_coin", "silver_coin", ATCreativeTabs.ASTRO_TWEAKS_CT, 50, (stack, world, tooltip, flag) -> tooltip.add("500 coins \u00A4") );
    public static final Item STONE_COIN = createItem( "stone_coin", "stone_coin", ATCreativeTabs.ASTRO_TWEAKS_CT, 50, (stack, world, tooltip, flag) -> tooltip.add("0.1 coins \u00A4") );
    public static final Item WOOD_COIN = createItem( "wood_coin", "wood_coin", ATCreativeTabs.ASTRO_TWEAKS_CT, 50, (stack, world, tooltip, flag) -> tooltip.add("0.01 coins \u00A4") );
    public static final Item DIAMANT_COIN = createItem( "diamant_coin", "diamant_coin", ATCreativeTabs.ASTRO_TWEAKS_CT, 50, (stack, world, tooltip, flag) -> tooltip.add("10,000 coins \u00A4") );
    public static final Item ADAMANTIUM_COIN = createItem( "adamantium_coin", "adamantium_coin", ATCreativeTabs.ASTRO_TWEAKS_CT, 50, (stack, world, tooltip, flag) -> tooltip.add("100,000,000 coins \u00A4") );
    public static final Item ELUNITE_COIN = createItem( "elunite_coin", "elunite_coin", ATCreativeTabs.ASTRO_TWEAKS_CT, 50, (stack, world, tooltip, flag) -> tooltip.add("1,000,000 coins \u00A4") );
    public static final Item MYTHRIL_COIN = createItem( "mythril_coin", "mythril_coin", ATCreativeTabs.ASTRO_TWEAKS_CT, 50, (stack, world, tooltip, flag) -> tooltip.add("10,000,000 coins \u00A4") );
    public static final Item PALLADIUM_COIN = createItem( "palladium_coin", "palladium_coin", ATCreativeTabs.ASTRO_TWEAKS_CT, 50, (stack, world, tooltip, flag) -> tooltip.add("100,000 coins \u00A4") );
    public static final Item UNI_COIN = createItem( "uni_coin", "uni_coin", ATCreativeTabs.ASTRO_TWEAKS_CT, 50, (stack, world, tooltip, flag) -> tooltip.add("1,000,000,000 coins \u00A4") );
    public static final Item QUAD_ABGD_QUANT = createItem( "quad_abgd_quant", "quad_abgd_quant", ATCreativeTabs.AT_INTEGRATION_CT, 64, null );
    public static final Item ADVANCED_WIRELESS_MODULE = createItem( "advanced_wireless_module", "advanced_wireless_module", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item ALUMINIUM_STICK = createItem( "aluminium_stick", "aluminium_stick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item ALUMINIUM_X = createItem( "aluminium_x", "aluminium_x", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item BLUE_LED = createItem( "blue_led", "blue_led", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item BONE_SHARD = createItem( "bone_shard", "bone_shard", null, 64, null );
    public static final Item BRASS_DUST = createItem( "brass_dust", "brass_dust", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item BRASS_INGOT = createItem( "brass_ingot", "brass_ingot", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item BRASS_NUGGET = createItem( "brass_nugget", "brass_nugget", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item BRASS_STICK = createItem( "brass_stick", "brass_stick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item BRONZE_STICK = createItem( "bronze_stick", "bronze_stick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item BRONZE_X = createItem( "bronze_x", "bronze_x", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item CAPACITOR = createItem( "capacitor", "capacitor", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item CARBON_X = createItem( "carbon_x", "carbon_x", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item CEMENT_DUST = createItem( "cement_dust", "cement_dust", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item CHAIN_CANVAS = createItem( "chain_canvas", "chain_canvas", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item CLAY_BRICK = createItem( "clay_brick", "clay_brick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item COBALT_STICK = createItem( "cobalt_stick", "cobalt_stick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item COBALT_X = createItem( "cobalt_x", "cobalt_x", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item COPPER_STICK = createItem( "copper_stick", "copper_stick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item COPPER_X = createItem( "copper_x", "copper_x", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item CORDAGE_VINE = createItem( "cordage_vine", "cordage_vine", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item CORE_EMPTY = createItem( "core_empty", "core_empty", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item DIAMOND_SHARD = createItem( "diamond_shard", "diamond_shard", null, 64, null );
    public static final Item DIAMOND_STICK = createItem( "diamond_stick", "diamond_stick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item DIAMOND_X = createItem( "diamond_x", "diamond_x", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item DIODE_2 = createItem( "diode_2", "diode_2", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item DRAWING = createItem( "drawing", "drawing", ATCreativeTabs.ASTRO_TWEAKS_CT, 16, null );
    public static final Item ELECTRON = createItem( "electron", "electron", ATCreativeTabs.AT_INTEGRATION_CT, 64, null );
    public static final Item ELECTRUM_STICK = createItem( "electrum_stick", "electrum_stick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item ELECTRUM_X = createItem( "electrum_x", "electrum_x", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item EMERALD_STICK = createItem( "emerald_stick", "emerald_stick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item EMERALD_X = createItem( "emerald_x", "emerald_x", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item FLINT_SHARD = createItem( "flint_shard", "flint_shard", null, 64, null );
    public static final Item FULMINATE_POWDER = createItem( "fulminate_powder", "fulminate_powder", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item GENETICS = createItem( "genetics", "genetics", null, 64, null );
    public static final Item GOLD_STICK = createItem( "gold_stick", "gold_stick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item GOLD_X = createItem( "gold_x", "gold_x", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item GREEN_LED = createItem( "green_led", "green_led", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item HEART = createItem( "heart", "heart", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item HOLO_MEM_CRYSTAL_1 = createItem( "holo_mem_crystal_1", "holo_mem_crystal_1", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, (stack, world, tooltip, flag) -> tooltip.add("Al2O3 ^ 3") );
    public static final Item HOLO_MEM_CRYSTAL_2 = createItem( "holo_mem_crystal_2", "holo_mem_crystal_2", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, (stack, world, tooltip, flag) -> tooltip.add("SiO2+NaO2+Ag2O+B2O3 ^ 3") );
    public static final Item HOLO_MEM_CRYSTAL_3 = createItem( "holo_mem_crystal_3", "holo_mem_crystal_3", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, (stack, world, tooltip, flag) -> tooltip.add("LiNbO3 ^ 3") );
    public static final Item HOLO_MEM_CRYSTAL_4 = createItem( "holo_mem_crystal_4", "holo_mem_crystal_4", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, (stack, world, tooltip, flag) -> tooltip.add("SiO2 ^ 3") );
    public static final Item HOLO_MEM_CRYSTAL_5 = createItem( "holo_mem_crystal_5", "holo_mem_crystal_5", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, (stack, world, tooltip, flag) -> tooltip.add("C ^ 3") );
    public static final Item COPPER_COIL = createItem( "copper_coil", "copper_coil", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, (stack, world, tooltip, flag) -> tooltip.add("Tier: 1") );
    public static final Item GOLDEN_COIL = createItem( "golden_coil", "golden_coil", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, (stack, world, tooltip, flag) -> tooltip.add("Tier: 2") );
    public static final Item INTECORE_EMPTY = createItem( "intecore_empty", "intecore_empty", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, (stack, world, tooltip, flag) -> tooltip.add("Integrator (component)") );
    public static final Item INFINITY_SINGULARITY = createItem( "infinity_singularity", "infinity_singularity", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item IRIDIUM_STICK = createItem( "iridium_stick", "iridium_stick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item IRIDIUM_X = createItem( "iridium_x", "iridium_x", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item IRON_STICK = createItem( "iron_stick", "iron_stick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item IRON_X = createItem( "iron_x", "iron_x", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item LASER_EMITTER = createItem( "laser_emitter", "laser_emitter", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item LBLUE_LED = createItem( "lblue_led", "lblue_led", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item LENS_ADVANCED = createItem( "lens_advanced", "lens_advanced", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item LENS_BASIC = createItem( "lens_basic", "lens_basic", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item LENS_HARDENED = createItem( "lens_hardened", "lens_hardened", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item LENS_NORMALIZATION = createItem( "lens_normalization", "lens_normalization", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item LENS_SPECTRALL = createItem( "lens_spectrall", "lens_spectrall", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item LIGHT_MID = createItem( "light_mid", "light_mid", null, 64, null );
    public static final Item LIGHT_OFF = createItem( "light_off", "light_off", null, 64, null );
    public static final Item LIGHT_ON = createItem( "light_on", "light_on", null, 64, null );
    public static final Item LOGIC_RS_MODULE = createItem( "logic_rs_module", "logic_rs_module", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item MAGNETIC_STABILIZERS = createItem( "magnetic_stabilizers", "magnetic_stabilizers", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item MATTER_CRYSTAL = createItem( "matter_crystal", "matter_crystal", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item METEORIC_STICK = createItem( "meteoric_stick", "meteoric_stick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item METEORIC_IRON_X = createItem( "meteoric_iron_x", "meteoric_iron_x", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item MINERAL_STEEL_DUST = createItem( "mineral_steel_dust", "mineral_steel_dust", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item MINERAL_STEEL_INGOT = createItem( "mineral_steel_ingot", "mineral_steel_ingot", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item NEUTRONIUM_SINGULARITY = createItem( "neutronium_singularity", "neutronium_singularity", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item NICKEL_STICK = createItem( "nickel_stick", "nickel_stick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item NICKEL_X = createItem( "nickel_x", "nickel_x", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item NUCLEAR = createItem( "nuclear", "nuclear", null, 64, null );
    public static final Item NULL_ITEM = createItem( "null_item", "null_item", null, 64, null );
    public static final Item NULL_QUANT = createItem( "null_quant", "null_quant", ATCreativeTabs.AT_INTEGRATION_CT, 64, null );
    public static final Item ORANGE_LED = createItem( "orange_led", "orange_led", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item Q_HYPERON = createItem( "q_hyperon", "q_hyperon", ATCreativeTabs.AT_INTEGRATION_CT, 64, null );
    public static final Item Q_NEUTRON = createItem( "q_neutron", "q_neutron", ATCreativeTabs.AT_INTEGRATION_CT, 64, null );
    public static final Item Q_PROTON = createItem( "q_proton", "q_proton", ATCreativeTabs.AT_INTEGRATION_CT, 64, null );
    public static final Item QUAD_AAAA_QUANT = createItem( "quad_aaaa_quant", "quad_aaaa_quant", ATCreativeTabs.AT_INTEGRATION_CT, 64, null );
    public static final Item QUAD_BBBB_QUANT = createItem( "quad_bbbb_quant", "quad_bbbb_quant", ATCreativeTabs.AT_INTEGRATION_CT, 64, null );
    public static final Item QUAD_DDDD_QUANT = createItem( "quad_dddd_quant", "quad_dddd_quant", ATCreativeTabs.AT_INTEGRATION_CT, 64, null );
    public static final Item QUAD_GGGG_QUANT = createItem( "quad_gggg_quant", "quad_gggg_quant", ATCreativeTabs.AT_INTEGRATION_CT, 64, null );
    public static final Item RED_LED = createItem( "red_led", "red_led", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item REDSTONE_BRICK = createItem( "redstone_brick", "redstone_brick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item RESISTOR = createItem( "resistor", "resistor", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item ROCK = createItem( "rock", "rock", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item ROCK_FLAT = createItem( "rock_flat", "rock_flat", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item RUBBER_LUMP = createItem( "rubber_lump", "rubber_lump", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item RUBY = createItem( "ruby", "ruby", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item RUBY_DUST = createItem( "ruby_dust", "ruby_dust", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item RUBY_STICK = createItem( "ruby_stick", "ruby_stick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item RUBY_X = createItem( "ruby_x", "ruby_x", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SAW_FRAME = createItem( "saw_frame", "saw_frame", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SCHEME = createItem( "scheme", "scheme", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SEED_OF_LIFE = createItem( "seed_of_life", "seed_of_life", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, (stack, world, tooltip, flag) -> tooltip.add("???") );
    public static final Item SILICON_CRYSTAL = createItem( "silicon_crystal", "silicon_crystal", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SILICON_DISC = createItem( "silicon_disc", "silicon_disc", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SILVER_STICK = createItem( "silver_stick", "silver_stick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SILVER_X = createItem( "silver_x", "silver_x", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SOME_ARROWS = createItem( "some_arrows", "some_arrows", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SOME_BLAZE_RODS = createItem( "some_blaze_rods", "some_blaze_rods", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SOME_BONES = createItem( "some_bones", "some_bones", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SOME_BOOKS = createItem( "some_books", "some_books", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SOME_DIAMOND_SHARDS = createItem( "some_diamond_shards", "some_diamond_shards", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SOME_ENDER_EYES = createItem( "some_ender_eyes", "some_ender_eyes", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SOME_ENDER_PEARLS = createItem( "some_ender_pearls", "some_ender_pearls", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SOME_FEATHERS = createItem( "some_feathers", "some_feathers", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SOME_FIREBALLS = createItem( "some_fireballs", "some_fireballs", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SOME_GOLDEN_APPLES = createItem( "some_golden_apples", "some_golden_apples", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SOME_GOLDEN_CARROTS = createItem( "some_golden_carrots", "some_golden_carrots", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SOME_GUNPOWDER = createItem( "some_gunpowder", "some_gunpowder", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SOME_LEATHER = createItem( "some_leather", "some_leather", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SOME_MAGMA_CREAM = createItem( "some_magma_cream", "some_magma_cream", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SOME_PAPER = createItem( "some_paper", "some_paper", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SOME_ROTTEN_FLESH = createItem( "some_rotten_flesh", "some_rotten_flesh", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SOME_STRINGS = createItem( "some_strings", "some_strings", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SOME_SUGAR_CANES = createItem( "some_sugar_canes", "some_sugar_canes", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SPACE_STATION_ICO = createItem( "space_station_ico", "space_station_ico", null, 64, null );
    public static final Item SPOILED_EGG = createItem( "spoiled_egg", "spoiled_egg", null, 64, null );
    public static final Item SPOILED_RABBIT_FOOT = createItem( "spoiled_rabbit_foot", "spoiled_rabbit_foot", null, 64, null );
    public static final Item SPOILED_SPIDER_EYE_2 = createItem( "spoiled_spider_eye_2", "spoiled_spider_eye_2", null, 64, null );
    public static final Item STAR_FRAGMENT = createItem( "star_fragment", "star_fragment", null, 64, null );
    public static final Item STEEL_STICK = createItem( "steel_stick", "steel_stick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item STEEL_X = createItem( "steel_x", "steel_x", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item STICK_BUNDLE = createItem( "stick_bundle", "stick_bundle", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item STONE_BRICK = createItem( "stone_brick", "stone_brick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item STRANGE_CLOT = createItem( "strange_clot", "strange_clot", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item STRANGE_MATTER = createItem( "strange_matter", "strange_matter", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item STRANGE_ORB = createItem( "strange_orb", "strange_orb", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item SUBSPACE_ICO = createItem( "subspace_ico", "subspace_ico", null, 64, null );
    public static final Item SUPER_WIRELESS_MODULE = createItem( "super_wireless_module", "super_wireless_module", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item TIN_STICK = createItem( "tin_stick", "tin_stick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item TIN_X = createItem( "tin_x", "tin_x", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item TITANIUM_STICK = createItem( "titanium_stick", "titanium_stick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item TITANIUM_X = createItem( "titanium_x", "titanium_x", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item TOOL_MOUNT = createItem( "tool_mount", "tool_mount", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item TRIPLE_ABD_QUANT = createItem( "triple_abd_quant", "triple_abd_quant", ATCreativeTabs.AT_INTEGRATION_CT, 64, null );
    public static final Item TRIPLE_ABG_QUANT = createItem( "triple_abg_quant", "triple_abg_quant", ATCreativeTabs.AT_INTEGRATION_CT, 64, null );
    public static final Item TRIPLE_AGD_QUANT = createItem( "triple_agd_quant", "triple_agd_quant", ATCreativeTabs.AT_INTEGRATION_CT, 64, null );
    public static final Item TRIPLE_BGD_QUANT = createItem( "triple_bgd_quant", "triple_bgd_quant", ATCreativeTabs.AT_INTEGRATION_CT, 64, null );
    public static final Item URANIUM_STICK = createItem( "uranium_stick", "uranium_stick", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item URANIUM_X = createItem( "uranium_x", "uranium_x", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item VIOLET_LED = createItem( "violet_led", "violet_led", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item VOID_ANTIMATTER_CORE = createItem( "void_antimatter_core", "void_antimatter_core", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, (stack, world, tooltip, flag) -> tooltip.add("stable!") );
    public static final Item VOID_CORE = createItem( "void_core", "void_core", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item VOID_MATTER = createItem( "void_matter", "void_matter", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );
    public static final Item VOID_MATTER_CORE = createItem( "void_matter_core", "void_matter_core", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, (stack, world, tooltip, flag) -> tooltip.add("stable!") );
    public static final Item YELLOW_LED = createItem( "yellow_led", "yellow_led", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, null );

    public static final Item SURVIVALIST_BADGE = createItem( "survivalist_badge", "survivalist_badge", ATCreativeTabs.ASTRO_TWEAKS_CT, 1, null );  
    public static final Item PHILISTINE_BADGE = createItem( "philistine_badge", "philistine_badge", ATCreativeTabs.ASTRO_TWEAKS_CT, 1, null );  
    public static final Item ENGENEER_BADGE = createItem( "engeneer_badge", "engeneer_badge", ATCreativeTabs.ASTRO_TWEAKS_CT, 1, null );  
    public static final Item MASTER_BADGE = createItem( "master_badge", "master_badge", ATCreativeTabs.ASTRO_TWEAKS_CT, 1, null );  
    public static final Item PHOENIX_BADGE = createItem( "phoenix_badge", "phoenix_badge", ATCreativeTabs.ASTRO_TWEAKS_CT, 1, null );  
    public static final Item SPACE_BADGE = createItem( "space_badge", "space_badge", ATCreativeTabs.ASTRO_TWEAKS_CT, 1, null );  
    public static final Item SINGULARITY_BADGE = createItem( "singularity_badge", "singularity_badge", ATCreativeTabs.ASTRO_TWEAKS_CT, 1, null );  
    public static final Item QUANTUM_BADGE = createItem( "quantum_bagde", "quantum_bagde", ATCreativeTabs.ASTRO_TWEAKS_CT, 1, null );  
    public static final Item PLANET_BADGE = createItem( "planet_badge", "planet_badge", ATCreativeTabs.ASTRO_TWEAKS_CT, 1, null );  
    public static final Item QUASAR_BADGE = createItem( "quasar_badge", "quasar_badge", ATCreativeTabs.ASTRO_TWEAKS_CT, 1, null );  
    
    public static final Item SCIENCE = createItem( "science", "science", ATCreativeTabs.ASTRO_TWEAKS_CT, 1, (stack, world, tooltip, flag) -> tooltip.add("Under development") );  
    public static final Item PLANT_FIBER = createItem( "plant_fiber", "plant_fiber", ATCreativeTabs.ASTRO_TWEAKS_CT, 64, (stack, world, tooltip, flag) -> tooltip.add(new TextComponentTranslation("item.plant_fiber.tooltip").getFormattedText()) );  





    private ATItems() {}

    public static final void ClearRegList() {
        ItemsToRegister = null;
    }

    private static Item[] ItemsToRegister = {



WOOD_COIN,STONE_COIN,COPPER_COIN,SILVER_COIN,GOLD_COIN,PLATINUM_COIN,DIAMANT_COIN,PALLADIUM_COIN,ELUNITE_COIN,MYTHRIL_COIN,ADAMANTIUM_COIN,UNI_COIN,



ALUMINIUM_X,BRASS_X,BRONZE_X,COPPER_X,CARBON_X,COBALT_X,DIAMOND_X,ELECTRUM_X,EMERALD_X,GOLD_X,IRIDIUM_X,IRON_X,METEORIC_IRON_X,NICKEL_X,RUBY_X,SILVER_X,STEEL_X,TIN_X,TITANIUM_X,URANIUM_X,
ALUMINIUM_STICK,BRASS_STICK,BRONZE_STICK,CARBON_STICK,COBALT_STICK,COPPER_STICK,DIAMOND_STICK,ELECTRUM_STICK,EMERALD_STICK,GOLD_STICK,IRIDIUM_STICK,IRON_STICK,METEORIC_STICK,NICKEL_STICK,RUBY_STICK,
SILVER_STICK,STEEL_STICK,TIN_STICK,TITANIUM_STICK,URANIUM_STICK,

PLANT_FIBER,CORDAGE_FIBER,CORDAGE_VINE,ROCK,ROCK_FLAT,BONE_SHARD,FLINT_SHARD,DIAMOND_SHARD,CLAY_BRICK,STONE_BRICK,REDSTONE_BRICK, 
CEMENT_DUST,

RUBY_DUST,RUBY,
BRASS_DUST,BRASS_INGOT,BRASS_NUGGET,
MINERAL_STEEL_DUST,MINERAL_STEEL_INGOT,

DRAWING,HEART,SCHEME,SEED_OF_LIFE,
CHAIN_CANVAS,FULMINATE_POWDER,LOGIC_RS_MODULE,SAW_FRAME,TOOL_MOUNT,RUBBER_LUMP,



  

SPOILED_EGG, SPOILED_RABBIT_FOOT, SPOILED_SPIDER_EYE_2, STAR_FRAGMENT,        
      

CORE_EMPTY,VOID_ANTIMATTER_CORE,VOID_CORE,VOID_MATTER,VOID_MATTER_CORE,MATTER_CRYSTAL,STRANGE_MATTER,STRANGE_CLOT,STRANGE_ORB,


STICK_BUNDLE,SOME_ARROWS,SOME_BLAZE_RODS,SOME_BONES,SOME_BOOKS,SOME_DIAMOND_SHARDS,SOME_ENDER_EYES,SOME_ENDER_PEARLS,SOME_FEATHERS,SOME_FIREBALLS,SOME_GOLDEN_APPLES,SOME_GOLDEN_CARROTS,SOME_GUNPOWDER,
SOME_LEATHER,SOME_MAGMA_CREAM,SOME_PAPER,SOME_ROTTEN_FLESH,SOME_STRINGS,SOME_SUGAR_CANES,

RED_LED,ORANGE_LED,YELLOW_LED,GREEN_LED,LBLUE_LED,BLUE_LED,VIOLET_LED,
CAPACITOR,RESISTOR,DIODE_2,COPPER_COIL,GOLDEN_COIL,
SILICON_CRYSTAL,SILICON_DISC,
LENS_BASIC,LENS_ADVANCED,LENS_HARDENED,LENS_NORMALIZATION,LENS_SPECTRALL,
ADVANCED_WIRELESS_MODULE,SUPER_WIRELESS_MODULE,HOLO_MEM_CRYSTAL_1,HOLO_MEM_CRYSTAL_2,HOLO_MEM_CRYSTAL_3,HOLO_MEM_CRYSTAL_4,HOLO_MEM_CRYSTAL_5,
MAGNETIC_STABILIZERS,LASER_EMITTER,INTECORE_EMPTY,


ELECTRON,Q_PROTON,Q_NEUTRON,Q_HYPERON,
TRIPLE_ABD_QUANT,TRIPLE_ABG_QUANT,TRIPLE_AGD_QUANT,TRIPLE_BGD_QUANT, 
QUAD_ABGD_QUANT,QUAD_AAAA_QUANT,QUAD_BBBB_QUANT,QUAD_GGGG_QUANT,QUAD_DDDD_QUANT,
NULL_QUANT,NULL_ITEM,

NEUTRONIUM_SINGULARITY,INFINITY_SINGULARITY,

LIGHT_OFF,LIGHT_MID,LIGHT_ON,
SCIENCE,GENETICS,NUCLEAR,SPACE_STATION_ICO,SUBSPACE_ICO,
SURVIVALIST_BADGE,PHILISTINE_BADGE,ENGENEER_BADGE,MASTER_BADGE,PHOENIX_BADGE,SPACE_BADGE,SINGULARITY_BADGE,QUANTUM_BADGE,PLANET_BADGE,QUASAR_BADGE, 

// ===== Converted elements =====

// Armor
ItemBrassArmor.HELMET,
ItemBrassArmor.CHESTPLATE,
ItemBrassArmor.LEGGINGS,
ItemBrassArmor.BOOTS,
ItemRubyArmor.HELMET,
ItemRubyArmor.CHESTPLATE,
ItemRubyArmor.LEGGINGS,
ItemRubyArmor.BOOTS,
EmeraldArmorItems.HELMET,
EmeraldArmorItems.CHESTPLATE,
EmeraldArmorItems.LEGGINGS,
EmeraldArmorItems.BOOTS,
ItemSapphireArmor.HELMET,
ItemSapphireArmor.CHESTPLATE,
ItemSapphireArmor.LEGGINGS,
ItemSapphireArmor.BOOTS,

// Brass tools
ItemBrassSword.SWORD,
ItemBrassAxe.AXE,
ItemBrassPickaxe.PICKAXE,
ItemBrassShovel.SHOVEL,
ItemBrassHoe.HOE,

// Ruby tools
ItemRubySword.SWORD,
ItemRubyAxe.AXE,
ItemRubyPickaxe.PICKAXE,
ItemRubyShovel.SHOVEL,
ItemRubyHoe.HOE,

// Emerald tools
ItemEmeraldSword.SWORD,
ItemEmeraldAxe.AXE,
ItemEmeraldShovel.SHOVEL,
ItemEmeraldPickaxe.PICKAXE,
ItemEmeraldHoe.HOE,

// Crystal tools
ItemCrystalSword.SWORD,
ItemCrystalAxe.AXE,
ItemCrystalPickaxe.PICKAXE,
ItemCrystalShovel.SHOVEL,
ItemCrystalHoe.HOE,
ItemCrystalMultitool.MULTITOOL,

// Neutronium tools
ItemNeutroniumSword.SWORD,
ItemNeutroniumAxe.AXE,
ItemNeutroniumPickaxe.PICKAXE,
ItemNeutroniumShovel.SHOVEL,
ItemNeutroniumHoe.HOE,
ItemNeutroniumMultitool.MULTITOOL,

// Saws
ItemSawDiamond.DIAMOND_SAW,
ItemSawIron.IRON_SAW,
ItemCopperSaw.COPPER_SAW,
ItemBronzeSaw.BRONZE_SAW,
ItemGoldenSaw.GOLDEN_SAW,
ItemSteelSaw.STEEL_SAW,
ItemTinSaw.TIN_SAW,

// Quants
ItemAlphaQuant.ALPHA_QUANT,
ItemBetaQuant.BETA_QUANT,
ItemGammaQuant.GAMMA_QUANT,
ItemDeltaQuant.DELTA_QUANT,
ItemStrangeQuant.STRANGE_QUANT,
ItemYAntiQuant.Y_ANTI_QUANT,

// Food
ItemCoockedTropicalFish.TROPICAL_FISH,
ItemCoockedPufferfish.PUFFERFISH,
ItemDryBread.DRY_BREAD,
ItemMilkBottle.MILK_BOTTLE,

// Spoiled food
ItemSpoiledBread.SPOILED_BREAD,
ItemSpoiledCarrotOnStick.SPOILED_CARROT_ON_STICK,
ItemSpoiledDrink.SPOILED_DRINK,
ItemSpoiledFishX.SPOILED_FISH,
ItemSpoiledFishPufferfish1.SPOILED_PUFFERFISH,
ItemSpoiledFoodX.SPOILED_FOOD,
ItemSpoiledPumpkinPie.SPOILED_PUMPKIN_PIE,
ItemSpoiledSoup.SPOILED_SOUP,
ItemSpoiledSpiderEye1.SPOILED_SPIDER_EYE,
ItemSpoiledVegetablesX.SPOILED_VEGETABLES,

// Misc
ItemGavel.GAVEL,
ItemXpBoxU.XP_BOX,
ItemPowerUnitX.POWER_UNIT_X,
ItemSilkeBadge.SILKE_BADGE,
ItemUnstableVoidCore.UNSTABLE_VOID_CORE,
ItemUDestroyerItem.DESTROYER_ITEM,
ItemVoidAntimatter.VOID_ANTIMATTER,
TotemOfGod.TOTEM_OF_GOD,
astrotweaks.item.SpatialAnchor.SpatialAnchor.SPATIAL_ANCHOR,


    };

    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
            ItemsToRegister
        );
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, value = Side.CLIENT)
    public static class ClientHandler {
        @SideOnly(Side.CLIENT)
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event) {
            for (Item item : ItemsToRegister) { registerModel(item); }
        }
        @SideOnly(Side.CLIENT)
        private static void registerModel(Item item) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }

    private static Item createItem(String registryName, String unlocalizedName, CreativeTabs creativeTab, int maxStackSize, TooltipConsumer tooltipConsumer) {
        return new ItemWithTooltip(tooltipConsumer)
                .setRegistryName(MOD_ID, registryName)
                .setUnlocalizedName(unlocalizedName)
                .setCreativeTab(creativeTab)
                .setMaxStackSize(maxStackSize);
    }

    @FunctionalInterface
    private interface TooltipConsumer {
        void accept(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag);
    }

    private static class ItemWithTooltip extends Item {
        private final TooltipConsumer tooltipConsumer;
        private ItemWithTooltip(TooltipConsumer tooltipConsumer) {
            this.tooltipConsumer = tooltipConsumer;
        }
        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
            super.addInformation(stack, world, tooltip, flag);

            if (tooltipConsumer != null) {
                tooltipConsumer.accept(stack, world, tooltip, flag);
            }
        }
    }
}
