package astrotweaks.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.ResourceLocation;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;


import java.util.*;

public class RecipeHandler {
    private static final Logger LOGGER = LogManager.getLogger("astrotweaks");

	// Set (immutable)
    public static final Set<String> RECIPES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(


"S 000,010,000; 0=avaritia:resource:7;1=minecraft:dye:11;RES=minecraft:record_13",
"S 000,010,000; 0=avaritia:resource:7;1=minecraft:dye:0; RES=minecraft:record_11",
"S 000,010,000; 0=avaritia:resource:7;1=minecraft:dye:10;RES=minecraft:record_cat",
"S 000,010,000; 0=avaritia:resource:7;1=minecraft:dye:14;RES=minecraft:record_blocks",
"S 000,010,000; 0=avaritia:resource:7;1=minecraft:dye:6; RES=minecraft:record_far",
"S 000,010,000; 0=avaritia:resource:7;1=minecraft:dye:1; RES=minecraft:record_chirp",
"S 000,010,000; 0=avaritia:resource:7;1=minecraft:dye:5; RES=minecraft:record_mall",
"S 000,010,000; 0=avaritia:resource:7;1=minecraft:dye:13;RES=minecraft:record_mellohi",
"S 000,010,000; 0=avaritia:resource:7;1=minecraft:dye:12;RES=minecraft:record_wait",
"S 000,010,000; 0=avaritia:resource:7;1=minecraft:dye:2; RES=minecraft:record_ward",
"S 000,010,000; 0=avaritia:resource:7;1=minecraft:dye:15;RES=minecraft:record_strad",
"S 000,010,000; 0=avaritia:resource:7;1=minecraft:coal;  RES=minecraft:record_stal",

"S 000,010,000; 0=astrotweaks:strange_matter;1=minecraft:ender_pearl;RES=astrotweaks:strange_orb",

"S 202,121,202;0=minecraft:dye; 1=minecraft:dye:11; 2=ore:ingotIron;RES=astrotweaks:hazard_zone_tape.4",
"S 202,121,202;0=minecraft:dye:1;1=minecraft:dye:15;2=ore:ingotIron;RES=astrotweaks:hazard_tape.4",
"S 202,121,202;0=minecraft:dye:13;1=minecraft:dye:11;2=ore:ingotIron;RES=astrotweaks:radiation_hazard_tape.4",
"S 202,121,202;0=minecraft:dye;1=minecraft:dye:15;2=ore:ingotIron;RES=astrotweaks:aisle_cleaning_tape.4",
"S 202,121,202;0=minecraft:dye:10;1=minecraft:dye:15;2=ore:ingotIron;RES=astrotweaks:safety_zone_tape.4",
"S 202,121,202;0=minecraft:dye:4;1=minecraft:dye:15;2=ore:ingotIron; RES=astrotweaks:dont_walk_tape.4",
"S 202,121,202;0=minecraft:dye:14;1=minecraft:dye:15;2=ore:ingotIron;RES=astrotweaks:technical_works_tape.4",

// misc

"# astrotweaks:plant_fiber.3;RES=astrotweaks:cordage_fiber",
"# astrotweaks:cordage_fiber.2;RES=minecraft:string",
"# minecraft:nether_star.9;RES=astrotweaks:netherstar_block",
"# astrotweaks:netherstar_block;RES=minecraft:nether_star.9",
"# minecraft:paper.4;minecraft:feather;minecraft:dye;RES=astrotweaks:drawing",
"S 00,00,;0=astrotweaks:stone_brick;RES=minecraft:stonebrick",
"# minecraft:red_mushroom.9;RES=minecraft:red_mushroom_block",
"# minecraft:brown_mushroom.9;RES=minecraft:brown_mushroom_block",
"# minecraft:red_mushroom_block;RES=minecraft:red_mushroom.9",
"# minecraft:brown_mushroom_block;RES=minecraft:brown_mushroom.9",
"# minecraft:clay_ball.2;RES=astrotweaks:clay_brick",
"S 000,0 0,000; 0=astrotweaks:iron_stick;RES=astrotweaks:metal_frame.4",
"# minecraft:brown_mushroom.4;RES=astrotweaks:brown_mushrooms",
"# astrotweaks:brown_mushrooms;RES=minecraft:brown_mushroom.4",
"# minecraft:red_mushroom.4;RES=astrotweaks:red_mushrooms",
"# astrotweaks:red_mushrooms;RES=minecraft:red_mushroom.4",

"# astrotweaks:spoiled_carrot_on_stick;RES=minecraft:fishing_rod",
"# minecraft:carrot_on_a_stick;RES=minecraft:fishing_rod",


"S 0  ,00 ,000; 0=astrotweaks:cobbled_deepslate; RES=astrotweaks:cobbled_deepslate_stairs.4",
"S 0  ,00 ,000; 0=astrotweaks:deepslate_bricks; RES=astrotweaks:deepslate_bricks_stairs.4",
"S 0  ,00 ,000; 0=astrotweaks:deepslate_tiles; RES=astrotweaks:deepslate_tiles_stairs.4",
"S 000,,; 0=astrotweaks:cobbled_deepslate; RES=astrotweaks:cobbled_deepslate_slab.6",
"S 000,,; 0=astrotweaks:deepslate_bricks; RES=astrotweaks:deepslate_bricks_slab.6",
"S 000,,; 0=astrotweaks:deepslate_tiles; RES=astrotweaks:deepslate_tiles_slab.6",

"S 00,00,; 0=astrotweaks:deepslate; RES=astrotweaks:deepslate_bricks.4",
"S 00,00,; 0=minecraft:dirt; RES=astrotweaks:dirt_bricks.4",


"# minecraft:cobblestone;RES=astrotweaks:rock.9",
"# astrotweaks:rock.9;RES=minecraft:cobblestone",


"# pyrotech:material:12;RES=astrotweaks:plant_fiber",
"# astrotweaks:plant_fiber;RES=pyrotech:material:12",
"# astrotweaks:rock.2;RES=astrotweaks:rock_flat.3",
"# astrotweaks:rock;ore:toolHammer;RES=astrotweaks:rock_flat.2",
"# astrotweaks:gavel;minecraft:stonebrick:0;RES=astrotweaks:stone_brick.4",

"S ,010, 0 ; 0=minecraft:stick; 1=ore:plankWood; RES=astrotweaks:tool_mount",

"S 010,121,010;0=minecraft:paper;1=astrotweaks:fulminate_powder;2=minecraft:gunpowder; RES=astrotweaks:fexplosive",
"# ore:dustSilver.2; ore:dustCoal.2; ore:dustSaltpeter.2; ore:dustRedstone.2; RES=astrotweaks:fulminate_powder.4",




// armor, weapons & tools

"S 0 ,0 ,12; 0=astrotweaks:rock_flat; 1=minecraft:stick; 2=ore:twine;  RES=minecraft:stone_sword",
"S 000, 12, 1 ; 0=astrotweaks:rock_flat; 1=minecraft:stick; 2=ore:twine;  RES=minecraft:stone_pickaxe",
"S 012,01 , 1 ; 0=astrotweaks:rock_flat; 1=minecraft:stick; 2=ore:twine;  RES=minecraft:stone_axe",
"S 01,21,; 0=astrotweaks:rock; 1=minecraft:stick; 2=ore:twine;  RES=minecraft:stone_axe",
//"# astrotweaks:rock;minecraft:stick;ore:twine;RES=minecraft:stone_axe",
"S 02,1 ,1 ; 0=astrotweaks:rock; 1=minecraft:stick; 2=ore:twine;  RES=minecraft:stone_shovel",
"S 002, 1 , 1 ; 0=astrotweaks:rock_flat; 1=minecraft:stick; 2=ore:twine; RES=minecraft:stone_hoe",


"S 0 0, 0 ,0 0; 0=minecraft:iron_nugget; RES=astrotweaks:chain_canvas",
"S 000,0 0,   ; 0=astrotweaks:chain_canvas; RES=minecraft:chainmail_helmet",
"S 0 0,000,000; 0=astrotweaks:chain_canvas; RES=minecraft:chainmail_chestplate",
"S 000,0 0,0 0; 0=astrotweaks:chain_canvas; RES=minecraft:chainmail_leggings",
"S    ,0 0,0 0; 0=astrotweaks:chain_canvas; RES=minecraft:chainmail_boots",

"S 000,0 0,   ; 0=ore:gemRuby; RES=astrotweaks:ruby_helmet",
"S 0 0,000,000; 0=ore:gemRuby; RES=astrotweaks:ruby_chestplate",
"S 000,0 0,0 0; 0=ore:gemRuby; RES=astrotweaks:ruby_leggings",
"S    ,0 0,0 0; 0=ore:gemRuby; RES=astrotweaks:ruby_boots",
"S 0 ,0 ,1 ;    0=ore:gemRuby; 1=minecraft:stick; RES=astrotweaks:ruby_sword",
"S 000, 1 , 1 ; 0=ore:gemRuby; 1=minecraft:stick; RES=astrotweaks:ruby_pickaxe",
"S 00 ,01 , 1 ; 0=ore:gemRuby; 1=minecraft:stick; RES=astrotweaks:ruby_axe",
"S 0 ,1 ,1 ;    0=ore:gemRuby; 1=minecraft:stick; RES=astrotweaks:ruby_shovel",
"S 00 , 1 , 1 ; 0=ore:gemRuby; 1=minecraft:stick; RES=astrotweaks:ruby_hoe",

"S 000,0 0,   ; 0=ore:gemEmerald; RES=astrotweaks:emerald_helmet",
"S 0 0,000,000; 0=ore:gemEmerald; RES=astrotweaks:emerald_chestplate",
"S 000,0 0,0 0; 0=ore:gemEmerald; RES=astrotweaks:emerald_leggings",
"S    ,0 0,0 0; 0=ore:gemEmerald; RES=astrotweaks:emerald_boots",
"S 0 ,0 ,1 ;    0=ore:gemEmerald; 1=minecraft:stick; RES=astrotweaks:emerald_sword",
"S 000, 1 , 1 ; 0=ore:gemEmerald; 1=minecraft:stick; RES=astrotweaks:emerald_pickaxe",
"S 00 ,01 , 1 ; 0=ore:gemEmerald; 1=minecraft:stick; RES=astrotweaks:emerald_axe",
"S 0 ,1 ,1 ;    0=ore:gemEmerald; 1=minecraft:stick; RES=astrotweaks:emerald_shovel",
"S 00 , 1 , 1 ; 0=ore:gemEmerald; 1=minecraft:stick; RES=astrotweaks:emerald_hoe",

"S 000,0 0,   ; 0=ore:ingotBrass; RES=astrotweaks:brass_helmet",
"S 0 0,000,000; 0=ore:ingotBrass; RES=astrotweaks:brass_chestplate",
"S 000,0 0,0 0; 0=ore:ingotBrass; RES=astrotweaks:brass_leggings",
"S    ,0 0,0 0; 0=ore:ingotBrass; RES=astrotweaks:brass_boots",
"S 0 ,0 ,1 ;    0=ore:ingotBrass; 1=minecraft:stick; RES=astrotweaks:brass_sword",
"S 000, 1 , 1 ; 0=ore:ingotBrass; 1=minecraft:stick; RES=astrotweaks:brass_pickaxe",
"S 00 ,01 , 1 ; 0=ore:ingotBrass; 1=minecraft:stick; RES=astrotweaks:brass_axe",
"S 0 ,1 ,1 ;    0=ore:ingotBrass; 1=minecraft:stick; RES=astrotweaks:brass_shovel",
"S 00 , 1 , 1 ; 0=ore:ingotBrass; 1=minecraft:stick; RES=astrotweaks:brass_hoe",







"S 00, 1,; 0=ore:ingotIron; 1=astrotweaks:iron_stick; RES=astrotweaks:gavel",
"S 000,01,; 0=minecraft:stick; 1=astrotweaks:cordage_fiber;RES=astrotweaks:saw_frame",
"S 000,01,; 0=minecraft:stick; 1=ore:string; RES=astrotweaks:saw_frame",

"S ,011,2; 0=astrotweaks:saw_frame; 2=ore:string; 1=ore:plateIron; RES=astrotweaks:iron_saw",
"S ,011,2; 0=astrotweaks:saw_frame; 2=ore:string; 1=ore:plateCopper; RES=astrotweaks:copper_saw",
"S ,011,2; 0=astrotweaks:saw_frame; 2=ore:string; 1=ore:plateTin; RES=astrotweaks:tin_saw",
"S ,011,2; 0=astrotweaks:saw_frame; 2=ore:string; 1=ore:plateGold; RES=astrotweaks:golden_saw",
"S ,011,2; 0=astrotweaks:saw_frame; 2=ore:string; 1=ore:plateBronze; RES=astrotweaks:bronze_saw",
"S ,011,2; 0=astrotweaks:saw_frame; 2=ore:string; 1=ore:plateSteel; RES=astrotweaks:steel_saw",
"S ,011,2; 0=astrotweaks:saw_frame; 2=ore:string; 1=ore:plateDiamond; RES=astrotweaks:diamond_saw",
"S ,011,2; 0=astrotweaks:saw_frame; 2=ore:string; 1=astrotweaks:some_diamond_shards; RES=astrotweaks:diamond_saw",




// blocks <---> ingots

"# astrotweaks:mineral_steel_ingot.9; RES=astrotweaks:mineral_steel",
"# astrotweaks:mineral_steel; RES=astrotweaks:mineral_steel_ingot.9",

"# astrotweaks:brass_ingot.9; RES=astrotweaks:brass_block",
"# astrotweaks:brass_block; RES=astrotweaks:brass_ingot.9",
"# astrotweaks:brass_nugget.9; RES=astrotweaks:brass_ingot",
"# astrotweaks:brass_ingot; RES=astrotweaks:brass_nugget.9",
"# ore:dustCopper.2; ore:dustZink; RES=astrotweaks:brass_dust.3",

"# minecraft:potato.9; RES=astrotweaks:potato_block",
"# astrotweaks:potato_block; RES=minecraft:potato.9",
"# minecraft:carrot.9; RES=astrotweaks:carrot_block",
"# astrotweaks:carrot_block; RES=minecraft:carrot.9",
"# minecraft:bread.9; RES=astrotweaks:bread_block",
"# astrotweaks:bread_block; RES=minecraft:bread.9",
"# minecraft:apple.9; RES=astrotweaks:apple_block",
"# astrotweaks:apple_block; RES=minecraft:apple.9",
"# minecraft:beetroot.9; RES=astrotweaks:beetroot_block",
"# astrotweaks:beetroot_block; RES=minecraft:beetroot.9",
"# minecraft:sugar.9; RES=astrotweaks:sugar_block",
"# astrotweaks:sugar_block; RES=minecraft:sugar.9",

"# ore:gemRuby.9; RES=astrotweaks:ruby_block",
"# astrotweaks:ruby_block; RES=astrotweaks:ruby.9",


/////


"# minecraft:dye:15;RES=quark:root_dye:2",
"# minecraft:dye:0; RES=quark:root_dye:1",
"# minecraft:dye:4; RES=quark:root_dye:0",

"# astrotweaks:star_fragment.4; RES=minecraft:nether_star",
"# minecraft:milk_bucket; minecraft:glass_bottle.3; RES=astrotweaks:milk_bottle.3",
"S 010,121,010; 0=minecraft:redstone; 1=minecraft:comparator; 2=minecraft:gold_ingot; RES=astrotweaks:logic_rs_module",



"# alchemistry:ingot:14; RES=galacticraftcore:basic_item:2",
"S 000,0 0,; 0=galacticraftcore:item_basic_moon:2; RES=minecraft:sapphire_helmet",
"S 0 0,000,000; 0=galacticraftcore:item_basic_moon:2; RES=minecraft:sapphire_chestplate",
"S 000,0 0,0 0; 0=galacticraftcore:item_basic_moon:2; RES=minecraft:sapphire_leggings",
"S ,0 0,0 0; 0=galacticraftcore:item_basic_moon:2; RES=minecraft:sapphire_boots",
"S 010,121,010; 0=minecraft:glowstone_dust; 1=minecraft:magma_cream; 2=minecraft:blaze_powder; RES=thermalfoundation:material:2051",
"S 010,121,010; 0=minecraft:snow; 1=minecraft:packed_ice; 2=minecraft:blaze_powder; RES=thermalfoundation:material:2049",
"S 010,121,010; 0=minecraft:magma; 1=minecraft:magma_cream; 2=minecraft:obsidian; RES=thermalfoundation:material:2053",


"S 000,121,121; 0=minecraft:iron_block; 1=astrotweaks:iron_stick; 2=ore:plateIron; RES=astrotweaks:money_table",
"S 000,121,101; 0=minecraft:iron_block; 1=minecraft:iron_ingot; 2=minecraft:anvil; RES=astrotweaks:money_table",


// Plant dublication

"# minecraft:yellow_flower; minecraft:dye:15; RES=minecraft:yellow_flower.2",
"# minecraft:red_flower; minecraft:dye:15; RES=minecraft:red_flower.2",
"# minecraft:red_flower:1; minecraft:dye:15; RES=minecraft:red_flower:1.2",
"# minecraft:red_flower:2; minecraft:dye:15; RES=minecraft:red_flower:2.2",
"# minecraft:red_flower:3; minecraft:dye:15; RES=minecraft:red_flower:3.2",
"# minecraft:red_flower:4; minecraft:dye:15; RES=minecraft:red_flower:4.2",
"# minecraft:red_flower:5; minecraft:dye:15; RES=minecraft:red_flower:5.2",
"# minecraft:red_flower:6; minecraft:dye:15; RES=minecraft:red_flower:6.2",
"# minecraft:red_flower:7; minecraft:dye:15; RES=minecraft:red_flower:7.2",
"# minecraft:red_flower:8; minecraft:dye:15; RES=minecraft:red_flower:8.2",
"# minecraft:double_plant; minecraft:dye:15; RES=minecraft:double_plant.2",
"# minecraft:double_plant:1; minecraft:dye:15; RES=minecraft:double_plant:1.2",
"# minecraft:double_plant:2; minecraft:dye:15; RES=minecraft:double_plant:2.2",
"# minecraft:double_plant:3; minecraft:dye:15; RES=minecraft:double_plant:3.2",
"# minecraft:double_plant:4; minecraft:dye:15; RES=minecraft:double_plant:4.2",
"# minecraft:double_plant:5; minecraft:dye:15; RES=minecraft:double_plant:5.2",
"# minecraft:tallgrass:1; minecraft:dye:15; RES=minecraft:tallgrass:1.2",
"# minecraft:tallgrass:2; minecraft:dye:15; RES=minecraft:tallgrass:2.2",
"# minecraft:waterlily; minecraft:dye:15; RES=minecraft:waterlily.2",
"# minecraft:vine; minecraft:dye:15; RES=minecraft:vine.2",

"# astrotweaks:bush_1; minecraft:dye:15; RES=astrotweaks:bush_1.2",
"# astrotweaks:bush_2; minecraft:dye:15; RES=astrotweaks:bush_2.2",
"# astrotweaks:bush_3; minecraft:dye:15; RES=astrotweaks:bush_3.2",
"# astrotweaks:bush_4; minecraft:dye:15; RES=astrotweaks:bush_4.2",
"# astrotweaks:bush_5; minecraft:dye:15; RES=astrotweaks:bush_5.2",
"# astrotweaks:bush_6; minecraft:dye:15; RES=astrotweaks:bush_6.2",
"# astrotweaks:bush_7; minecraft:dye:15; RES=astrotweaks:bush_7.2",
"# astrotweaks:fern_1; minecraft:dye:15; RES=astrotweaks:fern_1.2",


// Planks from logs + saw

"# minecraft:log:0; ore:toolSaw; RES=minecraft:planks:0.4",
"# minecraft:log:1; ore:toolSaw; RES=minecraft:planks:1.4",
"# minecraft:log:2; ore:toolSaw; RES=minecraft:planks:2.4",
"# minecraft:log:3; ore:toolSaw; RES=minecraft:planks:3.4",
"# minecraft:log2:0; ore:toolSaw; RES=minecraft:planks:4.4",
"# minecraft:log2:1; ore:toolSaw; RES=minecraft:planks:5.4",
"# gregtech:rubber_log; ore:toolSaw; RES=gregtech:planks.4",


"# ore:logWood; ore:toolSaw; RES=minecraft:stick.8",
"# ore:plankWood;ore:toolSaw;RES=minecraft:stick.2",



// bundles & stacks

"# minecraft:stick.8; RES=astrotweaks:stick_bundle",
"# astrotweaks:stick_bundle; RES=minecraft:stick.8",
"# minecraft:bone.8; RES=astrotweaks:some_bones",
"# astrotweaks:some_bones; RES=minecraft:bone.8",
"# minecraft:reeds.8; RES=astrotweaks:some_sugar_canes",
"# astrotweaks:some_sugar_canes; RES=minecraft:reeds.8",
"# minecraft:golden_carrot.8; RES=astrotweaks:some_golden_carrots",
"# astrotweaks:some_golden_carrots; RES=minecraft:golden_carrot.8",
"# minecraft:blaze_rod.8; RES=astrotweaks:some_blaze_rods",
"# astrotweaks:some_blaze_rods; RES=minecraft:blaze_rod.8",
"# minecraft:rotten_flesh.8; RES=astrotweaks:some_rotten_flesh",
"# astrotweaks:some_rotten_flesh; RES=minecraft:rotten_flesh.8",
"# minecraft:ender_pearl.8; RES=astrotweaks:some_ender_pearls",
"# astrotweaks:some_ender_pearls; RES=minecraft:ender_pearl.8",
"# minecraft:ender_eye.8; RES=astrotweaks:some_ender_eyes",
"# astrotweaks:some_ender_eyes; RES=minecraft:ender_eye.8",
"# astrotweaks:diamond_shard.8; RES=astrotweaks:some_diamond_shards",
"# astrotweaks:some_diamond_shards; RES=astrotweaks:diamond_shard.8",
"# minecraft:feather.8; RES=astrotweaks:some_feathers",
"# astrotweaks:some_feathers; RES=minecraft:feather.8",
"# minecraft:leather.8; RES=astrotweaks:some_leather",
"# astrotweaks:some_leather; RES=minecraft:leather.8",
"# minecraft:paper.8; RES=astrotweaks:some_paper",
"# astrotweaks:some_paper; RES=minecraft:paper.8",
"# minecraft:book.8; RES=astrotweaks:some_books",
"# astrotweaks:some_books; RES=minecraft:book.8",
"# minecraft:gunpowder.8; RES=astrotweaks:some_gunpowder",
"# astrotweaks:some_gunpowder; RES=minecraft:gunpowder.8",
"# minecraft:magma_cream.8; RES=astrotweaks:some_magma_cream",
"# astrotweaks:some_magma_cream; RES=minecraft:magma_cream.8",
"# minecraft:golden_apple.8; RES=astrotweaks:some_golden_apples",
"# astrotweaks:some_golden_apples; RES=minecraft:golden_apple.8",
"# minecraft:arrow.8; RES=astrotweaks:some_arrows",
"# astrotweaks:some_arrows; RES=minecraft:arrow.8",
"# minecraft:string.8; RES=astrotweaks:some_strings",
"# astrotweaks:some_strings; RES=minecraft:string.8",
"# minecraft:fire_charge.8; RES=astrotweaks:some_fireballs",
"# astrotweaks:some_fireballs; RES=minecraft:fire_charge.8",




// Rods (sticks) & Xs

"S 0,0,; 0=ore:gemDiamond; RES=astrotweaks:diamond_stick",
"S 0,0,; 0=ore:gemEmerald; RES=astrotweaks:emerald_stick",
"S 0,0,; 0=ore:gemRuby; RES=astrotweaks:ruby_stick",
"S 0,0,; 0=ore:ingotGold; RES=astrotweaks:gold_stick",
"S 0,0,; 0=ore:ingotBrass; RES=astrotweaks:brass_stick",
"S 0,0,; 0=ore:ingotIron; RES=astrotweaks:iron_stick",
"S 0,0,; 0=ore:ingotCopper; RES=astrotweaks:copper_stick",
"S 0,0,; 0=ore:ingotBronze; RES=astrotweaks:bronze_stick",
"S 0,0,; 0=ore:ingotTin; RES=astrotweaks:tin_stick",
"S 0,0,; 0=ore:ingotAluminium; RES=astrotweaks:aluminium_stick",
"S 0,0,; 0=ore:ingotAluminum; RES=astrotweaks:aluminium_stick",
"S 0,0,; 0=ore:ingotTitanium; RES=astrotweaks:titanium_stick",
"S 0,0,; 0=ore:ingotNickel; RES=astrotweaks:nickel_stick",
"S 0,0,; 0=ore:ingotCobalt; RES=astrotweaks:cobalt_stick",
"S 0,0,; 0=ore:ingotElectrum; RES=astrotweaks:electrum_stick",
"S 0,0,; 0=ore:ingotSteel; RES=astrotweaks:steel_stick",
"S 0,0,; 0=ore:ingotIridium; RES=astrotweaks:iridium_stick",
"S 0,0,; 0=ore:ingotSilver; RES=astrotweaks:silver_stick",
"S 0,0,; 0=ore:ingotMeteoricIron; RES=astrotweaks:meteoric_stick",


"S 0,0,; 0=ore:rodDiamond; RES=astrotweaks:diamond_x",
"S 0,0,; 0=ore:rodEmerald; RES=astrotweaks:emerald_x",
"S 0,0,; 0=ore:rodRuby; RES=astrotweaks:ruby_x",
"S 0,0,; 0=ore:rodGold; RES=astrotweaks:gold_x",
"S 0,0,; 0=ore:rodBrass; RES=astrotweaks:brass_x",
"S 0,0,; 0=ore:rodIron; RES=astrotweaks:iron_x",
"S 0,0,; 0=ore:rodCopper; RES=astrotweaks:copper_x",
"S 0,0,; 0=ore:rodBronze; RES=astrotweaks:bronze_x",
"S 0,0,; 0=ore:rodTin; RES=astrotweaks:tin_x",
"S 0,0,; 0=ore:rodAluminium; RES=astrotweaks:aluminium_x",
"S 0,0,; 0=ore:rodAluminum; RES=astrotweaks:aluminium_x",
"S 0,0,; 0=ore:rodTitanium; RES=astrotweaks:titanium_x",
"S 0,0,; 0=ore:rodNickel; RES=astrotweaks:nickel_x",
"S 0,0,; 0=ore:rodCobalt; RES=astrotweaks:cobalt_x",
"S 0,0,; 0=ore:rodElectrum; RES=astrotweaks:electrum_x",
"S 0,0,; 0=ore:rodSteel; RES=astrotweaks:steel_x",
"S 0,0,; 0=ore:rodIridium; RES=astrotweaks:iridium_x",
"S 0,0,; 0=ore:rodSilver; RES=astrotweaks:silver_x",
"S 0,0,; 0=ore:rodMeteoricIron; RES=astrotweaks:meteoric_iron_x",
"S 0,0,; 0=ore:rodCarbon; RES=astrotweaks:carbon_x",





//"# minecraft:coal, minecraft:stick; RES=minecraft:torch.4",
//"S AAA,   ,   ; A=ore:logWood; RES=minecraft:planks:0.4",
"S 000,010,000; 0=minecraft:iron_nugget; 1=astrotweaks:strange_quant;   RES=astrotweaks:quantum_bagde"


)));
////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final List<IRecipe> RECIPES_TO_REGISTER = new ArrayList<>();

    public static void loadRecipes() {
        for (String recipeLine : RECIPES) {
            String trimmed = recipeLine.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("//")) continue;

            char firstChar = trimmed.charAt(0);
            if (firstChar == '#') {
                parseShapeless(trimmed.substring(1).trim());
            } else if (firstChar == 'S' || firstChar == 's') {
                parseShaped(trimmed.substring(1).trim());
            } else {
                LOGGER.warn("Unknown recipe type: {}", trimmed);
            }
        }
    }

    // ---------- Shaped ----------
    private static void parseShaped(String data) {
        String[] parts = data.split(";", -1);
        if (parts.length < 3) {
            LOGGER.warn("Shaped recipe '{}' does not contain enough parts (it needs Pattern, Keys, and Result)", data);
            return;
        }

		// pattern
		String patternStr = parts[0]; // removed trim()
		String[] patternRows = patternStr.split(",", -1);
		List<String> rows = new ArrayList<>();
		int maxLen = 0;
		for (String row : patternRows) {
		    // DO NOT trim row here - leading/trailing spaces are significant
		    rows.add(row);
		    if (row.length() > maxLen) maxLen = row.length();
		}
		// pad right (same)
		for (int i = 0; i < rows.size(); i++) {
		    String r = rows.get(i);
		    if (r.length() < maxLen) {
		        StringBuilder sb = new StringBuilder(r);
		        for (int k = 0; k < maxLen - r.length(); k++) sb.append(' ');
		        rows.set(i, sb.toString());
		    }
		}
		// trim empty rows top/bottom
		int top = 0;
		while (top < rows.size() && rows.get(top).replace("\u00A0", " ").trim().isEmpty()) top++; // optional: normalize NBSP
		int bottom = rows.size() - 1;
		while (bottom >= top && rows.get(bottom).replace("\u00A0", " ").trim().isEmpty()) bottom--;
		if (top > bottom) {
		    LOGGER.warn("Pattern is empty after trimming: {}", data);
		    return;
		}
		List<String> trimmedRows = rows.subList(top, bottom + 1);
		
		// find leftmost and rightmost non-space column
		int left = Integer.MAX_VALUE;
		int right = Integer.MIN_VALUE;
		for (String r : trimmedRows) {
		    for (int i = 0; i < r.length(); i++) {
		        if (r.charAt(i) != ' ') {
		            left = Math.min(left, i);
		            right = Math.max(right, i);
		        }
		    }
		}
		if (right < left) {
		    LOGGER.warn("Pattern has no non-space chars: {}", data);
		    return;
		}
		// crop columns and build final pattern
		String[] pattern = new String[trimmedRows.size()];
		for (int i = 0; i < trimmedRows.size(); i++) {
		    pattern[i] = trimmedRows.get(i).substring(left, right + 1);
		}


		





	    // ---------- Keys ----------
	    Map<Character, Object> keys = new HashMap<>();
	    int i = 1;
	    for (; i < parts.length - 1; i++) {
	        String keyPart = parts[i].trim();
	        if (keyPart.isEmpty()) continue;
	        String[] kv = keyPart.split("=", 2);
	        if (kv.length != 2) {
	            LOGGER.warn("Incorrect key definition in '{}'", keyPart);
	            return;
	        }
	        char keyChar = kv[0].charAt(0);
	        String valueStr = kv[1].trim();
	        Object ingredient = parseShapedIngredient(valueStr);
	        if (ingredient == null) return;
	        keys.put(keyChar, ingredient);
	    }

	    // ---------- Result ----------
	    if (i >= parts.length) {
	        LOGGER.warn("The result is missing in the shaped recipe: {}", data);
	        return;
	    }
	    String resultPart = parts[i].trim();
	    if (!resultPart.startsWith("RES=")) {
	        LOGGER.warn("Result should start with 'RES=' in '{}'", resultPart);
	        return;
	    }
	    String resultStr = resultPart.substring(4).trim();
	    ItemStack result = parseResult(resultStr);
	    if (result == null || result.isEmpty()) return;
	
	    // check that all the symbols of the pattern are defined in the keys
	    Set<Character> patternChars = new HashSet<>();
	    for (String row : pattern) {
	        for (char c : row.toCharArray()) {
	            if (c != ' ') patternChars.add(c);
	        }
	    }
	    for (char c : patternChars) {
	        if (!keys.containsKey(c)) {
	            LOGGER.warn("The pattern symbol '{}' is not defined in the recipe keys: {}", c, data);
	            return;
	        }
	    }

	    List<Object> recipeObjects = new ArrayList<>();
	    recipeObjects.add(pattern);
	    for (Map.Entry<Character, Object> entry : keys.entrySet()) {
	        recipeObjects.add(entry.getKey());
	        recipeObjects.add(entry.getValue());
	    }
	    try {
	        ResourceLocation id = new ResourceLocation("astrotweaks", "shaped_" + Math.abs(data.hashCode()));
	        IRecipe recipe = new ShapedOreRecipe(id, result, recipeObjects.toArray());
	        RECIPES_TO_REGISTER.add(recipe);
	    } catch (Exception e) {
	        LOGGER.warn("Couldn't add shaped recipe {}: {}", data, e.getMessage());
	    }
    }

    private static Object parseShapedIngredient(String value) {
        value = value.trim();
        if (value.startsWith("ore:")) {
            String oreName = value.substring(4);
            int dotIdx = oreName.lastIndexOf('.');
            if (dotIdx >= 0) {
                LOGGER.warn("Count in the shaped ingredient is ignored: '{}'", value);
                oreName = oreName.substring(0, dotIdx);
            }
            //if (!OreDictionary.doesOreNameExist(oreName) || OreDictionary.getOres(oreName).isEmpty()) {
            //    LOGGER.info("Recipe skipped: OreDictionary '{}' is empty or doesn't exist", oreName);
            //    return null;
            //}
            return oreName;
        } else {
            // Parse format: mod:item || mod:item:meta
            String[] parts = value.split(":");
            if (parts.length < 2) {
                LOGGER.warn("Invalid ingredient format: {}", value);
                return null;
            }
            String itemName = parts[0] + ":" + parts[1];
            int meta = 0;

            // meta (3rd)
            if (parts.length >= 3) {
                String third = parts[2];
                int dotIdx = third.indexOf('.');
                if (dotIdx >= 0) {
                    String metaStr = third.substring(0, dotIdx);
                    if (!metaStr.isEmpty()) {
                        if (metaStr.equals("*")) meta = OreDictionary.WILDCARD_VALUE;
                        else {
                            try { meta = Integer.parseInt(metaStr); }
                            catch (NumberFormatException e) {
                                LOGGER.warn("Incorrect meta in '{}'", value);
                                return null;
                            }
                        }
                    }
                    // count  ingored
                    LOGGER.warn("Count in shaped ingredient is ignored: '{}'", value);
                } else {
                    if (third.equals("*")) meta = OreDictionary.WILDCARD_VALUE;
                    else {
                        try { meta = Integer.parseInt(third); }
                        catch (NumberFormatException e) {
                            LOGGER.warn("Incorrect meta in '{}'", value);
                            return null;
                        }
                    }
                }
            }
            // If 3rd part not exist, but has comma in 2nd (example, minecraft:torch.4)
            else if (parts[1].contains(".")) {
                String[] sub = parts[1].split("\\.", 2);
                itemName = parts[0] + ":" + sub[0];
                // meta is 0, count ingored
                LOGGER.warn("Count in shaped ingredient is ignored: '{}'", value);
            }

            Item item = Item.getByNameOrId(itemName);
            if (item == null) {
                LOGGER.info("Recipe skipped: item '{}' was not found", itemName);
                return null;
            }
            return new ItemStack(item, 1, meta);
        }
    }

    // ---------- Shapeless ----------
	private static void parseShapeless(String data) {
	    String[] parts = data.split(";");
	    if (parts.length < 2) {
	        LOGGER.warn("Shapeless recipe must contain ingredients and result separated by ';': {}", data);
	        return;
	    }

	    // last pars = Result
	    String resultPart = parts[parts.length - 1].trim();
	    if (!resultPart.startsWith("RES=")) {
	        LOGGER.warn("The Result must start with 'RES=' in '{}'", resultPart);
	        return;
	    }
	    String resultStr = resultPart.substring(4).trim();
	    ItemStack result = parseResult(resultStr);
	    if (result == null || result.isEmpty()) return;
	
	    // all else is Ingredients
	    List<Object> inputs = new ArrayList<>();
	    for (int i = 0; i < parts.length - 1; i++) {
	        String token = parts[i].trim();
	        if (token.isEmpty()) continue;
	
	        if (token.startsWith("ore:")) {
	            // ore:tag.count
	            String oreName = token.substring(4);
	            int count = 1;
	            int dotIdx = oreName.lastIndexOf('.');
	            if (dotIdx >= 0) {
	                try {
	                    count = Integer.parseInt(oreName.substring(dotIdx + 1));
	                    oreName = oreName.substring(0, dotIdx);
	                } catch (NumberFormatException e) {
	                    LOGGER.warn("Incorrect amount in ore ingredient: {}", token);
	                    return;
	                }
	            }
	            //if (!OreDictionary.doesOreNameExist(oreName) || OreDictionary.getOres(oreName).isEmpty()) {
	            //    LOGGER.info("Recipe was skipped: OreDictionary '{}' is empty", oreName);
	            //    return;
	            //}
	            for (int j = 0; j < count; j++) inputs.add(oreName);
	        } else {
	            // Common item: mod:item || mod:item:meta.count || mod:item.count
	            String[] itemParts = token.split(":");
	            if (itemParts.length < 2) {
	                LOGGER.warn("Invalid ingredient format: {}", token);
	                return;
	            }
	            String itemName = itemParts[0] + ":" + itemParts[1];
	            int meta = 0;
	            int count = 1;
	
	            // meta or count
	            if (itemParts.length >= 3) {
	                String third = itemParts[2];
	                int dotIdx = third.indexOf('.');
	                if (dotIdx >= 0) {
	                    // meta.count
	                    String metaStr = third.substring(0, dotIdx);
	                    String countStr = third.substring(dotIdx + 1);
	                    if (!metaStr.isEmpty()) {
	                        if (metaStr.equals("*")) meta = OreDictionary.WILDCARD_VALUE;
	                        else {
	                            try { meta = Integer.parseInt(metaStr); }
	                            catch (NumberFormatException e) {
	                                LOGGER.warn("Incorrect Meta in '{}'", token);
	                                return;
	                            }
	                        }
	                    }
	                    try { count = Integer.parseInt(countStr); }
	                    catch (NumberFormatException e) {
	                        LOGGER.warn("Incorrect Count in '{}'", token);
	                        return;
	                    }
	                } else {
	                    // meta
	                    if (third.equals("*")) meta = OreDictionary.WILDCARD_VALUE;
	                    else {
	                        try { meta = Integer.parseInt(third); }
	                        catch (NumberFormatException e) {
	                            LOGGER.warn("Incorrect Meta in '{}'", token);
	                            return;
	                        }
	                    }
	                }
	            }
	            // If there is no third part, but there is a point in the second (mod:item.count)
	            else if (itemParts[1].contains(".")) {
	                String[] sub = itemParts[1].split("\\.", 2);
	                itemName = itemParts[0] + ":" + sub[0];
	                try { count = Integer.parseInt(sub[1]); }
	                catch (NumberFormatException e) {
	                    LOGGER.warn("Incorrect Count in '{}'", token);
	                    return;
	                }
	            }
	
	            Item item = Item.getByNameOrId(itemName);
	            if (item == null) {
	                LOGGER.info("Recipe was skipped: Item '{}' is empty", itemName);
	                return;
	            }
	            ItemStack stack = new ItemStack(item, 1, meta);
	            for (int j = 0; j < count; j++) inputs.add(stack.copy());
	        }
	    }
	    if (inputs.isEmpty()) {
	        LOGGER.warn("There are no valid ingredients in the shapeless recipe: {}", data);
	        return;
	    }
	    try {
	        ResourceLocation id = new ResourceLocation("astrotweaks","shapeless_"+Math.abs(data.hashCode()));
	        IRecipe recipe = new ShapelessOreRecipe(id, result, inputs.toArray());
	        RECIPES_TO_REGISTER.add(recipe);
	    } catch (Exception e) {
	        LOGGER.warn("Couldn't add shapeless recipe {}: {}", data, e.getMessage());
	    }
	}

    // ---------- General result parser ----------
    private static ItemStack parseResult(String value) {
        value = value.trim();
        if (value.startsWith("ore:")) {
            String oreName = value.substring(4);
            int count = 1;
            int dotIdx = oreName.lastIndexOf('.');
            if (dotIdx >= 0) {
                try {
                    count = Integer.parseInt(oreName.substring(dotIdx + 1));
                    oreName = oreName.substring(0, dotIdx);
                } catch (NumberFormatException e) {
                    LOGGER.warn("Incorrect Count in Ore: {}", value);
                    return ItemStack.EMPTY;
                }
            }
            List<ItemStack> ores = OreDictionary.getOres(oreName);
            if (ores.isEmpty()) {
                LOGGER.info("Recipe skipped: OreDictionary '{}' is empty", oreName);
                return ItemStack.EMPTY;
            }
            ItemStack result = ores.get(0).copy();
            result.setCount(count);
            return result;
        } else {
            String[] parts = value.split(":");
            if (parts.length < 2) {
                LOGGER.warn("Invalid result format: {}", value);
                return ItemStack.EMPTY;
            }
            String itemName = parts[0] + ":" + parts[1];
            int meta = 0;
            int count = 1;

            // 3rd part (mera or count)
            if (parts.length >= 3) {
                String third = parts[2];
                int dotIdx = third.indexOf('.');
                if (dotIdx >= 0) {
                    // meta.count
                    String metaStr = third.substring(0, dotIdx);
                    String countStr = third.substring(dotIdx + 1);
                    if (!metaStr.isEmpty()) {
                        if (metaStr.equals("*")) meta = OreDictionary.WILDCARD_VALUE;
                        else {
                            try { meta = Integer.parseInt(metaStr); }
                            catch (NumberFormatException e) {
                                LOGGER.warn("Invalid meta in result '{}'", value);
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                    try { count = Integer.parseInt(countStr); }
                    catch (NumberFormatException e) {
                        LOGGER.warn("Invalid quantity as a result '{}'", value);
                        return ItemStack.EMPTY;
                    }
                } else {
                    // meta only
                    if (third.equals("*")) meta = OreDictionary.WILDCARD_VALUE;
                    else {
                        try { meta = Integer.parseInt(third); }
                        catch (NumberFormatException e) {
                            LOGGER.warn("Invalid meta in result '{}'", value);
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }
            // If there is no third part, but there is a point in the second (mod:item.count)
            else if (parts[1].contains(".")) {
                String[] sub = parts[1].split("\\.", 2);
                itemName = parts[0] + ":" + sub[0];
                try { count = Integer.parseInt(sub[1]); }
                catch (NumberFormatException e) {
                    LOGGER.warn("Invalid quantity as a result '{}'", value);
                    return ItemStack.EMPTY;
                }
            }

            Item item = Item.getByNameOrId(itemName);
            if (item == null) {
                LOGGER.info("Recipe skipped: result item '{}' not found", itemName);
                return ItemStack.EMPTY;
            }
            return new ItemStack(item, count, meta);
        }
    }
}