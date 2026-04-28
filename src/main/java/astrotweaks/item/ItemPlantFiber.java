
package astrotweaks.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;


import java.util.List;

import astrotweaks.creativetab.TabAstroTweaks;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class ItemPlantFiber extends ElementsAstrotweaksMod.ModElement {
	@GameRegistry.ObjectHolder("astrotweaks:plant_fiber")
	public static final Item block = null;
	public ItemPlantFiber(ElementsAstrotweaksMod instance) {
		super(instance, 43);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("astrotweaks:plant_fiber", "inventory"));
	}
	public static class ItemCustom extends Item {
		public ItemCustom() {
			//setMaxDamage(0);
			//maxStackSize = 64;
			setUnlocalizedName("plant_fiber");
			setRegistryName("plant_fiber");
			setCreativeTab(TabAstroTweaks.tab);
		}
		/*
		@Override
		public int getItemEnchantability() {
			return 0;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack itemstack) {
			return 0;
		}

		@Override
		public float getDestroySpeed(ItemStack par1ItemStack, IBlockState par2Block) {
			return 1F;
		}*/

		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> tooltip, ITooltipFlag flag) {
  			super.addInformation(itemstack, world, tooltip, flag);
    		tooltip.add(TextFormatting.AQUA + new TextComponentTranslation("item.plant_fiber.tooltip").getFormattedText());
		}
	}
}
