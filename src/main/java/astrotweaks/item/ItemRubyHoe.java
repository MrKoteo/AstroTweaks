
package astrotweaks.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraftforge.oredict.OreDictionary;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.Item;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import java.util.Set;
import java.util.HashMap;

import astrotweaks.creativetab.TabAstroTweaks;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class ItemRubyHoe extends ElementsAstrotweaksMod.ModElement {
	@GameRegistry.ObjectHolder("astrotweaks:ruby_hoe")
	public static final Item block = null;
	public ItemRubyHoe(ElementsAstrotweaksMod instance) {
		super(instance, 17);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemHoe(EnumHelper.addToolMaterial("RUBY_HOE", 3, 1500, 8f, 3.1f, 16)) {
			public Set<String> getToolClasses(ItemStack stack) {
				HashMap<String, Integer> ret = new HashMap<String, Integer>();
				ret.put("hoe", 3);
				return ret.keySet();
			}
			@Override
			public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
			    int[] oreIds = OreDictionary.getOreIDs(repair);
			    for (int id : oreIds) {
			        String oreName = OreDictionary.getOreName(id);
			        if (oreName.equals("gemRuby")) {
			            return true;
			        }
			    }
			    return false;
			}
			
		}.setUnlocalizedName("ruby_hoe").setRegistryName("ruby_hoe"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("astrotweaks:ruby_hoe", "inventory"));
	}
}
