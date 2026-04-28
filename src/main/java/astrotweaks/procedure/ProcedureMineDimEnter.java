package astrotweaks.procedure;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.init.Blocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import astrotweaks.world.DepthsDim;
import astrotweaks.ElementsAstrotweaksMod;
import astrotweaks.procedure.ProcedureSwitchDimProc;
import java.util.HashMap;
import java.util.Map;

import astrotweaks.AstrotweaksModVariables;

@ElementsAstrotweaksMod.ModElement.Tag
public final class ProcedureMineDimEnter extends ElementsAstrotweaksMod.ModElement {
    //private static final int OVERWORLD_ID = 0;
    private static final int CAVERN_DIM_ID = DepthsDim.DIMID;
    private static final int MIN_HEIGHT_OVERWORLD = 4;
    private static final int MAX_HEIGHT_CAVERN = 250;
    private static final int TELEPORT_HEIGHT_OVERWORLD = 5;
    private static final int TELEPORT_HEIGHT_CAVERN = 252;

    public ProcedureMineDimEnter(ElementsAstrotweaksMod instance) {
        super(instance, 522);
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
    	if (!AstrotweaksModVariables.Enable_Depths_Dim_Bedrock_TP) return;
    	
        EntityPlayer player = event.getEntityPlayer();
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        
        if (world.getBlockState(pos).getBlock() != Blocks.BEDROCK) return;
        if (!isHoldingPickaxe(player)) return;
        
        int dimension = world.provider.getDimension();
        boolean shouldTeleport = false;
        int targetDim = 0;
        int targetY = 0;
        
        //teleport conditions
	    if (dimension == 0 && player.posY < MIN_HEIGHT_OVERWORLD) {
	        targetDim = CAVERN_DIM_ID;
	        targetY = TELEPORT_HEIGHT_CAVERN;
	    } else if (dimension == CAVERN_DIM_ID && player.posY > MAX_HEIGHT_CAVERN) {
	        targetDim = 0;
	        targetY = TELEPORT_HEIGHT_OVERWORLD;
	    } else {
	        return;
	    }
        
        if (world.isRemote) return;
        
        MinecraftServer server = world.getMinecraftServer();
        if (server == null) return;
		
		HashMap<String, String> cmdparams = new HashMap<>();
		cmdparams.put("0", Integer.toString(targetDim));
		cmdparams.put("1", player.getName());
		cmdparams.put("2", Integer.toString(pos.getX()));
		cmdparams.put("3", Integer.toString(targetY));
		cmdparams.put("4", Integer.toString(pos.getZ()));

		Map<String, Object> deps = new HashMap<>();
		deps.put("entity", player);         // EntityPlayer (not null)
		deps.put("cmdparams", cmdparams);
		deps.put("server", server);         // if Procedure need Server
		
		ProcedureSwitchDimProc.executeProcedure(deps, true);

        
        // clear target area
        World targetWorld = server.getWorld(targetDim);
        if (targetWorld != null) {
        	BlockPos targetPos = new BlockPos(pos.getX(), targetY, pos.getZ());
            targetWorld.destroyBlock(targetPos, 	true);
            targetWorld.destroyBlock(targetPos.up(),true);
	        //targetWorld.setBlockState(targetPos, Blocks.AIR.getDefaultState(), 2);
	        //targetWorld.setBlockState(targetPos.up(), Blocks.AIR.getDefaultState(), 2);
            
        }

        // debug
        System.out.println("Switch dimension: Depths <-> Overworld");
        
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
    	if (!AstrotweaksModVariables.Enable_Depths_Dim_Bedrock_TP) return;
    	
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static boolean isHoldingPickaxe(EntityPlayer player) {
	    if (player == null) return false;
	    ItemStack held = player.getHeldItemMainhand(); // main hand
	    if (held == null || held.isEmpty()) return false;
	    Item item = held.getItem();
	    return item.getToolClasses(held).contains("pickaxe");
	}
}