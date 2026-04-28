
package astrotweaks.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraftforge.oredict.OreDictionary;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.init.Blocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;

import java.util.Set;
import java.util.HashMap;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class ItemSawIron extends ElementsAstrotweaksMod.ModElement {
	@GameRegistry.ObjectHolder("astrotweaks:iron_saw")
	public static final Item block = null;
	public ItemSawIron(ElementsAstrotweaksMod instance) {
		super(instance, 293);
	}
	@Override
	public void initElements() {
		elements.items.add(() -> new ItemAxe(EnumHelper.addToolMaterial("IRON_SAW", 2, 250, 8f, 2f, 14), 2f, -2.7f) {
			@Override
			public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
			    int[] oreIds = OreDictionary.getOreIDs(repair);
			    for (int id : oreIds) {
			        String oreName = OreDictionary.getOreName(id);
			        if (oreName.equals("ingotIron")) {
			            return true;
			        }
			    }
			    return false;
			}
		}.setUnlocalizedName("iron_saw").setRegistryName("iron_saw"));
	}
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("astrotweaks:iron_saw", "inventory"));
	}
}
