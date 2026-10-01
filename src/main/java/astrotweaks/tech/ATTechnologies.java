package astrotweaks.tech;

import astrotweaks.AstrotweaksMod;
import astrotweaks.ModVariables;
import astrotweaks.tech.ark.ArkGUI;
import astrotweaks.tech.ark.BlockArk;
import astrotweaks.tech.ark.BlockArkResonator;
import astrotweaks.tech.mt.MTGUI;
import astrotweaks.tech.mt.BlockMoneyTable;
import astrotweaks.tech.qts.BlockQTPSupressor;
import astrotweaks.tech.sd.BlockSpatialDome;
import astrotweaks.tech.tdark.BlockTDArk;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;




public class ATTechnologies {

    public static final Block MONEY_TABLE = new BlockMoneyTable.BlockCustom().setRegistryName("money_table");

    public static final Block ARK = new BlockArk.BlockCustom().setRegistryName("ark");
    public static final Block ARK_RESONATOR = new BlockArkResonator.BlockCustom().setRegistryName("ark_resonator");

    public static final Block QTP_SUPRESSOR = new BlockQTPSupressor.BlockCustom().setRegistryName("qtp_supressor");

    public static final Block SPATIAL_DOME = new BlockSpatialDome.BlockCustom().setRegistryName("spatial_dome");

    public static final Block TDARK = new BlockTDArk.BlockCustom().setRegistryName("tdark");



// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

    /** TDARK-блок регистрируется только при включённом мультиверсе и TDARK. */
    private static final Block[] BLOCKS_WITH_TDARK = {
        ARK, ARK_RESONATOR, MONEY_TABLE, QTP_SUPRESSOR, SPATIAL_DOME, TDARK
    };
    private static final Block[] BLOCKS_BASE = {
        ARK, ARK_RESONATOR, MONEY_TABLE, QTP_SUPRESSOR, SPATIAL_DOME
    };
    public static Block[] getBlocks() {
        return (ModVariables.MULTIVERSE && ModVariables.Enable_TDARK) ? BLOCKS_WITH_TDARK : BLOCKS_BASE;
    }

    private ATTechnologies() {}


    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(getBlocks());
        registerTileEntities();
    }

    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (Block block : getBlocks()) {
            event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        }
    }

    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(BlockArk.TileEntityCustom.class, "astrotweaks:te_ark");
        GameRegistry.registerTileEntity(BlockMoneyTable.TileEntityCustom.class, "astrotweaks:te_money_table");
        GameRegistry.registerTileEntity(BlockQTPSupressor.TileEntityCustom.class, "astrotweaks:te_qtp_supressor");
        GameRegistry.registerTileEntity(BlockSpatialDome.TileEntityCustom.class, "astrotweaks:te_spatial_dome");
        if (ModVariables.MULTIVERSE && ModVariables.Enable_TDARK) {
            GameRegistry.registerTileEntity(BlockTDArk.TileEntityCustom.class, "astrotweaks:te_tdark");
        }
        ForgeChunkManager.setForcedChunkLoadingCallback(AstrotweaksMod.instance, new ForgeChunkManager.LoadingCallback() {
            @Override
            public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
                if (world.isRemote) return;
                for (ForgeChunkManager.Ticket ticket : tickets) {
                    NBTTagCompound data = ticket.getModData();
                    if (data.hasKey("x") && data.hasKey("z")) {
                        int x = data.getInteger("x");
                        int z = data.getInteger("z");
                        ChunkPos cp = new ChunkPos(x >> 4, z >> 4);
                        ForgeChunkManager.forceChunk(ticket, cp);
                    }
                }
            }
        });
    }

    static {
        AstrotweaksMod.PACKET_HANDLER.registerMessage(ArkGUI.ArkActionMessageHandler.class, ArkGUI.ArkActionMessage.class, 10, net.minecraftforge.fml.relauncher.Side.SERVER);
        AstrotweaksMod.PACKET_HANDLER.registerMessage(ArkGUI.GUIButtonPressedMessageHandler.class, ArkGUI.GUIButtonPressedMessage.class, 11, net.minecraftforge.fml.relauncher.Side.SERVER);

        AstrotweaksMod.PACKET_HANDLER.registerMessage(MTGUI.GUIButtonPressedMessageHandler.class, MTGUI.GUIButtonPressedMessage.class, 12, net.minecraftforge.fml.relauncher.Side.SERVER);
    }

    @Mod.EventBusSubscriber(modid = "astrotweaks", value = Side.CLIENT)
    public static class ClientHandler {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event) {
            for (Block block : getBlocks()) {
                Item item = Item.getItemFromBlock(block);
                if (item != null && item != Item.getItemFromBlock(net.minecraft.init.Blocks.AIR)) {
                    ModelLoader.setCustomModelResourceLocation(item, 0,
                        new ModelResourceLocation(block.getRegistryName(), "inventory"));
                }
            }
        }
    }
}
