
package astrotweaks.block;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.gen.feature.WorldGenBush;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.NonNullList;
import net.minecraft.util.EnumFacing;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockBush;
import net.minecraft.block.Block;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import javax.annotation.Nullable;

import astrotweaks.util.DropHandler;
import astrotweaks.util.DropHandler.DropEntry;
import net.minecraft.init.Items;

//import astrotweaks.creativetab.TabAstroTweaks;
import astrotweaks.ElementsAstrotweaksMod;


@ElementsAstrotweaksMod.ModElement.Tag
public class BlockBush4 extends ElementsAstrotweaksMod.ModElement {
	@GameRegistry.ObjectHolder("astrotweaks:bush_4")
	public static final Block block = null;
	public BlockBush4(ElementsAstrotweaksMod instance) {
		super(instance, 734);
	}

	@Override
	public void initElements() {
		elements.blocks.add(() -> new BlockCustomFlower());
		elements.items.add(() -> new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation("astrotweaks:bush_4", "inventory"));
	}

	public static class BlockCustomFlower extends BlockBush {
		public BlockCustomFlower() {
			setSoundType(SoundType.PLANT);
			//setCreativeTab(TabAstroTweaks.tab);
			setHardness(0F);
			setResistance(0F);
			setUnlocalizedName("bush_4");
			setRegistryName("bush_4");
		}

		@Override
		public boolean isReplaceable(IBlockAccess blockAccess, BlockPos pos) {
			return true;
		}
	    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
	      return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.5D, 1.0D);
	    }
		@Override
		public Block.EnumOffsetType getOffsetType() {
			return Block.EnumOffsetType.NONE;
		}
		@Override
		public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
			return 100;
		}
		@Override
		public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
			return 60;
		}
        private static final DropEntry[] DE_TABLE = new DropEntry[] {
            new DropEntry("minecraft:stick",    1, 40.0),
            new DropEntry("none",    			1, 60.0)
        };
		private static final DropHandler DE_DROP_TABLE = new DropHandler(DE_TABLE);
		@Override public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		    World w = (world instanceof World) ? (World) world : null;

		    int rolls = 3;
		    DE_DROP_TABLE.generateDrops(drops, w, rolls);
		}
		@Override
		public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable net.minecraft.tileentity.TileEntity te, ItemStack stack) {
		    boolean hasShears = stack != null && stack.getItem() == Items.SHEARS;
		    boolean hasSilk = stack != null && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0;
		    if (!world.isRemote && (hasShears || hasSilk)) {
		        spawnAsEntity(world, pos, new ItemStack(this));
		    } else {
		        super.harvestBlock(world, player, pos, state, te, stack);
		    }
		}
		@Override
		public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
			return new ItemStack(Item.getItemFromBlock(this), 1, this.damageDropped(state));
		}
		@Override
		public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
			return EnumPlantType.Plains;
		}
	}
}
