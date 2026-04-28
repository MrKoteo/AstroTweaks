package astrotweaks.oredict;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraft.item.ItemStack;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

import astrotweaks.item.*;
import astrotweaks.block.*;


import astrotweaks.ElementsAstrotweaksMod;
import net.minecraftforge.oredict.OreIngredient;


public class UOredictRegistrar {
	public UOredictRegistrar() {
	}

	//@SubscribeEvent
	public static void init(FMLInitializationEvent event) {
		new UOredictRegistrar();

		OreItems();
		OreBlocks();
		OreGems();
		OreDusts();
		OrePlates();
		OreNuggets();
		OreIngots();
		OreOres();
		OreRods();
	}

	@SubscribeEvent
	public void preInit(FMLPreInitializationEvent event) {
	}



	private static void OreItems() {
		OreDictionary.registerOre("rock", new ItemStack(ItemRock.block, (int) (1)));
		OreDictionary.registerOre("rock", new ItemStack(ItemRockFlat.block, (int) (1)));
		OreDictionary.registerOre("rockFlat", new ItemStack(ItemRockFlat.block, (int) (1)));
		OreDictionary.registerOre("brickStone", new ItemStack(ItemStoneBrick.block, (int) (1)));
		OreDictionary.registerOre("ingotStone", new ItemStack(ItemStoneBrick.block, (int) (1)));
		OreDictionary.registerOre("ingotBrickStone", new ItemStack(ItemStoneBrick.block, (int) (1)));
		OreDictionary.registerOre("brickClay", new ItemStack(ItemClayBrick.block, (int) (1)));
		OreDictionary.registerOre("ingotClay", new ItemStack(ItemClayBrick.block, (int) (1)));
		OreDictionary.registerOre("ingotBrickClay", new ItemStack(ItemClayBrick.block, (int) (1)));
		OreDictionary.registerOre("discSilicon", new ItemStack(ItemSiliconDisc.block, (int) (1)));
		OreDictionary.registerOre("twine", new ItemStack(ItemCordageFiber.block, (int) (1)));
		
		OreDictionary.registerOre("singularity", new ItemStack(ItemNeutroniumsingularity.block, (int) (1)));
		OreDictionary.registerOre("singularityNeutronium", new ItemStack(ItemNeutroniumsingularity.block, (int) (1)));
		
		OreDictionary.registerOre("singularity", new ItemStack(ItemInfinitySingularity.block, (int) (1)));
		OreDictionary.registerOre("singularityInfinity", new ItemStack(ItemInfinitySingularity.block, (int) (1)));

		OreDictionary.registerOre("shardFlint", new ItemStack(ItemFlintShard.block, (int) (1)));
		OreDictionary.registerOre("shard", new ItemStack(ItemFlintShard.block, (int) (1)));
		OreDictionary.registerOre("shardBone", new ItemStack(ItemBoneShard.block, (int) (1)));
		OreDictionary.registerOre("shard", new ItemStack(ItemBoneShard.block, (int) (1)));
		OreDictionary.registerOre("shardDiamond", new ItemStack(ItemDiamondShard.block, (int) (1)));
		OreDictionary.registerOre("shard", new ItemStack(ItemDiamondShard.block, (int) (1)));

		OreDictionary.registerOre("toolSaw", new ItemStack(ItemSawIron.block, (int) (1)));
		OreDictionary.registerOre("toolSaw", new ItemStack(ItemSawDiamond.block, (int) (1)));
		OreDictionary.registerOre("toolSaw", new ItemStack(ItemGoldenSaw.block, (int) (1)));
		OreDictionary.registerOre("toolSaw", new ItemStack(ItemCopperSaw.block, (int) (1)));
		OreDictionary.registerOre("toolSaw", new ItemStack(ItemBronzeSaw.block, (int) (1)));
		OreDictionary.registerOre("toolSaw", new ItemStack(ItemTinSaw.block, (int) (1)));
		OreDictionary.registerOre("toolSaw", new ItemStack(ItemSteelSaw.block, (int) (1)));

		OreDictionary.registerOre("wireGold", new ItemStack(ItemInductanceCoil1.block, (int) (1)));
		OreDictionary.registerOre("wireCopper", new ItemStack(ItemInductanceCoil2.block, (int) (1)));
	}
	private static void OreBlocks() {
		OreDictionary.registerOre("blockRuby", new ItemStack(BlockRubyBlock.block, (int) (1)));
		OreDictionary.registerOre("blockBrass", new ItemStack(BlockBrassBlock.block, (int) (1)));

		OreDictionary.registerOre("stone", new ItemStack(BlockDeepslate.block, (int) (1)));
		OreDictionary.registerOre("cobblestone", new ItemStack(BlockCobbledDeepslate.block, (int) (1)));
		OreDictionary.registerOre("dirt", new ItemStack(BlockDirtBricks.block, (int) (1)));
		OreDictionary.registerOre("blockNetherStar", new ItemStack(BlockNetherstarBlock.block, (int) (1)));

		OreDictionary.registerOre("hardenedClay", new ItemStack(Blocks.HARDENED_CLAY, (int) (1)));
		OreDictionary.registerOre("hardenedClay", new ItemStack(Blocks.STAINED_HARDENED_CLAY, (int) (1)));
		OreDictionary.registerOre("hardenedClay", new ItemStack(Blocks.STAINED_HARDENED_CLAY, (int) (1)));
		OreDictionary.registerOre("blockConcrete", new ItemStack(Blocks.CONCRETE, (int) (1)));
		OreDictionary.registerOre("blockConcretePowder", new ItemStack(Blocks.CONCRETE_POWDER, (int) (1)));
		OreDictionary.registerOre("blockGlazedTerracota", new ItemStack(Blocks.WHITE_GLAZED_TERRACOTTA, (int) (1)));
		OreDictionary.registerOre("blockGlazedTerracota", new ItemStack(Blocks.ORANGE_GLAZED_TERRACOTTA, (int) (1)));
		OreDictionary.registerOre("blockGlazedTerracota", new ItemStack(Blocks.MAGENTA_GLAZED_TERRACOTTA, (int) (1)));
		OreDictionary.registerOre("blockGlazedTerracota", new ItemStack(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, (int) (1)));
		OreDictionary.registerOre("blockGlazedTerracota", new ItemStack(Blocks.YELLOW_GLAZED_TERRACOTTA, (int) (1)));
		OreDictionary.registerOre("blockGlazedTerracota", new ItemStack(Blocks.LIME_GLAZED_TERRACOTTA, (int) (1)));
		OreDictionary.registerOre("blockGlazedTerracota", new ItemStack(Blocks.PINK_GLAZED_TERRACOTTA, (int) (1)));
		OreDictionary.registerOre("blockGlazedTerracota", new ItemStack(Blocks.GRAY_GLAZED_TERRACOTTA, (int) (1)));
		OreDictionary.registerOre("blockGlazedTerracota", new ItemStack(Blocks.SILVER_GLAZED_TERRACOTTA, (int) (1)));
		OreDictionary.registerOre("blockGlazedTerracota", new ItemStack(Blocks.CYAN_GLAZED_TERRACOTTA, (int) (1)));
		OreDictionary.registerOre("blockGlazedTerracota", new ItemStack(Blocks.PURPLE_GLAZED_TERRACOTTA, (int) (1)));
		OreDictionary.registerOre("blockGlazedTerracota", new ItemStack(Blocks.BLUE_GLAZED_TERRACOTTA, (int) (1)));
		OreDictionary.registerOre("blockGlazedTerracota", new ItemStack(Blocks.BROWN_GLAZED_TERRACOTTA, (int) (1)));
		OreDictionary.registerOre("blockGlazedTerracota", new ItemStack(Blocks.GREEN_GLAZED_TERRACOTTA, (int) (1)));
		OreDictionary.registerOre("blockGlazedTerracota", new ItemStack(Blocks.RED_GLAZED_TERRACOTTA, (int) (1)));
		OreDictionary.registerOre("blockGlazedTerracota", new ItemStack(Blocks.BLACK_GLAZED_TERRACOTTA, (int) (1)));
		OreDictionary.registerOre("blockMossy", new ItemStack(Blocks.MOSSY_COBBLESTONE, (int) (1)));

		OreDictionary.registerOre("blockDirt", new ItemStack(Blocks.DIRT, 1, 1));

		OreDictionary.registerOre("blockGrass", new ItemStack(Blocks.DIRT, 1, 2));


		//OreDictionary.registerOre("cobblestoneSlab", new ItemStack(BlockCobbledDeepslateSlab.block, (int) (1)));
	}
	private static void OreGems() {
		OreDictionary.registerOre("gemRuby", new ItemStack(ItemRuby.block, (int) (1)));
	}
	private static void OreDusts() {
		OreDictionary.registerOre("dustRuby", new ItemStack(ItemRubyDust.block, (int) (1)));
		OreDictionary.registerOre("dustCement", new ItemStack(ItemCementDust.block, (int) (1)));
		//OreDictionary.registerOre("dustEmerald", new ItemStack(ItemEmeraldDust.block, (int) (1)));
		OreDictionary.registerOre("dustBrass", new ItemStack(ItemBrassDust.block, (int) (1)));
	}
	private static void OrePlates() {
		OreDictionary.registerOre("plateCopper", new ItemStack(ItemCopperPlate.block, (int) (1)));
	}
	private static void OreNuggets() {
		OreDictionary.registerOre("nuggetBrass", new ItemStack(ItemBrassNugget.block, (int) (1)));
	}
	private static void OreIngots() {
		OreDictionary.registerOre("ingotBrass", new ItemStack(ItemBrassIngot.block, (int) (1)));
	}
	private static void OreOres() {
		OreDictionary.registerOre("oreRuby", new ItemStack(BlockRubyOre.block, (int) (1)));
		OreDictionary.registerOre("oreQuartz", new ItemStack(BlockQuartzOreStone.block, (int) (1)));
		OreDictionary.registerOre("oreQuartz", new ItemStack(BlockQuartzOreGranite.block, (int) (1)));
	}
	///
	private static void OreRods() {
		OreDictionary.registerOre("rodIron", new ItemStack(ItemIronStick.block, (int) (1)));
		OreDictionary.registerOre("rodGold", new ItemStack(ItemGoldStick.block, (int) (1)));
		OreDictionary.registerOre("rodCopper", new ItemStack(ItemCopperStick.block, (int) (1)));
		OreDictionary.registerOre("rodTin", new ItemStack(ItemTinStick.block, (int) (1)));
		OreDictionary.registerOre("rodBronze", new ItemStack(ItemBronzeStick.block, (int) (1)));
		OreDictionary.registerOre("rodDiamond", new ItemStack(ItemDiamondStick.block, (int) (1)));
	    OreDictionary.registerOre("rodAluminium", new ItemStack(ItemAluminiumStick.block, (int) (1)));
	    OreDictionary.registerOre("rodTitanium", new ItemStack(ItemTitaniumStick.block, (int) (1)));
	    OreDictionary.registerOre("rodNickel", new ItemStack(ItemNickelStick.block, (int) (1)));
	    OreDictionary.registerOre("rodCobalt", new ItemStack(ItemCobaltStick.block, (int) (1)));
	    OreDictionary.registerOre("rodMeteoricIron", new ItemStack(ItemMeteoricStick.block, (int) (1)));
	    OreDictionary.registerOre("rodElectrum", new ItemStack(ItemElectrumStick.block, (int) (1)));
	    OreDictionary.registerOre("rodEmerald", new ItemStack(ItemEmeraldStick.block, (int) (1)));
	    OreDictionary.registerOre("rodRuby", new ItemStack(ItemRubyStick.block, (int) (1)));
	    OreDictionary.registerOre("rodSteel", new ItemStack(ItemSteelStick.block, (int) (1)));
	    OreDictionary.registerOre("rodIridium", new ItemStack(ItemIridiumStick.block, (int) (1)));
	    OreDictionary.registerOre("rodSilver", new ItemStack(ItemSilverStick.block, (int) (1)));
	    OreDictionary.registerOre("rodUranium", new ItemStack(ItemUraniumStick.block, (int) (1)));
	    OreDictionary.registerOre("rodBrass", new ItemStack(ItemBrassStick.block, (int) (1)));
		OreDictionary.registerOre("rodCarbon", new ItemStack(ItemCarbonStick.block, (int) (1)));
		
	}


}
