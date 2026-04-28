package astrotweaks.recipe;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;


public final class GavelRecipe {
    public final Block inputBlock;
    public final int inputMeta; // -1 = sny meta
    public final ItemStack[] outputs; // array ItemStack (each element is copied upon spawn)
    public final float[] chances; // same size as outputs, values?0..1


    public GavelRecipe(Block inputBlock, int inputMeta, ItemStack[] outputs, float[] chances) {
        this.inputBlock = inputBlock;
        this.inputMeta = inputMeta;
        this.outputs = outputs;
        this.chances = chances;

        // System.out.println("jaba 1.1");
    }

    public boolean matches(IBlockState state) {
        if (state == null) return false;
        Block b = state.getBlock();
        if (b != inputBlock) return false;
        if (inputMeta == -1) return true;
        int meta = b.getMetaFromState(state);

		// System.out.println("jaba 1.2");
		
        return meta == inputMeta;
    }

    // Convenient factory method for storing meta-items as ItemStack[] and chances in percent (or 0..1)
    public static GavelRecipe of(Block inBlock, int inMeta, ItemStack[] outputs, float[] chances) {
		// System.out.println("jaba 1.3");
    	
        return new GavelRecipe(inBlock, inMeta, outputs, chances);
    }
}


