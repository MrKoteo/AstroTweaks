package astrotweaks.block;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.SoundType;
import net.minecraft.block.Block;

import astrotweaks.creativetab.TabAstroTweaks;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class BlockArkResonator extends ElementsAstrotweaksMod.ModElement {
	@GameRegistry.ObjectHolder("astrotweaks:ark_resonator")
	public static final Block block = null;
	public BlockArkResonator(ElementsAstrotweaksMod instance) {
		super(instance, 721);
	}
	@Override
	public void initElements() {
		elements.blocks.add(() -> new BlockCustom().setRegistryName("ark_resonator"));
		elements.items.add(() -> new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
			new ModelResourceLocation("astrotweaks:ark_resonator", "inventory"));
	}
	public static class BlockCustom extends Block {
		public BlockCustom() {
			super(Material.IRON);
			setUnlocalizedName("ark_resonator");
			setSoundType(SoundType.METAL);
			setHarvestLevel("pickaxe", 4);
			setHardness(100F);
			setResistance(100F);
			setLightLevel(0.066666666667F);
			setCreativeTab(TabAstroTweaks.tab);
		}
		@Override public EnumPushReaction getMobilityFlag(IBlockState state) { return EnumPushReaction.BLOCK; }
	}
}
