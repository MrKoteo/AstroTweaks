
package astrotweaks.block;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.SoundType;
import net.minecraft.block.Block;

import net.minecraft.util.math.Vec3d;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.Entity;
import net.minecraft.command.ICommandSender;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;


import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class BlockUnknownBlock extends ElementsAstrotweaksMod.ModElement {
	@GameRegistry.ObjectHolder("astrotweaks:unknown_block")
	public static final Block block = null;
	public BlockUnknownBlock(ElementsAstrotweaksMod instance) {
		super(instance, 91);
	}

	@Override
	public void initElements() {
		elements.blocks.add(() -> new BlockCustom().setRegistryName("unknown_block"));
		elements.items.add(() -> new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
				new ModelResourceLocation("astrotweaks:unknown_block", "inventory"));
	}
	public static class BlockCustom extends Block {
		public BlockCustom() {
			super(Material.ROCK);
			setUnlocalizedName("unknown_block");
			setSoundType(SoundType.METAL);
			setHardness(20F);
			setResistance(10F);
			//setLightLevel(0F);
			setLightOpacity(0);
			setCreativeTab(null);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public BlockRenderLayer getBlockLayer() {
			return BlockRenderLayer.TRANSLUCENT;
		}
		@Override
		@javax.annotation.Nullable
		public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
			return NULL_AABB;
		}
		/*
		@Override
		public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
			return true;
		}*/
		@Override
		public boolean isFullCube(IBlockState state) {
			return false;
		}
		@Override
		public boolean isOpaqueCube(IBlockState state) {
			return false;
		}
		@Override
		public EnumPushReaction getMobilityFlag(IBlockState state) {
			return EnumPushReaction.BLOCK;
		}
		@Override
		public MapColor getMapColor(IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
			return MapColor.BLACK;
		}
		@Override
		public int quantityDropped(Random random) {
			return 0;
		}

		@Override
		public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entity, EnumHand hand, EnumFacing direction,
				float hitX, float hitY, float hitZ) {
			super.onBlockActivated(world, pos, state, entity, hand, direction, hitX, hitY, hitZ);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

			if (!world.isRemote && world.getMinecraftServer() != null) {
				world.getMinecraftServer().getCommandManager().executeCommand(new ICommandSender() {
					@Override public String getName() {return "";}
					@Override public boolean canUseCommand(int permission, String command) {return true;}
					@Override public World getEntityWorld() {return world;}
					@Override public MinecraftServer getServer() {return world.getMinecraftServer();}
					@Override public boolean sendCommandFeedback() {return false;}
					@Override public BlockPos getPosition() {return new BlockPos((int) x, (int) y, (int) z);}
					@Override public Vec3d getPositionVector() {return new Vec3d(x, y, z);}
				}, "tellraw @a[distance=..4] {\"text\":\"You don't know what it is!\",\"color\":\"red\"}");
			}
			if (entity instanceof EntityPlayer)
				((EntityPlayer) entity).closeScreen();

			return true;
		}
	}
}
