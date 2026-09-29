package astrotweaks.block.black_hole;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;



public class BlackHoleBlock extends Block {

    public static final Block INSTANCE = new BlackHoleBlock()
            .setRegistryName("astrotweaks", "black_hole")
            .setUnlocalizedName("black_hole");

    public BlackHoleBlock() {
        super(Material.PORTAL);
        setHardness(-1.0F);
        setResistance(1000000000.0F);
        setLightOpacity(0);
        setLightLevel(0.0F);
        //setCreativeTab(ATCreativeTabs.ASTRO_TWEAKS_CT);
        // hardness -1 makes it unbreakable in survival, but allow creative break
        setBlockUnbreakable();
    }

    // Visual
    @Override public MapColor getMapColor(IBlockState s, IBlockAccess w, BlockPos p) { return MapColor.BLACK; }
    @Override public boolean isOpaqueCube(IBlockState s) { return false; }
    @Override public boolean isFullCube(IBlockState s) { return false; }
    @Override public boolean isNormalCube(IBlockState s, IBlockAccess w, BlockPos p) { return false; }
    @Override public EnumBlockRenderType getRenderType(IBlockState s) { return EnumBlockRenderType.ENTITYBLOCK_ANIMATED; }
    @SideOnly(Side.CLIENT)
    @Override public BlockRenderLayer getBlockLayer() { return BlockRenderLayer.TRANSLUCENT; }

    @Nullable
    @Override public AxisAlignedBB getCollisionBoundingBox(IBlockState s, IBlockAccess w, BlockPos p) { return NULL_AABB; }
    //@Override public boolean isReplaceable(IBlockAccess w, BlockPos p) { return false; }

    @Override public boolean hasTileEntity(IBlockState s) { return true; }
    @Override public TileEntity createTileEntity(World w, IBlockState s) { return new BlackHoleTileEntity(); }

    // Drops / pick
    @Override public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        // Drop item with mass preserved (handled via getPickBlock + breakBlock)
        // For survival unbreakable, this normally not called; but provide for creative pick
    }

    @Override public ItemStack getPickBlock(IBlockState state, net.minecraft.util.math.RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        ItemStack stack = new ItemStack(this);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof BlackHoleTileEntity) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setDouble(BlackHoleTileEntity.TAG_MASS, ((BlackHoleTileEntity) te).getMass());
            stack.setTagCompound(tag);
        }
        return stack;
    }

    @Override public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof BlackHoleTileEntity && stack.hasTagCompound() && stack.getTagCompound().hasKey(BlackHoleTileEntity.TAG_MASS)) {
            double m = stack.getTagCompound().getDouble(BlackHoleTileEntity.TAG_MASS);
            ((BlackHoleTileEntity) te).setMass(m);
        } else if (te instanceof BlackHoleTileEntity) {
            ((BlackHoleTileEntity) te).setMass(BlackHoleUtils.DEFAULT_MASS);
        }
    }

    @Override public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && player.isSneaking()) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof BlackHoleTileEntity) {
                double m = ((BlackHoleTileEntity) te).getMass();
                double rH = BlackHoleUtils.getHorizonRadius(m);
                double rG = BlackHoleUtils.getGravityRange(m);
                double rV = BlackHoleUtils.getVacuumRadius(m);
                player.sendMessage(new net.minecraft.util.text.TextComponentString(String.format("BlackHole mass=%.1f horizon=%.2f gravRange=%.2f vacuum=%.2f", m, rH, rG, rV)));
            }
            return true;
        }
        return false;
    }

    @Override public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }

    // Keep for Waila etc - allow to harvest in creative only
    @Override public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, net.minecraft.entity.Entity entity) { return false; }
    @Override public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) { return player.isCreative(); }
}
