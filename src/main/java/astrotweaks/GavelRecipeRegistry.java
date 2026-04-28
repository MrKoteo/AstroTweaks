package astrotweaks.recipe;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;

import java.util.ArrayList;
import java.util.List;

public class GavelRecipeRegistry {
    private static final List<GavelRecipe> RECIPES = new ArrayList<>();

	/*
    @SubscribeEvent
	public static void init(FMLInitializationEvent event) {
		GavelRecipeRegistry.initDefaults();
	}
	*/

    public static void register(GavelRecipe r) {
        RECIPES.add(r);

        // System.out.println("jaba 2.1");
    }

    public static List<GavelRecipe> getRecipes() {
		// System.out.println("jaba 2.2");
    	
        return RECIPES;
    }

    public static void initDefaults() {
        // stone (meta 0) -> astrotweaks:stone_brick (meta 0) with three chances: 100%, 50%, 25%
        // System.out.println("jaba 2.3");
        
        register(GavelRecipe.of(
            Blocks.STONE, 0,
            new ItemStack[] {
                new ItemStack(astrotweaks.item.ItemStoneBrick.block, 1, 0), // 2 with 100% chance
                new ItemStack(astrotweaks.item.ItemStoneBrick.block, 1, 0), // 1 with 50%
                new ItemStack(astrotweaks.item.ItemStoneBrick.block, 1, 0)  // 1 with 20%
            },
            new float[] { 1.0f, 0.5f, 0.2f }
        ));

        //  (stonebrick, meta 0) -> 4 stone_brick (100%)
        register(GavelRecipe.of(
            Blocks.STONEBRICK, 0,
            new ItemStack[] { new ItemStack(astrotweaks.item.ItemStoneBrick.block, 4, 0) },
            new float[] { 1.0f }
        ));

        
    }
}