package astrotweaks.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import astrotweaks.item.*;
import astrotweaks.block.*;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.IFuelHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public class CombinedFuelHandler implements IFuelHandler {
	public CombinedFuelHandler() {}
    private static class FuelEntry {
        final Item item;
        final int burnTime;
        final Integer meta; // null = any meta

        FuelEntry(Item item, int burnTime) {
            this(item, burnTime, null);
        }
        FuelEntry(Item item, int burnTime, Integer meta) {
            this.item = item;
            this.burnTime = burnTime;
            this.meta = meta;
        }
        boolean matches(ItemStack stack) {
            if (stack == null) return false;
            if (stack.getItem() != item) return false;
            if (meta == null) return true;
            return stack.getMetadata() == meta;
        }
    }

    private static final FuelEntry[] FUEL_ENTRIES = {
    	// Format: Item, (meta), burnTime
        new FuelEntry(Item.getItemFromBlock(Blocks.DOUBLE_PLANT), 40, 2),

        new FuelEntry(Item.getItemFromBlock(Blocks.DOUBLE_PLANT), 40),
        new FuelEntry(Items.FEATHER, 20),
        new FuelEntry(ItemPlantFiber.block, 40),
        new FuelEntry(ItemCordageFiber.block, 60),
        new FuelEntry(ItemDrawing.block, 60),
        new FuelEntry(Items.STRING, 20),
        new FuelEntry(Items.LEATHER, 50),
        new FuelEntry(Items.RABBIT_HIDE, 40),
        new FuelEntry(Item.getItemFromBlock(Blocks.LEAVES), 40),
        new FuelEntry(Item.getItemFromBlock(Blocks.TALLGRASS), 20),
        new FuelEntry(Item.getItemFromBlock(Blocks.TORCH), 200),
        new FuelEntry(Item.getItemFromBlock(Blocks.LEVER), 80),
        new FuelEntry(Items.SADDLE, 300),
        new FuelEntry(Items.BREAD, 100),
        new FuelEntry(Items.WHEAT, 50),
        new FuelEntry(Items.NAME_TAG, 40),
        new FuelEntry(Items.ARROW, 30),
        new FuelEntry(Items.TIPPED_ARROW, 30),
        new FuelEntry(ItemScheme.block, 100),
        new FuelEntry(Item.getItemFromBlock(Blocks.DEADBUSH), 100),
        new FuelEntry(Items.PAINTING, 100),
        new FuelEntry(Items.ITEM_FRAME, 100),
        new FuelEntry(Item.getItemFromBlock(Blocks.HAY_BLOCK), 800),
        new FuelEntry(Items.BED, 1000),
        new FuelEntry(Items.PAPER, 40),
        new FuelEntry(Items.BOOK, 100),
        new FuelEntry(Items.WRITABLE_BOOK, 120),
        new FuelEntry(Items.WRITTEN_BOOK, 120),
        new FuelEntry(Items.MAP, 80),
        new FuelEntry(Items.FILLED_MAP, 80),
        new FuelEntry(Items.FIRE_CHARGE, 1200),
        new FuelEntry(Items.LEAD, 50),
        new FuelEntry(Items.LEATHER_HELMET, 300),
        new FuelEntry(Items.LEATHER_CHESTPLATE, 300),
        new FuelEntry(Items.LEATHER_LEGGINGS, 300),
        new FuelEntry(Items.LEATHER_BOOTS, 300),
        new FuelEntry(Items.SHIELD, 500),
        new FuelEntry(Items.BLAZE_POWDER, 1200),
        new FuelEntry(Item.getItemFromBlock(Blocks.WEB), 20),
        new FuelEntry(Items.ARMOR_STAND, 400),
        new FuelEntry(Items.CARROT_ON_A_STICK, 300),
        new FuelEntry(Item.getItemFromBlock(Blocks.REDSTONE_TORCH), 100),

		new FuelEntry(Item.getItemFromBlock(BlockBush1.block), 40),
		new FuelEntry(Item.getItemFromBlock(BlockBush2.block), 40),
		new FuelEntry(Item.getItemFromBlock(BlockBush3.block), 40),
		new FuelEntry(Item.getItemFromBlock(BlockBush4.block), 40),
		new FuelEntry(Item.getItemFromBlock(BlockBush5.block), 40),
		new FuelEntry(Item.getItemFromBlock(BlockBush6.block), 40),
		new FuelEntry(Item.getItemFromBlock(BlockBush7.block), 40),
		new FuelEntry(Item.getItemFromBlock(BlockFern1.block), 20),
	
        new FuelEntry(ItemStickBundle.block, 800),
        new FuelEntry(ItemSomeBlazeRods.block, 19200),
        new FuelEntry(ItemSomePaper.block, 320),
        new FuelEntry(ItemSomeBooks.block, 800),
        new FuelEntry(ItemSomeFeathers.block, 160),
        new FuelEntry(ItemSomeLeather.block, 400),
        new FuelEntry(ItemSomeArrows.block, 240),
        new FuelEntry(ItemSomeStrings.block, 160)
    };

    private final FuelEntry[] entries = FUEL_ENTRIES;

    public void postInit(FMLPostInitializationEvent event) {
        GameRegistry.registerFuelHandler(this);
        //MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public int getBurnTime(ItemStack fuel) {
        if (fuel == null) return 0;
        for (FuelEntry e : entries) {
            if (e.matches(fuel)) return e.burnTime;
        }
        return 0;
    }
}