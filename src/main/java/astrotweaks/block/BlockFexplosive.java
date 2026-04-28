package astrotweaks.block;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.world.Explosion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Rotation;
import net.minecraft.util.Mirror;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.material.Material;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.Block;

import java.util.Map;
import java.util.HashMap;

import astrotweaks.creativetab.TabAstroTweaks;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class BlockFexplosive extends ElementsAstrotweaksMod.ModElement {
	@GameRegistry.ObjectHolder("astrotweaks:fexplosive")
	public static final Block block = null;
	public BlockFexplosive(ElementsAstrotweaksMod instance) {
		super(instance, 433);
	}

	@Override
	public void initElements() {
		elements.blocks.add(() -> new BlockCustom().setRegistryName("fexplosive"));
		elements.items.add(() -> new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation("astrotweaks:fexplosive", "inventory"));
	}
	public static class BlockCustom extends Block {
	    public static final PropertyDirection FACING = BlockHorizontal.FACING;
	
	    public BlockCustom() {
	        super(Material.CLOTH);
	        setUnlocalizedName("fexplosive");
	        setSoundType(SoundType.PLANT);
	        setHardness(1F);
	        setResistance(0F);
	        setCreativeTab(TabAstroTweaks.tab);
	        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	    }
	
	    @Override
	    protected BlockStateContainer createBlockState() {
	        return new BlockStateContainer(this, FACING);
	    }
	
	    @Override
	    public IBlockState withRotation(IBlockState state, Rotation rot) {
	        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	    }
	
	    @Override
	    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
	        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
	    }
	
	    @Override
	    public IBlockState getStateFromMeta(int meta) {
	        // Use horizontal index (0..3) for BlockHorizontal
	        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
	    }
	
	    @Override
	    public int getMetaFromState(IBlockState state) {
	        return state.getValue(FACING).getHorizontalIndex();
	    }
	
	    @Override
	    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
	        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	    }
	    @Override
	    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos fromPos) {
	        super.neighborChanged(state, world, pos, neighborBlock, fromPos);
	        if (!world.isRemote && world.isBlockIndirectlyGettingPowered(pos) > 0) {
	            initExp(world, pos);
	        }
	    }
	    @Override
	    public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion e) {
	        super.onBlockDestroyedByExplosion(world, pos, e);
	        if (!world.isRemote) initExp(world, pos);
	    }
	    @Override
	    public void onBlockClicked(World world, BlockPos pos, EntityPlayer entity) {
	        super.onBlockClicked(world, pos, entity);
	        if (!world.isRemote) initExp(world, pos);
	    }
	    @Override
	    public boolean onBlockActivated(World world,BlockPos pos,IBlockState state, EntityPlayer entity,EnumHand hand,EnumFacing direction, float hitX, float hitY, float hitZ) {
	        super.onBlockActivated(world, pos, state, entity, hand, direction, hitX, hitY, hitZ);
	        if (!world.isRemote) initExp(world, pos);
	        return true;
	    }
	    private static void initExp(World world, BlockPos pos) {
	        // server-side only already checked by callers; defensive check:
	        if (world.isRemote) return;
	        double x = pos.getX() + 0.5;
	        double y = pos.getY() + 0.5;
	        double z = pos.getZ() + 0.5;
	        // createExplosion(Entity exploder, double x, double y, double z, float strength, boolean createsFire)
	        world.createExplosion(null, x, y, z, 2.0F, true);
	        // Optionally: world.setBlockToAir(pos); if you want to remove block explicitly
	    }
	}

}
