package astrotweaks.block;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.oredict.OreDictionary;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.NonNullList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.SoundType;
import net.minecraft.block.Block;

import astrotweaks.creativetab.TabAstroTweaks;
import astrotweaks.ElementsAstrotweaksMod;

import java.util.Random;
import java.util.List;

import astrotweaks.util.DropHandler;
import astrotweaks.util.DropHandler.DropEntry;
import net.minecraft.init.Items;


@ElementsAstrotweaksMod.ModElement.Tag
public class BlockDeepMinerals extends ElementsAstrotweaksMod.ModElement {
    @GameRegistry.ObjectHolder("astrotweaks:deep_minerals")
    public static final Block block = null;

    public BlockDeepMinerals(ElementsAstrotweaksMod instance) {
        super(instance, 696);
    }

    @Override
    public void initElements() {
        elements.blocks.add(() -> new BlockCustom().setRegistryName("deep_minerals"));
        elements.items.add(() -> new ItemBlock(block).setRegistryName(block.getRegistryName()));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
                new ModelResourceLocation("astrotweaks:deep_minerals", "inventory"));
    }

    public static class BlockCustom extends Block {
        /*
        private static final class DropEntry {
            final String oreDict;
            final int quantity;
            final double weight;

            DropEntry(String oreDict, int quantity, double weight) {
                this.oreDict = oreDict;
                this.quantity = quantity;
                this.weight = weight;
            }
        }*/

        public BlockCustom() {
            super(Material.ROCK);
            setUnlocalizedName("deep_minerals");
            setSoundType(SoundType.STONE);
            setHarvestLevel("pickaxe", 3);
            setHardness(7.5F);
            setResistance(20F);
            setCreativeTab(TabAstroTweaks.tab);
        }
        @Override public EnumPushReaction getMobilityFlag(IBlockState state) { return EnumPushReaction.IGNORE; }

        // immutable array of possible drops
        private static final DropEntry[] DE_TABLE = new DropEntry[] {
            new DropEntry("oreCopper",    2, 10.0),
            new DropEntry("oreSilver",    2, 10.0),
            new DropEntry("oreGold",      2, 11.0),
            new DropEntry("oreIron",      2, 12.0),
            new DropEntry("oreLapis",     2, 10.0),
            new DropEntry("oreRedstone",  2, 10.0),
            new DropEntry("oreLead",      2, 10.0),
            new DropEntry("oreRuby",      2,  8.0),
            new DropEntry("oreTin",       2, 10.0),
            new DropEntry("oreUranium",   2, 10.0),
            new DropEntry("oreThorium",   2, 10.0),
            new DropEntry("oreCobalt",    2, 10.0),
            new DropEntry("oreNickel",    2, 10.0),
            new DropEntry("orePlatinum",  2, 10.0),
            new DropEntry("oreBoron",     2, 10.0),
            new DropEntry("oreTitanium",  2, 11.0),
            new DropEntry("oreNiobium",   2, 10.0),
            new DropEntry("dustTungsten", 2, 10.0),
            // new DropEntry("orePalladium", 2, 10.0),
        };
		private static final DropHandler DE_DROP_TABLE = new DropHandler(DE_TABLE);
		@Override public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		    World w = (world instanceof World) ? (World) world : null;

		    int rolls = (w != null) ? w.rand.nextInt(3) + 2 /* 2..4 */: new java.util.Random().nextInt(3) + 2;
		    DE_DROP_TABLE.generateDrops(drops, w, rolls);
		}
    }
}
