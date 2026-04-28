
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
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;

import java.util.Set;
import java.util.HashMap;


import astrotweaks.creativetab.TabAstroTweaks;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class ItemRubyAxe extends ElementsAstrotweaksMod.ModElement {
	@GameRegistry.ObjectHolder("astrotweaks:ruby_axe")
	public static final Item block = null;
	public ItemRubyAxe(ElementsAstrotweaksMod instance) {
		super(instance, 15);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemAxe(EnumHelper.addToolMaterial("RUBY_AXE", 3, 1500, 8f, 8.5f, 16), 8.5f, -2.9f) {

			@Override
			public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
			    int[] oreIds = OreDictionary.getOreIDs(repair);
			    for (int id : oreIds) {
			        String oreName = OreDictionary.getOreName(id);
			        if (oreName.equals("gemRuby") || oreName.equals("ruby")) {
			            return true;
			        }
			    }
			    return false;
			}
			public Set<String> getToolClasses(ItemStack stack) {
				HashMap<String, Integer> ret = new HashMap<String, Integer>();
				ret.put("axe", 3);
				return ret.keySet();
			}
		}.setUnlocalizedName("ruby_axe").setRegistryName("ruby_axe"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("astrotweaks:ruby_axe", "inventory"));
	}
}
