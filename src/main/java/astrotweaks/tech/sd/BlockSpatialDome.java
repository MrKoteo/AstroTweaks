package astrotweaks.tech.sd;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

import astrotweaks.AstrotweaksMod;

/**
 * Spatial Dome — защита от гравитации чёрной дыры в сфере радиуса range.
 *
 * <p>UX как у QTS: ПКМ +шаг, Shift+ПКМ −шаг, ЛКМ −шаг. Сфера от центра блока.
 * Бесплатный, всегда активен, держит свой чанк (приоритет перед BH: область
 * защиты не может быть прогружена без самого купола).
 */
public class BlockSpatialDome {

    /** Hard cap per spec. */
    public static int MAX_RANGE = 128;
    //public static final int MIN_RANGE = 1;
    public static final int DEFAULT_RANGE = 16;

    public static void updVars() { MAX_RANGE = astrotweaks.ModVariables.SD_Max_Range; }



    public static class BlockCustom extends Block implements net.minecraft.block.ITileEntityProvider {
        public BlockCustom() {
            super(Material.IRON);
            setUnlocalizedName("spatial_dome");
            setSoundType(SoundType.METAL);
            setHarvestLevel("pickaxe", 4);
            setHardness(100F);
            setResistance(100F);
            setLightLevel(0.333333333333F);
            setCreativeTab(astrotweaks.creativetab.ATCreativeTabs.ASTRO_TWEAKS_CT);
        }

        @Override public EnumPushReaction getMobilityFlag(IBlockState state) { return EnumPushReaction.BLOCK; }
        @Override public TileEntity createNewTileEntity(World worldIn, int meta) { return new TileEntityCustom(); }
        @Override public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int eventID, int eventParam) {
            super.eventReceived(state, worldIn, pos, eventID, eventParam);
            TileEntity tileentity = worldIn.getTileEntity(pos);
            return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
        }
        @Override public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.MODEL; }

        @Override
        public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
            if (!world.isRemote) {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof TileEntityCustom) {
                    TileEntityCustom dome = (TileEntityCustom) te;
                    int currentRange = dome.getRange();
                    int lmod = calcLmod(currentRange);
                    int newRange = player.isSneaking() ? currentRange - lmod : currentRange + lmod;
                    dome.setRange(newRange);
                    if (player instanceof EntityPlayerMP) {
                        player.sendStatusMessage(new TextComponentTranslation("sd.change_range", dome.getRange()), true);
                    }
                }
            }
            return true;
        }

        @Override
        public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
            if (!world.isRemote) {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof TileEntityCustom) {
                    TileEntityCustom dome = (TileEntityCustom) te;
                    int newRange = dome.getRange() - calcLmod(dome.getRange());
                    dome.setRange(newRange);
                    if (player instanceof EntityPlayerMP) {
                        player.sendStatusMessage(new TextComponentTranslation("sd.change_range", dome.getRange()), true);
                    }
                }
            }
            super.onBlockClicked(world, pos, player);
        }

        @Override
        public void breakBlock(World world, BlockPos pos, IBlockState state) {
            // TE invalidate/onChunkUnload снимают регистрацию в DomeManager сами.
            super.breakBlock(world, pos, state);
        }

        private static int calcLmod(int currentRange) {
            if (currentRange > MAX_RANGE) return MAX_RANGE;
            if (currentRange < 16) return 1;
            if (currentRange < 32) return 2;
            if (currentRange < 64) return 4;
            return 8;
        }
    }

    public static class TileEntityCustom extends TileEntity {
        private int range = DEFAULT_RANGE;
        private Ticket ticket;

        @Override
        public void onLoad() {
            super.onLoad();
            // Регистрация в менеджере на ОБЕИХ сторонах: клиент подавляет
            // локальную swim-тягу, сервер — всё остальное.
            DomeManager.addDome(world, pos, range);
            if (!world.isRemote) {
                ticket = ForgeChunkManager.requestTicket(AstrotweaksMod.instance, world, ForgeChunkManager.Type.NORMAL);
                if (ticket != null) {
                    ticket.getModData().setInteger("x", pos.getX());
                    ticket.getModData().setInteger("z", pos.getZ());
                    ForgeChunkManager.forceChunk(ticket, new ChunkPos(pos));
                }
            }
        }

        @Override
        public void onChunkUnload() {
            if (world != null && pos != null) {
                DomeManager.removeDome(world, pos);
                if (!world.isRemote && ticket != null) {
                    ForgeChunkManager.unforceChunk(ticket, new ChunkPos(pos));
                    ForgeChunkManager.releaseTicket(ticket);
                    ticket = null;
                }
            }
            super.onChunkUnload();
        }

        @Override
        public void invalidate() {
            if (world != null && pos != null) {
                DomeManager.removeDome(world, pos);
                if (!world.isRemote && ticket != null) {
                    ForgeChunkManager.unforceChunk(ticket, new ChunkPos(pos));
                    ForgeChunkManager.releaseTicket(ticket);
                    ticket = null;
                }
            }
            super.invalidate();
        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            super.readFromNBT(compound);
            int r = compound.getInteger("range");
            this.range = Math.max(1, Math.min(r == 0 ? DEFAULT_RANGE : r, MAX_RANGE));
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            super.writeToNBT(compound);
            compound.setInteger("range", this.range);
            return compound;
        }

        public int getRange() { return range; }

        public void setRange(int range) {
            int oldRange = this.range;
            this.range = Math.max(1, Math.min(range, MAX_RANGE));
            if (oldRange != this.range && world != null && !world.isRemote) {
                DomeManager.updateRange(world, pos, oldRange, this.range);
                markDirty();
                IBlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 3);
            }
        }

        @Override
        public SPacketUpdateTileEntity getUpdatePacket() {
            return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
        }

        @Override
        public NBTTagCompound getUpdateTag() {
            return this.writeToNBT(new NBTTagCompound());
        }

        @Override
        public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
            int oldRange = this.range;
            this.readFromNBT(pkt.getNbtCompound());
            // Клиент: обновить chunk-индекс под новый радиус.
            if (world != null && world.isRemote && oldRange != this.range) {
                DomeManager.updateRange(world, pos, oldRange, this.range);
            }
        }

        @Override
        public void handleUpdateTag(NBTTagCompound tag) {
            int oldRange = this.range;
            this.readFromNBT(tag);
            if (world != null && world.isRemote && oldRange != this.range) {
                DomeManager.updateRange(world, pos, oldRange, this.range);
            }
        }
    }
}
