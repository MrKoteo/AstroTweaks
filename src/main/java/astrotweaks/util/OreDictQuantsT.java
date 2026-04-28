package astrotweaks.oredict;

import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;

import astrotweaks.item.*;
import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class OreDictQuantsT extends ElementsAstrotweaksMod.ModElement {
    public OreDictQuantsT(ElementsAstrotweaksMod instance) {
        super(instance, 513);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        registerQuantItems("singleQuant",
            ItemAlphaQuant.block, ItemBetaQuant.block, ItemGammaQuant.block, ItemDeltaQuant.block,
            ItemStrangeQuant.block,
            ItemNullQuant.block
        );

        //registerQuantItems("doubleQuant",
            //ItemDualAAQuant.block,
            //ItemDualABQuant.block,
            //ItemDualAGQuant.block,
            //ItemDualADQuant.block,
            //ItemDualBBQuant.block,
            //ItemDualBGQuant.block,
            //ItemDualBDQuant.block,
            //ItemDualGGQuant.block,
            //ItemDualGDQuant.block,
            //ItemDualDDQuant.block,
            //ItemDoubleNullQuant.block
        //);

        registerQuantItems("tripleQuant",
            /*
            ItemTripleAABQuant.block,
            ItemTripleAAGQuant.block,
            ItemTripleAADQuant.block,
            ItemTripleABBQuant.block,
            ItemTripleAGGQuant.block,
            ItemTripleADDQuant.block,
            ItemTripleBBGQuant.block,
            ItemTripleBBDQuant.block,
            ItemTripleBGGQuant.block,
            ItemTripleBDDQuant.block,
            ItemTripleGGDQuant.block,
            ItemTripleGDDQuant.block,*/




			ItemTripleABGQuant.block,
            ItemTripleABDQuant.block,
            ItemTripleAGDQuant.block,
			ItemTripleBGDQuant.block

			//ItemTripleAAAQuant.block,
			//ItemTripleBBBQuant.block,
			//ItemTripleGGGQuant.block,
            //ItemTripleDDDQuant.block,
            //ItemTripleNullQuant.block
        );

        registerQuantItems("quadQuant",
            /*
            ItemQuadAAABQuant.block,
            ItemQuadAAAGQuant.block,
            ItemQuadAAADQuant.block,
            ItemQuadAABBQuant.block,
            ItemQuadAABGQuant.block,
            ItemQuadAABDQuant.block,
            ItemQuadAAGGQuant.block,
            ItemQuadAAGDQuant.block,
            ItemQuadAADDQuant.block,
            ItemQuadABBBQuant.block,
            ItemQuadABBGQuant.block,
            ItemQuadABBDQuant.block,
            ItemQuadABGGQuant.block,
            ItemQuadABDDQuant.block,
            ItemQuadAGGGQuant.block,
            ItemQuadAGGDQuant.block,
            ItemQuadAGDDQuant.block,
            ItemQuadADDDQuant.block,
            ItemQuadBBBGQuant.block,
            ItemQuadBBBDQuant.block,
            ItemQuadBBGGQuant.block,
            ItemQuadBBGDQuant.block,
            ItemQuadBBDDQuant.block,
            ItemQuadBGGGQuant.block,
            ItemQuadBGGDQuant.block,
            ItemQuadBGDDQuant.block,
            ItemQuadBDDDQuant.block,
            ItemQuadGGGDQuant.block,
            ItemQuadGGDDQuant.block,
            ItemQuadGDDDQuant.block,*/



            ItemQuadABGDQuant.block,
            ItemQuadAAAAQuant.block,
            ItemQuadBBBBQuant.block,
            ItemQuadGGGGQuant.block,
            ItemQuadDDDDQuant.block
            //ItemQuadNullQuant.block
        );
    }

    private void registerQuantItems(String oreName, Item... items) {
        for (Item item : items) {
            OreDictionary.registerOre(oreName, new ItemStack(item, 1));
        }
    }
}