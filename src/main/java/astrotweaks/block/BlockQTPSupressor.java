package astrotweaks.qts;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.NonNullList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumBlockRenderType;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.SoundType;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.Block;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nullable;
import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraft.util.math.ChunkPos;

import astrotweaks.creativetab.TabAstroTweaks;

import astrotweaks.qts.SuppressorEventHandler;
import astrotweaks.qts.SuppressorManager;


import astrotweaks.AstrotweaksMod;
import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class BlockQTPSupressor extends ElementsAstrotweaksMod.ModElement {
	@GameRegistry.ObjectHolder("astrotweaks:qtp_supressor")
	public static final Block block = null;
	public BlockQTPSupressor(ElementsAstrotweaksMod instance) {
		super(instance, 727);
	}

	@Override
	public void initElements() {
		elements.blocks.add(() -> new BlockCustom().setRegistryName("qtp_supressor"));
		elements.items.add(() -> new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	@Override
	public void init(FMLInitializationEvent event) {
		GameRegistry.registerTileEntity(TileEntityCustom.class, "astrotweaks:te_qtp_supressor");
		
	    ForgeChunkManager.setForcedChunkLoadingCallback(AstrotweaksMod.instance, new ForgeChunkManager.LoadingCallback() {
	        @Override
	        public void ticketsLoaded(List<Ticket> tickets, World world) {
	            if (world.isRemote) return;
	            for (Ticket ticket : tickets) {
	                NBTTagCompound data = ticket.getModData();
	                if (data.hasKey("x") && data.hasKey("z")) {
	                    int x = data.getInteger("x");
	                    int z = data.getInteger("z");
	                    // we will restore the forcibly uploaded chunk, if necessary
	                    ChunkPos cp = new ChunkPos(x >> 4, z >> 4);
	                    ForgeChunkManager.forceChunk(ticket, cp);
	                    // you can also restore range/other fields from data
	                } else {
						// if you saved the chunk coords directly in the ticket: example below
	                    // ForgeChunkManager.forceChunk(ticket, new ChunkPos(ticket.getChunkX(), ticket.getChunkZ()));
	                }
	            }
	        }
	    });
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
				new ModelResourceLocation("astrotweaks:qtp_supressor", "inventory"));
	}
	
	public static class BlockCustom extends Block implements ITileEntityProvider {
		public BlockCustom() {
			super(Material.IRON);
			setUnlocalizedName("qtp_supressor");
			setSoundType(SoundType.METAL);
			setHarvestLevel("pickaxe", 4);
			setHardness(100F);
			setResistance(100F);
			setLightLevel(0.333333333333F);
			setCreativeTab(TabAstroTweaks.tab);
		}

		@Override
		public EnumPushReaction getMobilityFlag(IBlockState state) {
			return EnumPushReaction.BLOCK;
		}

		@Override
		public TileEntity createNewTileEntity(World worldIn, int meta) {
			return new TileEntityCustom();
		}

		@Override
		public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int eventID, int eventParam) {
			super.eventReceived(state, worldIn, pos, eventID, eventParam);
			TileEntity tileentity = worldIn.getTileEntity(pos);
			return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
		}

		@Override
		public EnumBlockRenderType getRenderType(IBlockState state) {
			return EnumBlockRenderType.MODEL;
		}

		@Override
		public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing,
				float hitX, float hitY, float hitZ) {
			if (!world.isRemote) {
				TileEntity te = world.getTileEntity(pos);
				if (te instanceof TileEntityCustom) {
					TileEntityCustom sup = (TileEntityCustom) te;
					int currentRange = sup.getRange();
					int lmod = calc_lmod(currentRange);
					
					int newRange = sup.getRange() + lmod;
					sup.setRange(newRange);
					if (player instanceof EntityPlayerMP) {
						((EntityPlayerMP) player).sendMessage(new TextComponentString("Suppressor range set to " + newRange + " blocks"));
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
					TileEntityCustom sup = (TileEntityCustom) te;
					int currentRange = sup.getRange();
					int lmod = calc_lmod(currentRange);

					
					int newRange = sup.getRange() - lmod;
					sup.setRange(newRange);
					if (player instanceof EntityPlayerMP) {
						((EntityPlayerMP) player).sendMessage(new TextComponentString("Suppressor range set to " + newRange + " blocks"));
					}
				}
			}
			super.onBlockClicked(world, pos, player);
		}

		private static int calc_lmod(int currentRange) {
				if (currentRange > 991) return 32;
				int lmod = 32;
				if 		(currentRange < 16)  lmod = 1;
				else if (currentRange < 32)  lmod = 2;
				else if (currentRange < 128) lmod = 4;
				else if (currentRange < 256) lmod = 8;
				else if (currentRange < 512) lmod = 16;
				//else lmod = 32;
				return lmod;
			}
	}
	

	public static class TileEntityCustom extends TileEntity implements ITickable {
		private int range = 16; // R in blocks
		private boolean enabled = true; // ...
		private Ticket ticket;
		

		// ========== lifecycle TileEntity ==========
		@Override
		public void onLoad() {
		    super.onLoad();
		    if (!world.isRemote) {
		        //  ticket
		        ticket = ForgeChunkManager.requestTicket(AstrotweaksMod.instance, world, ForgeChunkManager.Type.NORMAL);
		        if (ticket != null) {
		            // you can save the coordinates in ticket.getModData() for recovery after restart
		            ticket.getModData().setInteger("x", pos.getX());
		            ticket.getModData().setInteger("z", pos.getZ());
		            // force-load a chunk containing a block
		            ChunkPos cp = new ChunkPos(pos);
		            ForgeChunkManager.forceChunk(ticket, cp);
		        }
		        //
		        SuppressorManager.addSuppressor(world, pos, range);
		    }
		}
		
		@Override
		public void onChunkUnload() {
		    if (!world.isRemote) {
		        if (ticket != null) {
		            ForgeChunkManager.unforceChunk(ticket, new ChunkPos(pos));
		            ForgeChunkManager.releaseTicket(ticket);
		            ticket = null;
		        }
		        SuppressorManager.removeSuppressor(world, pos);
		    }
		    super.onChunkUnload();
		}
		
		@Override
		public void invalidate() {
		    if (!world.isRemote) {
		        if (ticket != null) {
		            ForgeChunkManager.unforceChunk(ticket, new ChunkPos(pos));
		            ForgeChunkManager.releaseTicket(ticket);
		            ticket = null;
		        }
		        SuppressorManager.removeSuppressor(world, pos);
		    }
		    super.invalidate();
		}

		// ========== NBT save/load ==========
		@Override
		public void readFromNBT(NBTTagCompound compound) {
			super.readFromNBT(compound);
			this.range = compound.getInteger("range");
			this.enabled = compound.getBoolean("enabled");
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound compound) {
			super.writeToNBT(compound);
			compound.setInteger("range", this.range);
			compound.setBoolean("enabled", this.enabled);
			return compound;
		}

		// ========== getters/setters ==========
		public int getRange() {
			return range;
		}

		public void setRange(int range) {
			int oldRange = this.range;
			this.range = Math.max(1, Math.min(range, 1024)); // 64 chunks = 1024 blocks
			if (oldRange != this.range && !world.isRemote) {
				SuppressorManager.updateRange(world, pos, this.range);
			}
			markDirty();
			IBlockState state = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, state, state, 3);
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
			markDirty();
			IBlockState state = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, state, state, 3);
		}

		// ========== ITickable (not using) ==========
		@Override
		public void update() {
			// none now
		}

		// ========== sync ==========
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
			this.readFromNBT(pkt.getNbtCompound());
		}

		@Override
		public void handleUpdateTag(NBTTagCompound tag) {
			this.readFromNBT(tag);
		}
	}
}