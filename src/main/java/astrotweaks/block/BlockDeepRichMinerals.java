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
import net.minecraft.init.Items;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.SoundType;
import net.minecraft.block.Block;

import astrotweaks.creativetab.TabAstroTweaks;
import astrotweaks.ElementsAstrotweaksMod;

import astrotweaks.util.DropHandler;
import astrotweaks.util.DropHandler.DropEntry;
import net.minecraft.init.Items;


import java.util.Random;
import java.util.List;
import java.util.ArrayList;

@ElementsAstrotweaksMod.ModElement.Tag
public class BlockDeepRichMinerals extends ElementsAstrotweaksMod.ModElement {
    @GameRegistry.ObjectHolder("astrotweaks:deep_rich_minerals")
    public static final Block block = null;

    public BlockDeepRichMinerals(ElementsAstrotweaksMod instance) {
        super(instance, 700);
    }

    @Override
    public void initElements() {
        elements.blocks.add(() -> new BlockCustom().setRegistryName("deep_rich_minerals"));
        elements.items.add(() -> new ItemBlock(block).setRegistryName(block.getRegistryName()));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
                new ModelResourceLocation("astrotweaks:deep_rich_minerals", "inventory"));
    }

    public static class BlockCustom extends Block {
    	/*
        private static class DropEntry {
            final String oreDict;
            final int quantity;
            final double weight; // chance

            DropEntry(String oreDict, int quantity, double weight) {
                this.oreDict = oreDict;
                this.quantity = quantity;
                this.weight = weight;
            }
        }*/

        // list of drops
        /*
        private static final List<DropEntry> DROP_LIST = new ArrayList<>();
        static {
            DROP_LIST.add(new DropEntry("oreDiamond", 	4, 20.0));
            DROP_LIST.add(new DropEntry("oreEmerald", 	4,  9.0));
            DROP_LIST.add(new DropEntry("oreGold",   	4, 20.0));
			DROP_LIST.add(new DropEntry("oreIron", 		4, 11.0));
			DROP_LIST.add(new DropEntry("oreLapis", 	4,  9.0));
			DROP_LIST.add(new DropEntry("oreUranium", 	4, 15.0));
			DROP_LIST.add(new DropEntry("oreThorium", 	4, 15.0));
			DROP_LIST.add(new DropEntry("oreTitanium",  4, 14.0));
			DROP_LIST.add(new DropEntry("oreNiobium",   4, 12.0));
			DROP_LIST.add(new DropEntry("dustTungsten", 4, 12.0));
			
            
        } */

        private static final astrotweaks.util.DropHandler.DropEntry[] DE_TABLE = new astrotweaks.util.DropHandler.DropEntry[] {
            new DropEntry("oreDiamond", 	4, 20.0),
            new DropEntry("oreEmerald", 	4,	9.0),
            new DropEntry("oreGold",   		4, 20.0),
			new DropEntry("oreIron", 		4, 11.0),
			new DropEntry("oreLapis", 		4,	9.0),
			new DropEntry("oreUranium", 	4, 15.0),
			new DropEntry("oreThorium", 	4, 15.0),
			new DropEntry("oreTitanium",	4, 14.0),
			new DropEntry("oreNiobium",		4, 12.0),
			new DropEntry("dustTungsten",	4, 12.0),
        };



        public BlockCustom() {
            super(Material.IRON);
            setUnlocalizedName("deep_rich_minerals");
            setSoundType(SoundType.STONE);
            setHarvestLevel("pickaxe", 3);
            setHardness(60F);
            setResistance(50F);
            //setLightLevel(0F);
            //setLightOpacity(255);
            setCreativeTab(TabAstroTweaks.tab);
        }
        @Override public EnumPushReaction getMobilityFlag(IBlockState state) {return EnumPushReaction.IGNORE;}

		private static final DropHandler DE_DROP_TABLE = new DropHandler(DE_TABLE);
		@Override public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		    World w = (world instanceof World) ? (World) world : null;

		    int rolls = (w != null) ? w.rand.nextInt(4) + 2 /* 2..5 */: new java.util.Random().nextInt(4) + 2;
		    DE_DROP_TABLE.generateDrops(drops, w, rolls);
		}
    }
}