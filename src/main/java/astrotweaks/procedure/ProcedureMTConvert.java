package astrotweaks.procedure;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections; 
import java.util.ArrayList;
import java.util.List;

import astrotweaks.item.ItemWoodCoin;
import astrotweaks.item.ItemUniCoin;
import astrotweaks.item.ItemStoneCoin;
import astrotweaks.item.ItemSilverCoin;
import astrotweaks.item.ItemPlatinumCoin;
import astrotweaks.item.ItemPalladiumCoin;
import astrotweaks.item.ItemMythrilCoin;
import astrotweaks.item.ItemGoldCoin;
import astrotweaks.item.ItemGavel;
import astrotweaks.item.ItemEluniteCoin;
import astrotweaks.item.ItemDiamantCoin;
import astrotweaks.item.ItemCopperPlate;
import astrotweaks.item.ItemCopperCoin;
import astrotweaks.item.ItemAdamantiumCoin;

import astrotweaks.ElementsAstrotweaksMod;
import astrotweaks.AstrotweaksModVariables;

@ElementsAstrotweaksMod.ModElement.Tag
public class ProcedureMTConvert extends ElementsAstrotweaksMod.ModElement {
    private static final Map<Item, Item> UPGRADE_MAP = new HashMap<>();
    private static final Map<Item, Item> DOWNGRADE_MAP = new HashMap<>();
    private static boolean mapsInitialized = false;
    private static List<ItemStack> COPPER_PLATE_ORES = null;
    private static final Random RAND = new Random();

    public ProcedureMTConvert(ElementsAstrotweaksMod instance) { super(instance, 321); }

	private static boolean isCopperPlate(ItemStack stack) {
	    if (stack.isEmpty()) return false;
	    ensureCopperPlateOres();
	    // use OreDictionary.itemMatches
	    for (ItemStack ore : COPPER_PLATE_ORES) {
	        if (OreDictionary.itemMatches(ore, stack, false)) return true;
	    }
	    return false;
	}

    // Ensure the maps are filled when items are already registered -> avoid "null" keys
    private static void ensureMapsInitialized() {
        if (mapsInitialized) return;
        UPGRADE_MAP.put(ItemCopperCoin.block, ItemSilverCoin.block);
        UPGRADE_MAP.put(ItemSilverCoin.block, ItemGoldCoin.block);
        UPGRADE_MAP.put(ItemGoldCoin.block, ItemPlatinumCoin.block);
        UPGRADE_MAP.put(ItemPlatinumCoin.block, ItemDiamantCoin.block);
        UPGRADE_MAP.put(ItemDiamantCoin.block, ItemPalladiumCoin.block);
        UPGRADE_MAP.put(ItemPalladiumCoin.block, ItemEluniteCoin.block);
        UPGRADE_MAP.put(ItemEluniteCoin.block, ItemMythrilCoin.block);
        UPGRADE_MAP.put(ItemMythrilCoin.block, ItemAdamantiumCoin.block);
        UPGRADE_MAP.put(ItemAdamantiumCoin.block, ItemUniCoin.block);
        UPGRADE_MAP.put(ItemWoodCoin.block, ItemStoneCoin.block);
        UPGRADE_MAP.put(ItemStoneCoin.block, ItemCopperCoin.block);


        DOWNGRADE_MAP.put(ItemSilverCoin.block, ItemCopperCoin.block);
        DOWNGRADE_MAP.put(ItemGoldCoin.block, ItemSilverCoin.block);
        DOWNGRADE_MAP.put(ItemPlatinumCoin.block, ItemGoldCoin.block);
        DOWNGRADE_MAP.put(ItemDiamantCoin.block, ItemPlatinumCoin.block);
        DOWNGRADE_MAP.put(ItemPalladiumCoin.block, ItemDiamantCoin.block);
        DOWNGRADE_MAP.put(ItemEluniteCoin.block, ItemPalladiumCoin.block);
        DOWNGRADE_MAP.put(ItemMythrilCoin.block, ItemEluniteCoin.block);
        DOWNGRADE_MAP.put(ItemAdamantiumCoin.block, ItemMythrilCoin.block);
        DOWNGRADE_MAP.put(ItemUniCoin.block, ItemAdamantiumCoin.block);
        DOWNGRADE_MAP.put(ItemStoneCoin.block, ItemWoodCoin.block);
        DOWNGRADE_MAP.put(ItemCopperCoin.block, ItemStoneCoin.block);

        mapsInitialized = true;
    }

    // Helper that safely returns ItemStack.EMPTY instead of null
    private static ItemStack safeGetSlotItemStack(TileEntity inv, int slot) {
        if (inv instanceof TileEntityLockableLoot) {
            ItemStack s = ((TileEntityLockableLoot) inv).getStackInSlot(slot);
            return s == null ? ItemStack.EMPTY : s;
        }
        return ItemStack.EMPTY;
    }
	/*
    private static int getSlotAmount(World world, BlockPos pos, int slot) {
        TileEntity inv = world.getTileEntity(pos);
        ItemStack stack = safeGetStack(inv, slot);
        return stack.isEmpty() ? 0 : stack.getCount();
    }*/
	/*
    private static ItemStack getSlotItemStack(World world, BlockPos pos, int slot) {
        TileEntity inv = world.getTileEntity(pos);
        return safeGetStack(inv, slot);
    }*/

    private static void setSlotItem(TileEntity te, int slot, ItemStack stack) {
        //TileEntity inv = world.getTileEntity(pos);
        if (te instanceof TileEntityLockableLoot) {
            ((TileEntityLockableLoot) te).setInventorySlotContents(slot, stack);
        }
    }
    private static void decreaseSlot(TileEntity te, int slot, int amount) {
        //TileEntity inv = world.getTileEntity(pos);
        if (te instanceof TileEntityLockableLoot) {
            ((TileEntityLockableLoot) te).decrStackSize(slot, amount);
        }
    }
    private static void damageGavel(TileEntity te, int slot) {
        //TileEntity inv = world.getTileEntity(pos);
        if (te instanceof TileEntityLockableLoot) {
            ItemStack stack = ((TileEntityLockableLoot) te).getStackInSlot(slot);
            if (stack != null && !stack.isEmpty()) {
                // Try to damage; when broken shrink the stack (same logic as your original)
                if (stack.attemptDamageItem(1, RAND, null)) {
                    stack.shrink(1);
                    stack.setItemDamage(0);
                }
                ((TileEntityLockableLoot) te).setInventorySlotContents(slot, stack);
            }
        }
    }
    public static void executeProcedure(Map<String, Object> dependencies) {
		String[] keys = {"x", "y", "z", "world"};
		for (String key : keys) {
		    if (dependencies.get(key) == null) {
		        System.err.println("Failed to load dependency " + key + "!");
		        return;
		    }
		}

        int x = (int) dependencies.get("x");
        int y = (int) dependencies.get("y");
        int z = (int) dependencies.get("z");
        World world = (World) dependencies.get("world");
        BlockPos pos = new BlockPos(x, y, z);

        // lazy init the maps here (after items have been registered)
        ensureMapsInitialized();

		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityLockableLoot)) return;
		
		ItemStack gavelStack = safeGetSlotItemStack(te, 4);
		if (gavelStack.isEmpty() || gavelStack.getItem() != ItemGavel.block) return;
		
		boolean did = false;
		if (canProcessUpgrade(te) && processUpgradeConversion(te)) {
		    did = true;
		} else if (canProcessDowngrade(te) && processDowngradeConversion(te)) {
		    did = true;
		}
		
		if (did) damageGavel(te, 4);

    }

	private static void ensureCopperPlateOres() {
	    if (COPPER_PLATE_ORES != null) return;
	    List<ItemStack> ores = OreDictionary.getOres("plateCopper");

	    COPPER_PLATE_ORES = ores == null ? Collections.emptyList() : new ArrayList<>(ores);
	}



    private static boolean canProcessUpgrade(TileEntity te) {
        //ItemStack slot0Stack = safeGetSlotItemStack(world, pos, 0);

		ItemStack slot0Stack = safeGetSlotItemStack(te, 0);
		ItemStack slot1Stack = safeGetSlotItemStack(te, 1);
		if (slot0Stack.isEmpty()) return false;

        if (isCopperPlate(slot0Stack)) { // check for CopperPlate
            if (slot1Stack.isEmpty()) return true;
            if (slot1Stack.getItem() != ItemCopperCoin.block) return false;
            return slot1Stack.getCount() + 1 <= slot1Stack.getMaxStackSize();
        }

	    Item inputItem = slot0Stack.getItem();
	    Item outputItem = UPGRADE_MAP.get(inputItem);
	    if (outputItem == null) return false;

        int required = 10;
        if (slot0Stack.getCount() < required) return false;


        if (slot1Stack.isEmpty()) return true;
        if (slot1Stack.getItem() != outputItem) return false;
        return slot1Stack.getCount() + 1 <= slot1Stack.getMaxStackSize();
    }
    private static boolean processUpgradeConversion(TileEntity te) {
        ItemStack slot0Stack = safeGetSlotItemStack(te, 0);
        if (slot0Stack.isEmpty()) return false;

		if (isCopperPlate(slot0Stack)) {
            // rem 1 copper plate
            decreaseSlot(te, 0, 1);
            
            // add 1 copper coin
            ItemStack outStack = safeGetSlotItemStack(te, 1);
            int newCount = (outStack.isEmpty() ? 0 : outStack.getCount()) + 1;
            int max = new ItemStack(ItemCopperCoin.block).getMaxStackSize();
            if (newCount > max) newCount = max;
            setSlotItem(te, 1, new ItemStack(ItemCopperCoin.block, newCount));
            return true;
        }
        
        Item inputItem = slot0Stack.getItem();
        Item outputItem = UPGRADE_MAP.get(inputItem);
        if (outputItem == null) return false;

        int removeCount = 10;
        decreaseSlot(te, 0, removeCount);

        ItemStack outStack = safeGetSlotItemStack(te, 1);
        int newCount = (outStack.isEmpty() ? 0 : outStack.getCount()) + 1;
        int max = (outStack.isEmpty() ? outputItem.getItemStackLimit(new ItemStack(outputItem, 1)) : outStack.getMaxStackSize());
        if (newCount > max) newCount = max;
        //setSlotItem(te, 1, new ItemStack(outputItem, newCount));
		if (!outStack.isEmpty() && outStack.getItem() == outputItem) {
		    outStack.setCount(newCount);
		    setSlotItem(te, 1, outStack);
		} else {
		    setSlotItem(te, 1, new ItemStack(outputItem, newCount));
		}
        
        return true;
    }

    private static boolean canProcessDowngrade(TileEntity te) {
        ItemStack slot2Stack = safeGetSlotItemStack(te, 2);
        ItemStack slot3Stack = safeGetSlotItemStack(te, 3);
        if (slot2Stack.isEmpty()) return false;

        Item inputItem = slot2Stack.getItem();
        Item outputItem = DOWNGRADE_MAP.get(inputItem);
        if (outputItem == null) return false;

        //ItemStack slot3Stack = safeGetSlotItemStack(world, pos, 3);
        if (slot3Stack.isEmpty()) return true;
        if (slot3Stack.getItem() != outputItem) return false;
        return slot3Stack.getCount() + 10 <= slot3Stack.getMaxStackSize();
    }
    private static boolean processDowngradeConversion(TileEntity te) {
        ItemStack slot2Stack = safeGetSlotItemStack(te, 2);
        if (slot2Stack.isEmpty()) return false;
        Item inputItem = slot2Stack.getItem();
        Item outputItem = DOWNGRADE_MAP.get(inputItem);
        if (outputItem == null) return false;

        decreaseSlot(te, 2, 1);

        ItemStack outStack = safeGetSlotItemStack(te, 3);
        int newCount = (outStack.isEmpty() ? 0 : outStack.getCount()) + 10;
        int max = (outStack.isEmpty() ? outputItem.getItemStackLimit(new ItemStack(outputItem, 1)) : outStack.getMaxStackSize());
        if (newCount > max) newCount = max;
        //setSlotItem(te, 3, new ItemStack(outputItem, newCount));
		if (!outStack.isEmpty() && outStack.getItem() == outputItem) {
		    outStack.setCount(newCount);
		    setSlotItem(te, 3, outStack);
		} else {
		    setSlotItem(te, 3, new ItemStack(outputItem, newCount));
		}
        
        return true;
    }
}