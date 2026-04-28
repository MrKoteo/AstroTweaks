package astrotweaks.block;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.DamageSource;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.SoundType;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;



import net.minecraft.util.NonNullList;
import java.util.Map;
import java.util.HashMap;

//import astrotweaks.procedure.ProcedureQMIsHot;

import astrotweaks.ModVariables;

import astrotweaks.creativetab.TabAstroTweaks;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class BlockQmBlock extends ElementsAstrotweaksMod.ModElement {
	@GameRegistry.ObjectHolder("astrotweaks:qm_block")
	public static final Block block = null;
	public BlockQmBlock(ElementsAstrotweaksMod instance) { super(instance, 259); }
	@Override
	public void initElements() {
		elements.blocks.add(() -> new BlockCustom().setRegistryName("qm_block"));
		elements.items.add(() -> new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation("astrotweaks:qm_block", "inventory"));
	}
	public static class BlockCustom extends Block {
		public BlockCustom() {
			super(Material.IRON);
			setUnlocalizedName("qm_block");
			setSoundType(SoundType.METAL);
			setHardness(-1.0F);
			setResistance(1000000F);
			setLightLevel(1F);
			//setLightOpacity(255);
			//setCreativeTab(TabAstroTweaks.tab);
			setBlockUnbreakable();
		}
		@Override public MapColor getMapColor(IBlockState state, IBlockAccess blockAccess, BlockPos pos) { return MapColor.IRON; }
		@Override
	    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
	    	if (ModVariables.QM_is_fully_unbreakable) {
	    		return false;
	    	}
	    	return true;
	    }
		@Override public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {}
		@Override
		public void onBlockClicked(World world, BlockPos pos, EntityPlayer entity) {
			super.onBlockClicked(world, pos, entity);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			boolean Break = true;

			QM_is_hot(entity, world, x, y, z, Break);
		}

		@Override
		public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entity, EnumHand hand, EnumFacing direction, float hitX, float hitY, float hitZ) {
			super.onBlockActivated(world, pos, state, entity, hand, direction, hitX, hitY, hitZ);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			boolean Break = false;

			QM_is_hot(entity, world, x, y, z, Break);

			return true;
		}

	}

	private static void QM_is_hot(Entity entity, World world, int x, int y, int z, boolean Break) {
		entity.attackEntityFrom(DamageSource.ON_FIRE, (float) 10);
		if (!Break) {
			world.setBlockToAir(new BlockPos(x, y, z));
			world.setBlockState(new BlockPos(x, y, z), BlockQmBlock.block.getDefaultState(), 3);
		} else if (ModVariables.QM_is_fully_unbreakable && !Break) {
			world.setBlockToAir(new BlockPos(x, y, z));
			world.setBlockState(new BlockPos(x, y, z), BlockQmBlock.block.getDefaultState(), 3);
		}
	}
}
