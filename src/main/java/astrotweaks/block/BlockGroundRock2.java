package astrotweaks.block;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

//import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.Rotation;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Mirror;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;


import net.minecraft.init.Blocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.material.Material;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;

import astrotweaks.world.SurfaceWorldGenerator;

import java.util.Collections;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;

import astrotweaks.item.ItemRockFlat;
import astrotweaks.AstrotweaksModVariables;
import astrotweaks.ModVariables;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class BlockGroundRock2 extends ElementsAstrotweaksMod.ModElement {
	@GameRegistry.ObjectHolder("astrotweaks:ground_rock_2")
	public static final Block block = null;
	public BlockGroundRock2(ElementsAstrotweaksMod instance) {
		super(instance, 559);
	}

	@Override
	public void initElements() {
		elements.blocks.add(() -> new BlockCustom().setRegistryName("ground_rock_2"));
		elements.items.add(() -> new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
				new ModelResourceLocation("astrotweaks:ground_rock_2", "inventory"));
	}

	@Override
	public void generateWorld(Random random, int chunkX, int chunkZ, World world, int dimID, IChunkGenerator cg, IChunkProvider cp) {
		if (!ModVariables.Enable_Ground_Elements) return;
	    if (dimID != 0) return;
			double rga = ModVariables.Rock_Gen_Attempts;
			Set<Biome> rgb = ModVariables.Rock_Gen_Biomes_Cached;
		
		    SurfaceWorldGenerator.generateSurface(random, chunkX, chunkZ, world, block, rgb, rga /*Double attempts*/, ModVariables.Rock_Gen_Min_Y /*min Y*/, ModVariables.Rock_Gen_Max_Y /*max Y*/);
	}

	public static class BlockCustom extends Block {
		public static final PropertyDirection FACING = BlockHorizontal.FACING;
		public BlockCustom() {
			super(Material.CLAY);
			setUnlocalizedName("ground_rock_2");
			setSoundType(SoundType.STONE);
			setHardness(0F);
			setResistance(0F);
			//setLightLevel(0F);
			setLightOpacity(0);
			this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		}

		@SideOnly(Side.CLIENT)
		@Override
		public BlockRenderLayer getBlockLayer() {
			return BlockRenderLayer.CUTOUT_MIPPED;
		}

		@Override
		@javax.annotation.Nullable
		public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
			return NULL_AABB;
		}

		@Override
		public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
			return true;
		}

		@Override
		public boolean isFullCube(IBlockState state) {
			return false;
		}

		@Override
		public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
			/*
			switch ((EnumFacing) state.getValue(BlockHorizontal.FACING)) {
				case SOUTH :
				default :
					return new AxisAlignedBB(1, 0, 1, 0.25, 0.125, 0.25);
				case NORTH :
					return new AxisAlignedBB(0, 0, 0, 0.75, 0.125, 0.75);
				case EAST :
					return new AxisAlignedBB(1, 0, 0, 0.25, 0.125, 0.75);
				case WEST :
					return new AxisAlignedBB(0, 0, 1, 0.75, 0.125, 0.25);
			}*/
			return new AxisAlignedBB(0.250, 0.0, 0.250, 0.750, 0.125, 0.750);
		}

		@Override
		protected net.minecraft.block.state.BlockStateContainer createBlockState() {
			return new net.minecraft.block.state.BlockStateContainer(this, new IProperty[]{FACING});
		}

		@Override
		public IBlockState withRotation(IBlockState state, Rotation rot) {
			return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
		}

		@Override
		public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
			return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
		}

		@Override
		public IBlockState getStateFromMeta(int meta) {
		    EnumFacing facing = EnumFacing.getFront(meta);
		    
		    // protect
		    if (facing.getAxis() == EnumFacing.Axis.Y) {
		        facing = EnumFacing.NORTH;
		    }
		    
		    return this.getDefaultState().withProperty(FACING, facing);
		}

		@Override
		public int getMetaFromState(IBlockState state) {
		    EnumFacing facing = (EnumFacing) state.getValue(FACING);
		    return facing.getIndex();
		}

		@Override
		public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
				EntityLivingBase placer) {
			if (facing == EnumFacing.UP || facing == EnumFacing.DOWN)
				return this.getDefaultState().withProperty(FACING, EnumFacing.NORTH);
			return this.getDefaultState().withProperty(FACING, facing);
		}

		@Override
		public boolean isOpaqueCube(IBlockState state) {
			return false;
		}
		@Override
		public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
		    return BlockFaceShape.UNDEFINED;
		}

		@Override
		public boolean isReplaceable(IBlockAccess blockAccess, BlockPos pos) {
			return true;
		}

		
		@Override
		public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		    drops.add(new ItemStack(ItemRockFlat.block, (int) (1))); // <astrotweaks:rock_flat>
		}
		
		@Override
		public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		    BlockPos below = pos.down();
		    IBlockState stateBelow = worldIn.getBlockState(below);
		    return stateBelow.isSideSolid(worldIn, below, EnumFacing.UP);
		}

		@Override
		public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos fromPos) {
			super.neighborChanged(state, world, pos, neighborBlock, fromPos);
			if (world.isRemote) return;
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			if (world.isBlockIndirectlyGettingPowered(new BlockPos(x, y, z)) > 0) {
			} else {
			}
			
			if ((((world.getBlockState(new BlockPos((int) x, (int) y, (int) z))).getBlock() == BlockGroundRock2.block.getDefaultState().getBlock())
				&& ((world.getBlockState(new BlockPos((int) x, (int) (y - 1), (int) z))).getBlock() == Blocks.AIR.getDefaultState().getBlock()))) {
				world.setBlockToAir(new BlockPos((int) x, (int) y, (int) z));

				EntityItem entityToSpawn = new EntityItem(world, (x + 0.5), (y + 0.5), (z + 0.5), new ItemStack(ItemRockFlat.block, (int) (1)));
				entityToSpawn.setPickupDelay(10);
				world.spawnEntity(entityToSpawn);

			}
		
		    if (isAdjacentToWater(world, pos) || world.getBlockState(pos).getMaterial() == Material.WATER) {
		        world.destroyBlock(pos, true);
		    }

			

		}


		@Override
		public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entity, EnumHand hand, EnumFacing direction,
				float hitX, float hitY, float hitZ) {
			super.onBlockActivated(world, pos, state, entity, hand, direction, hitX, hitY, hitZ);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			
			world.setBlockToAir(new BlockPos((int) x, (int) y, (int) z));
			if (!world.isRemote) {
				EntityItem entityToSpawn = new EntityItem(world, (x + 0.5), (y + 0.5), (z + 0.5), new ItemStack(ItemRockFlat.block, (int) (1)));
				entityToSpawn.setPickupDelay(10);
				world.spawnEntity(entityToSpawn);
			}

			return true;
		}

		private boolean isAdjacentToWater(World world, BlockPos pos) {
		    for (EnumFacing f : EnumFacing.values()) {
		        IBlockState s = world.getBlockState(pos.offset(f));
		        if (s.getMaterial() == Material.WATER) return true;
		    }
		    return false;
		}
	}
}
