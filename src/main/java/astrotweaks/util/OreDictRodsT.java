/*
package astrotweaks.oredict;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import astrotweaks.item.*;
import astrotweaks.ElementsAstrotweaksMod;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@ElementsAstrotweaksMod.ModElement.Tag
public class OreDictRodsT extends ElementsAstrotweaksMod.ModElement {

    public OreDictRodsT(ElementsAstrotweaksMod instance) {
        super(instance, 486);
    }

    private enum RodMaterial {
        IRON("Iron", ItemIronStick.block),
        GOLD("Gold", ItemGoldStick.block),
        COPPER("Copper", ItemCopperStick.block),
        TIN("Tin", ItemTinStick.block),
        BRONZE("Bronze", ItemBronzeStick.block),
        DIAMOND("Diamond", ItemDiamondStick.block),
        ALUMINIUM("Aluminium", ItemAluminiumStick.block),
        TITANIUM("Titanium", ItemTitaniumStick.block),
        NICKEL("Nickel", ItemNickelStick.block),
        COBALT("Cobalt", ItemCobaltStick.block),
        METEORIC("MeteoricIron", ItemMeteoricStick.block),
        ELECTRUM("Electrum", ItemElectrumStick.block),
        EMERALD("Emerald", ItemEmeraldStick.block),
        RUBY("Ruby", ItemRubyStick.block),
        STEEL("Steel", ItemSteelStick.block),
        IRIDIUM("Iridium", ItemIridiumStick.block),
        SILVER("Silver", ItemSilverStick.block),
        URANIUM("Uranium", ItemUraniumStick.block),
        BRASS("Brass", ItemBrassStick.block),
        CARBON("Carbon", ItemCarbonStick.block);

        private final List<String> oreDictNames;
        private final ItemStack stack;

        RodMaterial(String oreKey, Item item, String... additionalOreKeys) {
            this.oreDictNames = new ArrayList<>();
            this.oreDictNames.add("rod" + oreKey);
            this.oreDictNames.addAll(Arrays.asList(additionalOreKeys));
            this.stack = new ItemStack(item, 1);
        }

        void register() {
            for (String name : oreDictNames) {
                OreDictionary.registerOre(name, stack);
            }
        }
    }

    @Override
    public void init(FMLInitializationEvent event) {
        for (RodMaterial mat : RodMaterial.values()) {
            mat.register();
        }
    }
}
*/