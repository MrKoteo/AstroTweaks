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
import net.minecraft.item.ItemBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.SoundType;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;

import astrotweaks.creativetab.TabAstroTweaks;
import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class BlockUDestroyerBlock extends ElementsAstrotweaksMod.ModElement {
	@GameRegistry.ObjectHolder("astrotweaks:u_destroyer_block")
	public static final Block block = null;

	public BlockUDestroyerBlock(ElementsAstrotweaksMod instance) {
		super(instance, 244);
	}
	@Override
	public void initElements() {
		elements.blocks.add(() -> new BlockCustom().setRegistryName("u_destroyer_block"));
		elements.items.add(() -> new ItemBlock(block).setRegistryName("u_destroyer_block"));
	}
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(net.minecraft.item.Item.getItemFromBlock(block), 0,
				new ModelResourceLocation("astrotweaks:u_destroyer_block", "inventory"));
	}

	public static class BlockCustom extends Block {
		public BlockCustom() {
			super(Material.IRON, MapColor.IRON);
			setUnlocalizedName("u_destroyer_block");
			setSoundType(SoundType.METAL);
			setCreativeTab(TabAstroTweaks.tab);
			setBlockUnbreakable();
		}
		@Override
		public net.minecraft.block.material.EnumPushReaction getMobilityFlag(IBlockState state) {
			return net.minecraft.block.material.EnumPushReaction.IGNORE;
		}
		@Override
		public MapColor getMapColor(IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
			return MapColor.AIR;
		}
		@Override
		public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos fromPos) {
			super.neighborChanged(state, world, pos, neighborBlock, fromPos);
			if (world.isBlockIndirectlyGettingPowered(pos) > 0) {
				DelXArea(world, pos);
			}
		}
		@Override
		public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entity, EnumHand hand, EnumFacing direction,
				float hitX, float hitY, float hitZ) {
			super.onBlockActivated(world, pos, state, entity, hand, direction, hitX, hitY, hitZ);
			DelXArea(world, pos);
			return true;
		}
		// Execute several fill commands to clear stacked regions around pos (server-side)
		private static void DelXArea(World world, BlockPos pos) {
			if (world == null || pos == null || world.isRemote) return;
			MinecraftServer server = world.getMinecraftServer();
			if (server == null) return;

			final ICommandSender sender = new ICommandSender() {
				@Override public String getName() { return "udestroyer"; }
				@Override public boolean canUseCommand(int perm, String cmd) { return true; }
				@Override public World getEntityWorld() { return world; }
				@Override public MinecraftServer getServer() { return server; }
				@Override public boolean sendCommandFeedback() { return false; }
				@Override public BlockPos getPosition() { return pos; }
				@Override public Vec3d getPositionVector() { return new Vec3d(pos.getX(), pos.getY(), pos.getZ()); }
			};

			final String[] cmds = {
				"fill ~10 ~10 ~10 ~-10 ~9 ~-10 air 0 replace",
				"fill ~10 ~8 ~10 ~-10 ~6 ~-10 air 0 replace",
				"fill ~10 ~5 ~10 ~-10 ~3 ~-10 air 0 replace",
				"fill ~10 ~2 ~10 ~-10 ~-2 ~-10 air 0 replace",
				"fill ~10 ~-3 ~10 ~-10 ~-5 ~-10 air 0 replace",
				"fill ~10 ~-6 ~10 ~-10 ~-8 ~-10 air 0 replace",
				"fill ~10 ~-9 ~10 ~-10 ~-10 ~-10 air 0 replace"
			};
			for (String c : cmds) {
				try {
					server.getCommandManager().executeCommand(sender, c);
				} catch (Exception e) {
					// prevent server crash; ignore or log if desired
				}
			}
		}
	}
}
