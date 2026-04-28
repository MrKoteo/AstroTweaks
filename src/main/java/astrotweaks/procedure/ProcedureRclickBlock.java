package astrotweaks.procedure;

import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
//import java.util.function.Predicate;
//import java.util.function.BiConsumer;
import java.util.concurrent.ThreadLocalRandom;

import astrotweaks.item.ItemRockFlat;
import astrotweaks.item.ItemRock;
import astrotweaks.item.ItemCordageVine;
import astrotweaks.item.ItemCordageFiber;
import astrotweaks.item.ItemBoneShard;
import astrotweaks.item.ItemFlintShard;


import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class ProcedureRclickBlock extends ElementsAstrotweaksMod.ModElement {
    public ProcedureRclickBlock(ElementsAstrotweaksMod instance) {
        super(instance, 326);
    }
	private static final List<Rule> RULES = buildRules();
	private interface Condition { boolean matches(Context ctx); }
	private interface Action { void apply(Context ctx, double rand); }

    private static class Context {
	    public final Entity entity;
	    public final World world;
	    public final int x, y, z;
	    public final ItemStack mainHand, offHand;
	    public final net.minecraft.block.Block blockAt, blockAbove;

	    public Context(Entity entity, World world, int x, int y, int z) {
	        this.entity = entity;
	        this.world = world;
	        this.x = x; this.y = y; this.z = z;
	        this.mainHand = (entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHeldItemMainhand() : ItemStack.EMPTY;
	        this.offHand = (entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHeldItemOffhand() : ItemStack.EMPTY;
	        BlockPos pos = new BlockPos(x, y, z);
	        this.blockAt = world.getBlockState(pos).getBlock();
	        this.blockAbove = world.getBlockState(pos.up()).getBlock();
	    }

	    public boolean isPlayer() { return entity instanceof EntityPlayer; }
	
	    public void giveToPlayer(ItemStack stack) {
	        if (isPlayer()) ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entity, stack);
	    }
		public void removeOneMatching(net.minecraft.item.Item item) {
		    if (isPlayer()) ((EntityPlayer) entity).inventory.clearMatchingItems(item, -1, 1, null);
		}
	    

	    public void spawnItemStack(ItemStack stack) {
	        if (!world.isRemote) {
	            EntityItem e = new EntityItem(world, x + 0.5, y + 1.5, z + 0.5, stack);
	            e.setPickupDelay(10);
	            world.spawnEntity(e);
	        }
	    }
    }

	private static class Rule {
	    final Condition condition;
	    final Action action;
	    Rule(Condition c, Action a) { this.condition = c; this.action = a; }
	}

    private static List<Rule> buildRules() {
        List<Rule> rules = new ArrayList<>();

        // Vine -> fiber: if holding vine (main or off) and block is magma/fire/lava above or dimension == -1
		rules.add(new Rule(ctx -> {
		    boolean holdingVine = ctx.mainHand.getItem() == ItemCordageVine.block || ctx.offHand.getItem() == ItemCordageVine.block;
		    boolean hotBelow = ctx.blockAt == Blocks.MAGMA || ctx.blockAt == Blocks.FIRE;
		    boolean hotAbove = ctx.blockAbove == Blocks.FLOWING_LAVA || ctx.blockAbove == Blocks.LAVA;
		    boolean inNether = ctx.entity.dimension == -1;
		    return holdingVine && (hotBelow || hotAbove || inNether);
		}, (ctx, r) -> {
            ctx.removeOneMatching(ItemCordageVine.block);
            ctx.giveToPlayer(new ItemStack(ItemCordageFiber.block, 1));
        }));

        // Rock -> flat rock (stone/cobble/various stone metas/obsidian)
		rules.add(new Rule(ctx -> {
		    boolean holdingRock = ctx.mainHand.getItem() == ItemRock.block;
		    boolean blockOk = ctx.blockAt == Blocks.STONE || ctx.blockAt == Blocks.COBBLESTONE || ctx.blockAt == Blocks.OBSIDIAN;
		    return holdingRock && blockOk;
		}, (ctx, r) -> {
            if (r > 0.75) {
                ctx.removeOneMatching(ItemRock.block);
                if (r > 0.8) ctx.spawnItemStack(new ItemStack(ItemRockFlat.block, 1));
                if (r > 0.95) ctx.spawnItemStack(new ItemStack(ItemRockFlat.block, 1));
                if (r < 0.04) ctx.spawnItemStack(new ItemStack(ItemFlintShard.block, 1));
            }
        }));

        // Bone -> bone shards on stone-like blocks (same block check as above)
        rules.add(new Rule(ctx -> {
			ItemStack main = ctx.mainHand; 
			boolean holdingBone = main.getItem() == Items.BONE; 
			boolean blockOk = ctx.blockAt == Blocks.STONE || ctx.blockAt == Blocks.COBBLESTONE || ctx.blockAt == Blocks.OBSIDIAN; 
			return holdingBone && blockOk;
        }, (ctx, r) -> {
            if (r > 0.9) {
                ctx.removeOneMatching(Items.BONE);
                ctx.spawnItemStack(new ItemStack(ItemBoneShard.block, 1));
                if (r < 0.95) ctx.spawnItemStack(new ItemStack(ItemBoneShard.block, 1));
                if (r < 0.35) ctx.spawnItemStack(new ItemStack(ItemBoneShard.block, 1));
            }
        }));

        rules.add(new Rule(ctx -> {
			ItemStack main = ctx.mainHand; 
			boolean holdingFlint = main.getItem() == Items.FLINT; 
			boolean blockOk = ctx.blockAt == Blocks.STONE || ctx.blockAt == Blocks.COBBLESTONE || ctx.blockAt == Blocks.OBSIDIAN; 
			return holdingFlint && blockOk;
        }, (ctx, r) -> {
            if (r > 0.9) {
                ctx.removeOneMatching(Items.FLINT);
                ctx.spawnItemStack(new ItemStack(ItemFlintShard.block, 1));
                if (r < 0.75) ctx.spawnItemStack(new ItemStack(ItemFlintShard.block, 1));
                if (r < 0.33) ctx.spawnItemStack(new ItemStack(ItemFlintShard.block, 1));
            }
        }));
        return rules;
    }
    public static void executeProcedure(Entity entity, World world, int x, int y, int z) {    
        //World world = (World) dependencies.get("world");
        if (world.isRemote) return;
        //Entity entity = (Entity) dependencies.get("entity");
        if (!(entity instanceof EntityPlayer)) return;
        //int x = (int) dependencies.get("x");
        //int y = (int) dependencies.get("y");
        //int z = (int) dependencies.get("z");

        Context ctx = new Context(entity, world, x, y, z);
        
        //List<Rule> rules = buildRules();
        for (Rule r : RULES)
            if (r.condition.matches(ctx)) {
            	double rand = ThreadLocalRandom.current().nextDouble();
            	r.action.apply(ctx, rand);
            	break;
            }
    }
    
    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer entity = event.getEntityPlayer();
        int i = event.getPos().getX();
        int j = event.getPos().getY();
        int k = event.getPos().getZ();
        World world = event.getWorld();
        /*
        java.util.HashMap<String, Object> dependencies = new java.util.HashMap<>();
        dependencies.put("x", i);
        dependencies.put("y", j);
        dependencies.put("z", k);
        dependencies.put("world", world);
        dependencies.put("entity", entity);
        dependencies.put("event", event);*/

        this.executeProcedure(entity, world, i, j, k);
        //this.executeProcedure(dependencies);
    }
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
