package astrotweaks.util;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraft.world.World;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;


// Use:  
public final class DropHandler {
    public static final class DropEntry {
        public final String source; // "none" | "ore:oreName" | "modid:item" | "oreName" (ore by default)
        public final int quantity;
        public final double weight;

        public DropEntry(String source, int quantity, double weight) {
            this.source = source;
            this.quantity = quantity;
            this.weight = weight;
        }
    }
    private final DropEntry[] entries;
    public DropHandler(DropEntry[] entries) {
        this.entries = entries != null ? entries.clone() : new DropEntry[0];
    }
	/**
	* Generates drops (adds ItemStack to 'out').
	*
	* @param out the resulting list (NonNullList<ItemStack> can be passed as a List)
	* @param world IBlockAccess is usually World - used only for RNG; it can be null
    * @param rolls the number of rolls; if you need a random number from min..max, call the appropriate rand from the outside
    */
    public void generateDrops(List<ItemStack> out, World world, int rolls) {
        final Random rand = (world != null) ? world.rand : new Random();
        // Preparation of arrays of valid records and their cumulative weights
        int n = entries.length;
        DropEntry[] avail = new DropEntry[n];
        double[] cum = new double[n];
        int count = 0;
        double total = 0.0;

        for (int i = 0; i < n; i++) {
            DropEntry e = entries[i];
            if (e == null) continue;
            if (e.weight <= 0.0) continue;
            // Check availability: either "none" (always available), or OreDictionary is not empty or Item exists
            if (isSourceAvailable(e.source)) {
                total += e.weight;
                avail[count] = e;
                cum[count] = total;
                count++;
            }
        }

        if (count == 0 || total <= 0.0) return;

        for (int r = 0; r < rolls; r++) {
            double pick = rand.nextDouble() * total;
            int idx = 0;
            while (idx < count && pick >= cum[idx]) idx++;
            if (idx >= count) idx = count - 1;

            DropEntry chosen = avail[idx];
            // Empty result ("none")
            if (chosen.source.equalsIgnoreCase("none")) {
                continue;
            }

            // OreDictionary (if specified as "ore:NAME" or without prefix and contains "ore" at the beginning)
            if (isOreLookup(chosen.source)) {
                String oreName = stripOrePrefix(chosen.source);
                List<ItemStack> ores = OreDictionary.getOres(oreName);
                if (!ores.isEmpty()) {
                    ItemStack stack = ores.get(0).copy();
                    stack.setCount(Math.max(1, chosen.quantity));
                    out.add(stack);
                }
            } else {
                // Attempt to find an Item by registry name
                Item item = Item.getByNameOrId(chosen.source);
                if (item != null && item != Items.AIR) {
                    ItemStack stack = new ItemStack(item, Math.max(1, chosen.quantity));
                    out.add(stack);
                } else {
                    // final attempt - if the record looks like oreName (without prefix), try OreDictionary
                    List<ItemStack> ores = OreDictionary.getOres(chosen.source);
                    if (!ores.isEmpty()) {
                        ItemStack stack = ores.get(0).copy();
                        stack.setCount(Math.max(1, chosen.quantity));
                        out.add(stack);
                    }
                }
            }
        }
    }

    private static boolean isOreLookup(String src) {
        if (src == null) return false;
        String s = src.toLowerCase();
        return s.startsWith("ore:") || !s.contains(":") /* no ':' - likely oredict name */;
    }

    private static String stripOrePrefix(String src) {
        if (src == null) return src;
        if (src.toLowerCase().startsWith("ore:")) return src.substring(4);
        return src;
    }

    private static boolean isSourceAvailable(String src) {
        if (src == null) return false;
        if (src.equalsIgnoreCase("none")) return true;
        if (isOreLookup(src)) {
            String ore = stripOrePrefix(src);
            return !OreDictionary.getOres(ore).isEmpty();
        } else {
            Item item = Item.getByNameOrId(src);
            if (item != null && item != Items.AIR) return true;
            // fallback: maybe it's an oredict name without ':'
            return !OreDictionary.getOres(src).isEmpty();
        }
    }
}
