package astrotweaks.creativetab;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraft.item.ItemStack;
import net.minecraft.creativetab.CreativeTabs;

import astrotweaks.item.ItemScience;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public final class TabAstroTweaks extends ElementsAstrotweaksMod.ModElement {
	public TabAstroTweaks(ElementsAstrotweaksMod instance) {
		super(instance, 344);
	}

	@Override
	public void initElements() {
		tab = new CreativeTabs("tabastro_tweaks") {
			@SideOnly(Side.CLIENT)
			@Override
			public ItemStack getTabIconItem() {
				return new ItemStack(ItemScience.block, (int) (1));
			}
			/*
			@SideOnly(Side.CLIENT)
			public boolean hasSearchBar() {
				return false;
			}*/
		};
	}
	public static CreativeTabs tab;
}
