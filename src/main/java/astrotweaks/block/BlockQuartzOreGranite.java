
package astrotweaks.block;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.NonNullList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;
import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.SoundType;
import net.minecraft.block.Block;

import java.util.Random;

import astrotweaks.creativetab.TabAstroTweaks;
import astrotweaks.AstrotweaksModVariables;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class BlockQuartzOreGranite extends ElementsAstrotweaksMod.ModElement {
	@GameRegistry.ObjectHolder("astrotweaks:quartz_ore_granite")
	public static final Block block = null;
	public BlockQuartzOreGranite(ElementsAstrotweaksMod instance) {
		super(instance, 4);
	}

	@Override
	public void initElements() {
		elements.blocks.add(() -> new BlockCustom().setRegistryName("quartz_ore_granite"));
		elements.items.add(() -> new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
				new ModelResourceLocation("astrotweaks:quartz_ore_granite", "inventory"));
	}

/*
	@Override
	public void generateWorld(Random random, int chunkX, int chunkZ, World world, int dimID, IChunkGenerator cg, IChunkProvider cp) {
		if (!(dimID == 0)) return;
		if (!(AstrotweaksModVariables.Overworld_Quartz_Generation)) return;
		
		for (int i = 0; i < 6; i++) {  // gen attempts
			int x = chunkX + random.nextInt(16);
			int y = random.nextInt(30) + 25;
			int z = chunkZ + random.nextInt(16);
			(new WorldGenMinable(block.getDefaultState(), 4, new com.google.common.base.Predicate<IBlockState>() {
				public boolean apply(IBlockState blockAt) {
					boolean blockCriteria = false;
					IBlockState require;
					require = Blocks.STONE.getStateFromMeta(1);
					try {
						if ((blockAt.getBlock() == require.getBlock())
								&& (blockAt.getBlock().getMetaFromState(blockAt) == require.getBlock().getMetaFromState(require)))
							blockCriteria = true;
					} catch (Exception e) {
						if (blockAt.getBlock() == require.getBlock())
							blockCriteria = true;
					}
					return blockCriteria;
				}
			})).generate(world, random, new BlockPos(x, y, z));
		}
	}

*/


	@Override
	public void generateWorld(Random random, int chunkX, int chunkZ, World world, int dimID, IChunkGenerator cg, IChunkProvider cp) {
	    if (dimID != 0) return;
	    if (!AstrotweaksModVariables.Overworld_Quartz_Generation) return;
			
		com.google.common.base.Predicate<IBlockState> match = new com.google.common.base.Predicate<IBlockState>() {
		    @Override
		    public boolean apply(IBlockState state) {
		        //if (state == null) return false;
		        return state.getBlock() == Blocks.STONE && state.getBlock().getMetaFromState(state) == 1;
		    }
		};
	
	    for (int i = 0; i < 4; i++) { // gen attempts
	        int x = chunkX + random.nextInt(16);
	        int y = random.nextInt(25) + 25;
	        int z = chunkZ + random.nextInt(16);
	
	        new WorldGenMinable(block.getDefaultState(), 4, match).generate(world, random, new BlockPos(x, y, z));
	    }
	}
	



	
	public static class BlockCustom extends Block {
		public BlockCustom() {
			super(Material.ROCK);
			setUnlocalizedName("quartz_ore_granite");
			setSoundType(SoundType.STONE);
			setHarvestLevel("pickaxe", 1);
			setHardness(5F);
			setResistance(15F);
			setCreativeTab(TabAstroTweaks.tab);
		}

		@Override
		public MapColor getMapColor(IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
			return MapColor.NETHERRACK;
		}

		@Override
		public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
			drops.add(new ItemStack(Items.QUARTZ, (int) (1)));
		}
	}
}
