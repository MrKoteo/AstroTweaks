
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

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class ItemTinSaw extends ElementsAstrotweaksMod.ModElement {
	@GameRegistry.ObjectHolder("astrotweaks:tin_saw")
	public static final Item block = null;
	public ItemTinSaw(ElementsAstrotweaksMod instance) {
		super(instance, 298);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemAxe(EnumHelper.addToolMaterial("TIN_SAW", 1, 100, 6f, 1f, 10), 2f, -2.7f) {
			@Override
			public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
			    int[] oreIds = OreDictionary.getOreIDs(repair);
			    for (int id : oreIds) {
			        String oreName = OreDictionary.getOreName(id);
			        if (oreName.equals("ingotTin")) {
			            return true;
			        }
			    }
			    return false;
			}
		}.setUnlocalizedName("tin_saw").setRegistryName("tin_saw"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("astrotweaks:tin_saw", "inventory"));
	}
}
