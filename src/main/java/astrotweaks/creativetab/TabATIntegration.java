
package astrotweaks.creativetab;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraft.item.ItemStack;
import net.minecraft.creativetab.CreativeTabs;

import astrotweaks.item.ItemStrangeQuant;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class TabATIntegration extends ElementsAstrotweaksMod.ModElement {
	public TabATIntegration(ElementsAstrotweaksMod instance) {
		super(instance, 330);
	}

	@Override
	public void initElements() {
		tab = new CreativeTabs("tabat_integration") {
			@SideOnly(Side.CLIENT)
			@Override
			public ItemStack getTabIconItem() {
				return new ItemStack(ItemStrangeQuant.block, (int) (1));
			}

			@SideOnly(Side.CLIENT)
			public boolean hasSearchBar() {
				return false;
			}
		};
	}
	public static CreativeTabs tab;
}
