package astrotweaks.recipe;

import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import astrotweaks.item.*;
import astrotweaks.block.*;

import astrotweaks.AstrotweaksModVariables;
import astrotweaks.ModVariables;
import astrotweaks.ElementsAstrotweaksMod;
import net.minecraft.item.ItemGlassBottle;

@ElementsAstrotweaksMod.ModElement.Tag
public class RecipeSmeltingAll extends ElementsAstrotweaksMod.ModElement {
	public RecipeSmeltingAll(ElementsAstrotweaksMod instance) {
		super(instance, 366);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		GameRegistry.addSmelting(new ItemStack(BlockRubyOre.block, (int) (1)), new ItemStack(ItemRuby.block, (int) (1)), 1F);
		GameRegistry.addSmelting(new ItemStack(ItemCordageVine.block, (int) (1)), new ItemStack(ItemCordageFiber.block, (int) (1)), 0.1F);
		GameRegistry.addSmelting(new ItemStack(BlockCompressedSand.block, (int) (1)), new ItemStack(Blocks.QUARTZ_BLOCK, (int) (1), 0), 0.5F);

		GameRegistry.addSmelting(new ItemStack(Items.FISH, (int) (1), 2), new ItemStack(ItemCoockedTropicalFish.block, (int) (1)), 0.6F);
		GameRegistry.addSmelting(new ItemStack(Items.FISH, (int) (1), 3), new ItemStack(ItemCoockedPufferfish.block, (int) (1)), 1.5F);

		GameRegistry.addSmelting(new ItemStack(BlockQuartzOreStone.block, (int) (1)), new ItemStack(Items.QUARTZ, (int) (1)), 1F);
		GameRegistry.addSmelting(new ItemStack(BlockQuartzOreGranite.block, (int) (1)), new ItemStack(Items.QUARTZ, (int) (1)), 1F);
		
		GameRegistry.addSmelting(new ItemStack(ItemBrassDust.block, (int) (1)), new ItemStack(ItemBrassIngot.block, (int) (1)), 0F);

		GameRegistry.addSmelting(new ItemStack(ItemClayBrick.block, (int) (1)), new ItemStack(Items.BRICK, (int) (1)), 0.5F);
		GameRegistry.addSmelting(new ItemStack(BlockCobbledDeepslate.block, (int) (1)), new ItemStack(BlockDeepslate.block, (int) (1)), 0.5F);
		GameRegistry.addSmelting(new ItemStack(BlockDeepslate.block, (int) (1)), new ItemStack(BlockDeepslateTiles.block, (int) (1)), 0.5F);


		GameRegistry.addSmelting(new ItemStack(ItemSawIron.block, (int) (1)), new ItemStack(Items.IRON_NUGGET, (int) (1)), 0.1F);
		GameRegistry.addSmelting(new ItemStack(ItemGoldenSaw.block, (int) (1)), new ItemStack(Items.GOLD_NUGGET, (int) (1)), 0.1F);

		if (ModVariables.Better_Smelting) {
			GameRegistry.addSmelting(new ItemStack(Items.SHEARS, (int) (1)), new ItemStack(Items.IRON_NUGGET, (int) (1)), 0.1F);
			GameRegistry.addSmelting(new ItemStack(Items.BUCKET, (int) (1)), new ItemStack(Items.IRON_NUGGET, (int) (1)), 0.1F);
			GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE), (int) (1)), new ItemStack(Items.IRON_NUGGET, (int) (1)), 0.1F);
			GameRegistry.addSmelting(new ItemStack(Items.COMPASS, (int) (1)), new ItemStack(Items.IRON_NUGGET, (int) (1)), 0.1F);
			GameRegistry.addSmelting(new ItemStack(Items.IRON_HORSE_ARMOR, (int) (1)), new ItemStack(Items.IRON_NUGGET, (int) (1)), 0.1F);
			GameRegistry.addSmelting(new ItemStack(Items.MINECART, (int) (1)), new ItemStack(Items.IRON_INGOT, (int) (1)), 0.1F);
			GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(Blocks.IRON_BARS), (int) (1)), new ItemStack(Items.IRON_NUGGET, (int) (1)), 0.1F);
			GameRegistry.addSmelting(new ItemStack(Items.CAULDRON, (int) (1)), new ItemStack(Items.IRON_NUGGET, (int) (3)), 0.1F);
			GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(Blocks.HOPPER), (int) (1)), new ItemStack(Items.IRON_NUGGET, (int) (3)), 0.1F);
			GameRegistry.addSmelting(new ItemStack(Items.IRON_DOOR, (int) (1)), new ItemStack(Items.IRON_INGOT, (int) (1)), 0.1F);
			GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(Blocks.IRON_TRAPDOOR), (int) (1)), new ItemStack(Items.IRON_INGOT, (int) (1)), 0.1F);
			GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(Blocks.RAIL), (int) (1)), new ItemStack(Items.IRON_NUGGET, (int) (1)), 0.1F);
			GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(Blocks.DETECTOR_RAIL), (int) (1)), new ItemStack(Items.IRON_NUGGET, (int) (1)), 0.1F);
			GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(Blocks.ACTIVATOR_RAIL), (int) (1)), new ItemStack(Items.IRON_NUGGET, (int) (1)), 0.1F);
			GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(Blocks.TRIPWIRE_HOOK), (int) (1)), new ItemStack(Items.IRON_NUGGET, (int) (1)), 0.1F);

			
			GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE), (int) (1)), new ItemStack(Items.GOLD_NUGGET, (int) (1)), 0.1F);
			GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(Blocks.GOLDEN_RAIL), (int) (1)), new ItemStack(Items.GOLD_NUGGET, (int) (1)), 0.1F);
			GameRegistry.addSmelting(new ItemStack(Items.GOLDEN_HORSE_ARMOR, (int) (1)), new ItemStack(Items.GOLD_NUGGET, (int) (1)), 0.1F);
			GameRegistry.addSmelting(new ItemStack(Items.CLOCK, (int) (1)), new ItemStack(Items.GOLD_NUGGET, (int) (1)), 0.1F);


			GameRegistry.addSmelting(new ItemStack(ItemGavel.block, (int) (1)), new ItemStack(Items.IRON_NUGGET, (int) (1)), 0.1F);
		}




		if (AstrotweaksModVariables.Money_Can_Smelt) {
			 ItemStack output = ItemStack.EMPTY;
            if (!OreDictionary.getOres("nuggetCopper").isEmpty()) {
                ItemStack copperNuggetStack = OreDictionary.getOres("nuggetCopper").get(0);
                output = new ItemStack(copperNuggetStack.getItem(), 5, copperNuggetStack.getMetadata());
            }

            if (!output.isEmpty()) {
                GameRegistry.addSmelting(new ItemStack(ItemCopperCoin.block, 1), output, 0F);
            }
		}
		
	}
}
