package astrotweaks.world;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemTool;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.List;

//@Mod.EventBusSubscriber(modid = "astrotweaks")
public class RealisticBreak {
	private static final Set<Block> PICKAXE_ONLY_BLOCKS = new HashSet<>();
	private static final Set<Block> AXE_ONLY_BLOCKS = new HashSet<>();
	//private static final Set<Block> SHOVEL_ONLY_BLOCKS = new HashSet<>();
    private static final Set<Material> NO_HAND_MATERIALS;

    private static boolean initialized = false;
    private static int addedRulesCount = 0;

    static {
        HashSet<Material> noHand = new HashSet<>();
        noHand.add(Material.ROCK);
        noHand.add(Material.IRON);
        noHand.add(Material.SAND);
        noHand.add(Material.GROUND);
        noHand.add(Material.GRASS);
        NO_HAND_MATERIALS = Collections.unmodifiableSet(noHand);
    }
    public static void initFromStrings(String[] pickaxeRules, String[] axeRules/*, String[] shovelRules*/) {
        if (initialized) return;
        addedRulesCount = 0;
        // Parse and add to the appropriate sets
        addedRulesCount += parseAndAddToSet(pickaxeRules, PICKAXE_ONLY_BLOCKS);
        addedRulesCount += parseAndAddToSet(axeRules, AXE_ONLY_BLOCKS);
        //addedRulesCount += parseAndAddToSet(shovelRules, SHOVEL_ONLY_BLOCKS);

        Set<Block> pick = Collections.unmodifiableSet(new HashSet<>(PICKAXE_ONLY_BLOCKS));
        Set<Block> axe = Collections.unmodifiableSet(new HashSet<>(AXE_ONLY_BLOCKS));
        //Set<Block> shv = Collections.unmodifiableSet(new HashSet<>(SHOVEL_ONLY_BLOCKS));

        PICKAXE_ONLY_BLOCKS.clear(); PICKAXE_ONLY_BLOCKS.addAll(pick);
        AXE_ONLY_BLOCKS.clear(); AXE_ONLY_BLOCKS.addAll(axe);
        //SHOVEL_ONLY_BLOCKS.clear(); SHOVEL_ONLY_BLOCKS.addAll(shv);

		// Logging the number of added rules once
        FMLLog.info("RealBreak: added block rules: %d (including existing ones in the code)", addedRulesCount);
        initialized = true;
    }
    private static int parseAndAddToSet(String[] rules, Set<Block> targetSet) {
        if (rules == null || rules.length == 0) return 0;
        int added = 0;

        for (String raw : rules) {
            if (raw == null) continue;
            raw = raw.trim();
            if (raw.isEmpty()) continue;

            // ore:xxx -> use OreDictionary
            if (raw.startsWith("ore:")) {
                String oreName = raw.substring(4);
                if (oreName.isEmpty()) continue;
                List<ItemStack> ores = OreDictionary.getOres(oreName);
                if (ores == null || ores.isEmpty()) continue;
                for (ItemStack os : ores) {
                    if (os == null) continue;
                    Item item = os.getItem();
                    if (item == null) continue;
                    Block b = Block.getBlockFromItem(item);
                    if (b == null || b == Blocks.AIR) continue;
                    if (targetSet.add(b)) added++;
                }
                continue;
            }

            // Common registry name like "modid:name"
            ResourceLocation rl;
            try {
                rl = new ResourceLocation(raw);
            } catch (Exception e) {
                continue; // invalid format
            }

            Block reg = ForgeRegistries.BLOCKS.getValue(rl);
            if (reg == null || reg == Blocks.AIR) {
                // try Item (ItemBlock)
                continue; // just pass
            }
            if (targetSet.add(reg)) added++;
        }
        return added;
    }

	private static boolean isToolByClass(ItemStack stack, String toolClass) {
	    if (stack == null || stack.isEmpty()) return false;
	    try {
	        // Item#getToolClasses(ItemStack) -> Set<String>
	        Set<String> classes = stack.getItem().getToolClasses(stack);
	        return classes != null && classes.contains(toolClass);
	    } catch (NoSuchMethodError t) {
	        return false;
	    } catch (Throwable t) {
	        return false;
	    }
	}
	
	private static boolean isGenericTool(ItemStack stack) {
	    if (stack == null || stack.isEmpty()) return false;
	    Item item = stack.getItem();
	    if (item instanceof ItemTool) return true;
	    // Extra fallback: has least one tool class
	    try {
	        Set<String> classes = item.getToolClasses(stack);
	        return classes != null && !classes.isEmpty();
	    } catch (Throwable t) {
	        return false;
	    }
	}
	// HIGH priority for early solve
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBreakBlock(BlockEvent.BreakEvent event) {
	    EntityPlayer player = event.getPlayer();
	    if (player == null) return;
	    if (event.getState() == null) return;
	    if (player.capabilities.isCreativeMode) return;

	    Block block = event.getState().getBlock();
	    Material mat = event.getState().getMaterial();

	    ItemStack held = player.getHeldItemMainhand();
	    Item heldItem = (held != null && !held.isEmpty()) ? held.getItem() : null;

		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		/*
		try {
		    String itemName = (heldItem != null) ? String.valueOf(ForgeRegistries.ITEMS.getKey(heldItem)) : "none";
		    Set<String> classes = (held != null && !held.isEmpty()) ? held.getItem().getToolClasses(held) : Collections.emptySet();
		    FMLLog.info("RealBreak: player=%s held=%s toolClasses=%s block=%s material=%s",
		                player.getName(), itemName, classes, block.getRegistryName(), mat);
		} catch (Throwable t) {
		    FMLLog.info("RealBreak: failed to log tool info: %s", t.getMessage());
		}
		*/
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


	    if (NO_HAND_MATERIALS.contains(mat)) {
	        if (heldItem == null) { event.setCanceled(true); return; }
	    }

	    boolean needsPick = PICKAXE_ONLY_BLOCKS.contains(block);
	    boolean needsAxe  = AXE_ONLY_BLOCKS.contains(block);
	    //boolean needsShovel = SHOVEL_ONLY_BLOCKS.contains(block);
        if (!needsPick && !needsAxe/* && !needsShovel*/) return;
        if (heldItem == null) { event.setCanceled(true); return; }

	    if (mat == Material.GROUND || mat == Material.GRASS || mat == Material.SAND) {
	        if (isGenericTool(held)) return;
	        event.setCanceled(true);
	        return;
	    }

	    if (needsPick) {
	        if (isToolByClass(held, "pickaxe") || isToolByClass(held, "axe")) return;
	        // fallback: item canHarvestBlock
	        if (heldItem.canHarvestBlock(event.getState(), held)) return;
	        event.setCanceled(true);
	        return;
	    }
	    if (needsAxe) {
	        if (isToolByClass(held, "axe") || isToolByClass(held, "sword") || isToolByClass(held, "pickaxe")) return;
	        if (heldItem.canHarvestBlock(event.getState(), held)) return;
	        event.setCanceled(true);
	        return;
	    }
	    //if (needsShovel) {
	    //    //if (isToolByClass(held, "shovel")) return;
	    //    if (isGenericTool(held)) return;
	    //    if (heldItem.canHarvestBlock(event.getState(), held)) return;
	    //    event.setCanceled(true);
	    //}
    }
	private static void ExRules () {
		initFromStrings( 
			new String[] { // Pickaxe
				"ore:cobblestone","ore:stone","ore:blockConcrete","ore:hardenedClay","ore:blockMossy","ore:stoneBricks","ore:stoneSmooth","ore:blockGlazedTerracota",
				"ore:blockBrick","ore:netherrack","ore:endstone","astrotweaks:deepslate_bricks","ore:blockDiamond","ore:blockEmerald","ore:blockLapis",
				"minecraft:iron_trapdoor","minecraft:stone","minecraft:magma","minecraft:anvil",
				"ore:stoneDiorite","ore:stoneAndesite","ore:stoneGranite"
			},
			new String[] { // Axe
				"ore:logWood"
			}//,
			//new String[] { // Shovel
			//	"ore:dirt","ore:gravel","ore:grass","minecraft:mycelium","minecraft:soul_sand"
			//}
		);
	}
    public void postInit(FMLPostInitializationEvent event) {
		ExRules();
        MinecraftForge.EVENT_BUS.register(new RealisticBreak());
    }
}
