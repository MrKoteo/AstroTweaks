package astrotweaks.tech.mt;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections; 
import java.util.ArrayList;
import java.util.List;

import astrotweaks.item.ATItems;
import astrotweaks.item.ItemGavel;

import astrotweaks.ModVariables;



public class MTConvert {
    private static final Map<Item, Item> UPGRADE_MAP = new HashMap<>();
    private static final Map<Item, Item> DOWNGRADE_MAP = new HashMap<>();
    private static boolean mapsInitialized = false;
    private static List<ItemStack> COPPER_PLATE_ORES = null;
    private static final Random RAND = new Random();

    private static boolean Money_Can_Craft;
    private static boolean Money_Can_Conversion;
    private static int ConvCount;

    public static void updVars() { 
        Money_Can_Craft = ModVariables.Money_Can_Craft;
        Money_Can_Conversion = ModVariables.Money_Can_Conversion;
        ConvCount = ModVariables.Money_ConvCount;
    }



    

    public MTConvert() {}

    private static boolean isCopperPlate(ItemStack stack) {
        if (!Money_Can_Craft || stack == null || stack.isEmpty())  return false;

        ensureCopperPlateOres();
        for (ItemStack ore : COPPER_PLATE_ORES) {
            if (OreDictionary.itemMatches(ore, stack, false)) {
                return true;
            }
        }
        return false;
    }

    // Ensure the maps are filled when items are already registered -> avoid "null" keys
    private static void ensureMapsInitialized() {
        if (mapsInitialized) return;
        if (!Money_Can_Conversion) return;
        UPGRADE_MAP.put(ATItems.COPPER_COIN, ATItems.SILVER_COIN);
        UPGRADE_MAP.put(ATItems.SILVER_COIN, ATItems.GOLD_COIN);
        UPGRADE_MAP.put(ATItems.GOLD_COIN, ATItems.PLATINUM_COIN);
        UPGRADE_MAP.put(ATItems.PLATINUM_COIN, ATItems.DIAMANT_COIN);
        UPGRADE_MAP.put(ATItems.DIAMANT_COIN, ATItems.PALLADIUM_COIN);
        UPGRADE_MAP.put(ATItems.PALLADIUM_COIN, ATItems.ELUNITE_COIN);
        UPGRADE_MAP.put(ATItems.ELUNITE_COIN, ATItems.MYTHRIL_COIN);
        UPGRADE_MAP.put(ATItems.MYTHRIL_COIN, ATItems.ADAMANTIUM_COIN);
        UPGRADE_MAP.put(ATItems.ADAMANTIUM_COIN, ATItems.UNI_COIN);
        UPGRADE_MAP.put(ATItems.WOOD_COIN, ATItems.STONE_COIN);
        UPGRADE_MAP.put(ATItems.STONE_COIN, ATItems.COPPER_COIN);


        DOWNGRADE_MAP.put(ATItems.SILVER_COIN, ATItems.COPPER_COIN);
        DOWNGRADE_MAP.put(ATItems.GOLD_COIN, ATItems.SILVER_COIN);
        DOWNGRADE_MAP.put(ATItems.PLATINUM_COIN, ATItems.GOLD_COIN);
        DOWNGRADE_MAP.put(ATItems.DIAMANT_COIN, ATItems.PLATINUM_COIN);
        DOWNGRADE_MAP.put(ATItems.PALLADIUM_COIN, ATItems.DIAMANT_COIN);
        DOWNGRADE_MAP.put(ATItems.ELUNITE_COIN, ATItems.PALLADIUM_COIN);
        DOWNGRADE_MAP.put(ATItems.MYTHRIL_COIN, ATItems.ELUNITE_COIN);
        DOWNGRADE_MAP.put(ATItems.ADAMANTIUM_COIN, ATItems.MYTHRIL_COIN);
        DOWNGRADE_MAP.put(ATItems.UNI_COIN, ATItems.ADAMANTIUM_COIN);
        DOWNGRADE_MAP.put(ATItems.STONE_COIN, ATItems.WOOD_COIN);
        DOWNGRADE_MAP.put(ATItems.COPPER_COIN, ATItems.STONE_COIN);


        mapsInitialized = true;
    }

    private static ItemStack getStack(IInventory inventory, int slot) {
        ItemStack stack = inventory.getStackInSlot(slot);
        return stack == null ? ItemStack.EMPTY : stack;
    }
    private static void setStack(IInventory inventory, int slot, ItemStack stack) {
        inventory.setInventorySlotContents(slot, stack);
        inventory.markDirty();
    }
    private static void decreaseSlot(IInventory inventory, int slot, int amount) {
        inventory.decrStackSize(slot, amount);
        inventory.markDirty();
    }


    // Helper that safely returns ItemStack.EMPTY instead of null
    private static ItemStack safeGetSlotItemStack(TileEntity inv, int slot) {
        if (inv instanceof TileEntityLockableLoot) {
            ItemStack s = ((TileEntityLockableLoot) inv).getStackInSlot(slot);
            return s == null ? ItemStack.EMPTY : s;
        }
        return ItemStack.EMPTY;
    }
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
    private static void damageGavel(IInventory inventory, int slot) {
        ItemStack stack = getStack(inventory, slot);
        if (stack.isEmpty())  return;

        if (stack.attemptDamageItem(1, RAND, null)) {
            stack.shrink(1);
            stack.setItemDamage(0);
        }

        setStack(inventory, slot, stack);
    }

    public static void exect(BlockPos pos, World world) {
        ensureMapsInitialized();

        TileEntity te = world.getTileEntity(pos);

        if (!(te instanceof TileEntityLockableLoot))  return;

        IInventory inventory = (IInventory) te;
        ItemStack gavelStack = getStack(inventory, 4);

        if (gavelStack.isEmpty() || gavelStack.getItem() != ItemGavel.GAVEL)  return;

        if (processUpgrade(inventory) || processDowngrade(inventory)) {
            damageGavel(inventory, 4);
        }
    }

    private static void ensureCopperPlateOres() {
        if (COPPER_PLATE_ORES != null) return;

        if (!Money_Can_Craft) {
            COPPER_PLATE_ORES = Collections.emptyList();
            return;
        }
        List<ItemStack> ores = OreDictionary.getOres("plateCopper");

        COPPER_PLATE_ORES = ores == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(ores));
    }

    private static boolean processUpgrade(IInventory inventory) {
        ItemStack input = getStack(inventory, 0);
        ItemStack output = getStack(inventory, 1);

        if (input.isEmpty())  return false;

        /*
        * Медная пластина -> медная монета
        */
        if (isCopperPlate(input)) {
            if (!output.isEmpty() && output.getItem() != ATItems.COPPER_COIN) 
                return false;

            if (!output.isEmpty() && output.getCount() >= output.getMaxStackSize()) 
                return false;


            decreaseSlot(inventory, 0, 1);

            if (output.isEmpty()) {
                setStack(inventory, 1, new ItemStack(ATItems.COPPER_COIN, 1));
            } else {
                output.grow(1);
                setStack(inventory, 1, output);
            }

            return true;
        }

        /*
        * Обычное повышение монеты
        */
        Item outputItem = UPGRADE_MAP.get(input.getItem());

        if (outputItem == null)  return false;
        if (input.getCount() < ConvCount)  return false;
        if (!output.isEmpty() && output.getItem() != outputItem)  return false;
        if (!output.isEmpty() && output.getCount() >= output.getMaxStackSize())  return false;

        decreaseSlot(inventory, 0, ConvCount);

        if (output.isEmpty()) {
            setStack(inventory, 1, new ItemStack(outputItem, 1));
        } else {
            output.grow(1);
            setStack(inventory, 1, output);
        }
        return true;
    }

    private static boolean processDowngrade(IInventory inventory) {
        ItemStack input = getStack(inventory, 2);
        ItemStack output = getStack(inventory, 3);

        if (input.isEmpty()) {
            return false;
        }

        Item outputItem = DOWNGRADE_MAP.get(input.getItem());

        if (outputItem == null)  return false;
        if (!output.isEmpty() && output.getItem() != outputItem)  return false;
        if (!output.isEmpty() && output.getCount() + ConvCount > output.getMaxStackSize())  return false;

        decreaseSlot(inventory, 2, 1);

        if (output.isEmpty()) {
            int count = Math.min(ConvCount, new ItemStack(outputItem, 1).getMaxStackSize());
            setStack(inventory, 3, new ItemStack(outputItem, count));
        } else {
            output.grow(ConvCount);
            setStack(inventory, 3, output);
        }
        return true;
    }
}
