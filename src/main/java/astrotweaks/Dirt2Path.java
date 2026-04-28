package astrotweaks.tweaks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import astrotweaks.ModVariables;

@Mod.EventBusSubscriber
public final class Dirt2Path {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockRightclick(PlayerInteractEvent.RightClickBlock event) {
        if (event.isCanceled() || event.getResult() == Event.Result.DENY) return;

        EntityPlayer player = event.getEntityPlayer();
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        EnumHand hand = event.getHand();
        ItemStack held = player.getHeldItem(hand);
        // Early returns and safe checks (#1, #10)
        if (held.isEmpty()) return;
        if (!isShovel(held)) return;
        if (world.getBlockState(pos.up()).getMaterial() != Material.AIR) return;
        if (!player.canPlayerEdit(pos, event.getFace(), held)) return;

        IBlockState state = world.getBlockState(pos);

        boolean ips = player.isSneaking();
        if (!ips) return;

        if (isDirtLike(state)) {
            IBlockState pathState = getPathBlockState(state, held);
            applyChange(world, pos, pathState, SoundEvents.ITEM_SHOVEL_FLATTEN, player, held, hand, event);
            return;
        }
        if (isBlockPath(state)) {
            IBlockState dirtState = getDirtBlockState(state);
            applyChange(world, pos, dirtState, SoundEvents.ITEM_HOE_TILL, player, held, hand, event);
            return;
        }
    }
    // Safe tool-class check (#1)
    protected static boolean isShovel(ItemStack stack) {
        Item item = stack.getItem();
        Set<String> classes = item.getToolClasses(stack);
        return classes != null && (classes.contains("shovel") || classes.contains("spade"));
    }
    /**
     * Apply the block change + sound + swing + damage.
     * Sound and block change done on server only so clients don't double-play sound.
     * Swing is executed on client for immediate feedback.
     * Cancels the event and marks success on server after change (#2, #3).
     */
    protected static void applyChange(World world, BlockPos pos, IBlockState newState, SoundEvent soundEvent, EntityPlayer player, ItemStack stack, EnumHand hand, PlayerInteractEvent event) {
        if (world.isRemote) {
            // client: visual feedback
            player.swingArm(hand);
            return;
        }
        // Server: play sound for all clients, set block, damage item (not in creative)
        world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.setBlockState(pos, newState, 11);
        if (!player.capabilities.isCreativeMode) {
            stack.damageItem(1, player);
        }
        // cancel event so other handlers don't override it and set success (#3)
        event.setCanceled(true);
        event.setResult(Event.Result.ALLOW);
    }
    protected static boolean isBlockPath(IBlockState state) {
        return state.getBlock() == Blocks.GRASS_PATH;
    }
    protected static boolean isDirtLike(IBlockState state) {
        Block b = state.getBlock();

		if (b == Blocks.DIRT || b == Blocks.MYCELIUM || b == Blocks.FARMLAND) return true;

        return false;
    }
    protected static IBlockState getDirtBlockState(IBlockState current) { return Blocks.DIRT.getDefaultState(); }
    protected static IBlockState getPathBlockState(IBlockState current, ItemStack stack) { return Blocks.GRASS_PATH.getDefaultState(); }

}
