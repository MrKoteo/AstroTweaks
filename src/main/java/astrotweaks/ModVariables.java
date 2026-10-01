package astrotweaks;


import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
//import net.minecraft.block.Block;
//import net.minecraft.world.World;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


import java.util.*;



public class ModVariables {
	public ModVariables() {}
	public static boolean EnableProgressionSystem = false;
	public static boolean AstroTech_Environment = false;
	public static String CUSTOM_GAME_TITLE = "";


	public static boolean MULTIVERSE = false;
	public static boolean Enable_TDARK = false; // ничего не делает в игре если MULTIVERSE == False
	public static int MULTIVERSE_MAX_UNIVERSES = 40; // лимит числа вселенных ( LAST слот перезаписывается )
	public static boolean Enable_uVOID = false; // вселенная пустоты
	public static long MV_DeferredUnloadingMS = 8_000L;


	//								   МИНУТ * сек * тик
	public static int GG_MIN_DELAY_TICK = 5 * 60 * 20; // 5 minutes
	public static int GG_MAX_DELAY_TICK = 15 * 60 * 20; // 15 minutes
	public static int GG_MAX_OPER_PER_TICK = 64;
	public static int GG_Density = 18; // blocks in area
	public static int GG_Tall_Density = 10;
	public static int GG_Giant_Density = 3;
	public static boolean GG_ENABLED = true;
	// Трансформация блоков в замшелые варианты
	public static int BM_MIN_DELAY_TICK = 2 * 60 * 20; // _ minutes
	public static int BM_MAX_DELAY_TICK = 10 * 60 * 20; // _ minutes
	public static int BM_MAX_OPER_PER_TICK = 64;
	public static boolean BM_ENABLED = true;


	public static boolean Remove_METS_engineer = false;
	public static boolean Rem_Gravestone_Note = true;

	public static boolean ServerPingFix = true;
	public static boolean Enable_StepUp = true;
	public static boolean No_Potion_Icons = true;




	public static boolean Enable_RealisticBreak = false;
	public static double ExplosionDamageMult = 1.5D;
	public static boolean Enable_Dirt2Path = true;

	public static boolean QM_is_fully_unbreakable = true;
	public static boolean Better_Smelting = true;
	public static boolean Extra_Drops_Grass = true;
	public static boolean Extra_Drops_All = true;

	public static boolean Raw_Meat_Negative_Effects = true;
	public static boolean Food_Negative_Effects = true;

	public static boolean Enable_Depths_Dimension = true;
	public static boolean Enable_Depths_Dim_Bedrock_TP = true;

	public static boolean Enable_SnowVillages = true;
	public static boolean Enable_ForestVillages = true;
	//public static boolean Enable_BirchVillages = true;

	public static boolean Enable_Ground_Elements = true;
	public static double Stick_Gen_Attempts = 2.0D;
	public static double Rock_Gen_Attempts = 0.4D;
	public static int Stick_Gen_Min_Y = 60;
	public static int Stick_Gen_Max_Y = 125;
	public static int Rock_Gen_Min_Y = 55;
	public static int Rock_Gen_Max_Y = 150;
	public static Set<ResourceLocation> Stick_Gen_Biomes = null;
	public static Set<ResourceLocation> Rock_Gen_Biomes  = null;
	public static Set<Biome> Stick_Gen_Biomes_Cached = null;
	public static Set<Biome> Rock_Gen_Biomes_Cached = null;

	public static boolean Enable_Bushes = true;
    public static double bush1gen = 1.6; // Forest
    public static double bush2gen = 1.9; // swamp
    public static double bush3gen = 1.7; // Taiga
    public static double bush4gen = 1.8; // Jungle
    public static double bush5gen = 0.9; // siren
    public static double bush6gen = 0.5; // Plains
    public static double bush7gen = 1.6; // Jungle
    public static double fern1gen = 2.3; // taiga

	// Black Hole
	public static int BH_BUDGET_PER_TICK = 2048; // макс блоков за тик
	public static int BH_RESCAN_DELAY_TICKS = 3600; // 3 минуты, тиков
	public static double BH_MIN_ACCEL = 0.002D;
	public static double BH_MAX_ACCEL = 5.0D;
	public static double BH_MAX_SPEED = 4.5D;
	public static double BH_SUFFOCATION_ACCEL = 0.4D;
	public static double BH_MAX_GRAVITY_RANGE = 256.0D; // блоков
	public static double BH_MAX_BLOCK_CAPTURE_RANGE = 192.0D;
	public static double BH_MAX_MASS = 1.0E12D;

    public static double BH_MASS_PER_ITEM = 1.5D;
    public static double BH_MASS_PER_ENTITY = 20.0D;
    public static double BH_MASS_PER_XP = 0.5D;
    public static double BH_MASS_PER_PLAYER = 50.0D;
    public static double BH_MASS_PER_LIQUID = 1.0D;







	public static boolean Extra_Fuels = true;
	public static boolean doRegisterMinedBlocks = true;

	public static boolean Money_Can_Smelt = true;
	public static boolean Money_Can_Craft = true;
	public static boolean Money_Can_Conversion = true;
	public static int Money_ConvCount = 10;

	public static boolean OW_Minerals_Gen = true;
	public static boolean OW_Ruby_Gen = true;
	public static boolean OW_Quartz_Gen = true;

	public static int QTS_Max_Range = 2048;

	public static int SD_Max_Range = 128;

	public static boolean NoRedFlash = true;






	//##################################################
	/// TEH

	public static final Set<Biome> GEN_DEFAULT_BIOMES = createDefaultBiomes();
	private static final Set<Biome> createDefaultBiomes() {
	    ResourceLocation[] names = new ResourceLocation[] {
	        new ResourceLocation("plains"), new ResourceLocation("forest"), new ResourceLocation("taiga"),
	        new ResourceLocation("swampland"), new ResourceLocation("forest_hills"), new ResourceLocation("taiga_hills"),
	        new ResourceLocation("jungle"), new ResourceLocation("jungle_hills"), new ResourceLocation("jungle_edge"),
	        new ResourceLocation("birch_forest"), new ResourceLocation("birch_forest_hills"), new ResourceLocation("roofed_forest"),
	        new ResourceLocation("redwood_taiga"), new ResourceLocation("redwood_taiga_hills"), new ResourceLocation("savanna"),
	        new ResourceLocation("river"), new ResourceLocation("smaller_extreme_hills"),
	        new ResourceLocation("extreme_hills_with_trees"), new ResourceLocation("savanna_rock")
	    };
	
	    Set<Biome> set = new HashSet<>(names.length);
	    for (ResourceLocation rl : names) {
	        Biome b = Biome.REGISTRY.getObject(rl);
	        if (b != null) set.add(b);
	    }
	    return Collections.unmodifiableSet(set);
	}

	//public static Set<Biome> GGAllowed = createGGBiomes();

	public static List<ItemStack> MEAT_LIST;

	//##################################################

	public static void preInit() {}
	public static void init() {

		

        List<ItemStack> items = new ArrayList<>();
		String[] oreDictNames = {
		    "listAllmeatraw",
		    "meatRaw",
		    "listAllfishraw"
		};
		for (String name : oreDictNames) {
		    for (ItemStack stack : OreDictionary.getOres(name)) {
		        if (stack != null && !stack.isEmpty()) {
		            items.add(stack.copy());
		        }
		    }
		}

        String[] mEntries = {
            "minecraft:fish:1",
            "minecraft:fish:2"
        };
        for (String entry : mEntries) {
            String[] parts = entry.split(":");
            if (parts.length < 2) continue;
            String modid = parts[0];
            String itemName = parts[1];
            int meta = (parts.length > 2) ? Integer.parseInt(parts[2]) : 0;
            Item item = Item.getByNameOrId(modid + ":" + itemName);
            if (item != null) {
                ItemStack stack = new ItemStack(item, 1, meta);
                items.add(stack);
            } else {
                System.out.println("Couldn't find item: " + entry);
            }
        }
        
        MEAT_LIST = Collections.unmodifiableList(items);

		//public static volatile Set<Block> DIRT_LIKE = Collections.emptySet();
	}

	public static void postInit() {

		/*
	    ResourceLocation[] names = new ResourceLocation[] {
	        new ResourceLocation("desert"), new ResourceLocation("frozen_ocean"), new ResourceLocation("frozen_river"), new ResourceLocation("ice_flats"),
	        new ResourceLocation("ice_mountains"), new ResourceLocation("mushroom_island"), new ResourceLocation("mushroom_island_shore"), new ResourceLocation("desert_hills"),
	        new ResourceLocation("cold_beach"), new ResourceLocation("taiga_cold"), new ResourceLocation("taiga_cold_hills"), new ResourceLocation("mesa"),
	        new ResourceLocation("mesa_rock"), new ResourceLocation("mesa_clear_rock"), new ResourceLocation("mutated_desert"), new ResourceLocation("mutated_ice_flats"),
	        new ResourceLocation("mutated_mesa"), new ResourceLocation("mutated_mesa_rock"), new ResourceLocation("mutated_mesa_clear_rock")
	    };
	
	    Set<Biome> set = new HashSet<>(names.length);
	    for (ResourceLocation rl : names) {
	        Biome b = Biome.REGISTRY.getObject(rl);
	        if (b != null) set.add(b);
	    }
	    GGBlacklist = Collections.unmodifiableSet(set);
	    */
		String[] names = new String[] {
		    "desert","frozen_ocean","frozen_river","ice_flats","ice_mountains",
		    "mushroom_island","mushroom_island_shore","desert_hills","cold_beach",
		    "taiga_cold","taiga_cold_hills","mesa","mesa_rock","mesa_clear_rock",
		    "mutated_desert","mutated_ice_flats","mutated_mesa","mutated_mesa_rock","mutated_mesa_clear_rock"
		};
		
		int maxId = 0;
		// Find MAX ID in registry (BitSet size)
		for (int i = 0; i < Biome.REGISTRY.getKeys().size(); i++) {
		    maxId = Math.max(maxId, Biome.REGISTRY.getKeys().size());
		}
		BitSet biomeBlacklistBits = new BitSet(maxId + 1);
		for (String s : names) {
		    ResourceLocation rl = new ResourceLocation(s);
		    Biome b = Biome.REGISTRY.getObject(rl);
		    if (b != null) {
		        int id = Biome.REGISTRY.getIDForObject(b);
		        if (id >= 0) biomeBlacklistBits.set(id);
		    }
		}

		//GGBlacklist = biomeBlacklistBits;

	}

























	public static class MapVariables extends WorldSavedData {
		public static final String DATA_NAME = "astrotweaks_mapvars";
		public boolean showDeaths = false;
		public boolean Marked = false;
		public MapVariables() { super(DATA_NAME); }
		public MapVariables(String s) { super(s); }
		@Override public void readFromNBT(NBTTagCompound nbt) {
			showDeaths = nbt.getBoolean("showDeaths");
			Marked = nbt.getBoolean("Marked");
		}
		@Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			nbt.setBoolean("showDeaths", showDeaths);
			nbt.setBoolean("Marked", Marked);
			return nbt;
		}
		public void syncData(World world) {
			this.markDirty();
			if (world.isRemote) AstrotweaksMod.PACKET_HANDLER.sendToServer(new WorldSavedDataSyncMessage(0, this));
			else 				AstrotweaksMod.PACKET_HANDLER.sendToAll(new WorldSavedDataSyncMessage(0, this));
		}
		public static MapVariables get(World world) {
			MapVariables instance = (MapVariables) world.getMapStorage().getOrLoadData(MapVariables.class, DATA_NAME);
			if (instance == null) {
				instance = new MapVariables();
				world.getMapStorage().setData(DATA_NAME, instance);
			}
			return instance;
		}
	}

	public static class WorldVariables extends WorldSavedData {
		public static final String DATA_NAME = "astrotweaks_worldvars";
		public WorldVariables() { super(DATA_NAME); }
		public WorldVariables(String s) { super(s); }
		@Override public void readFromNBT(NBTTagCompound nbt) {}
		@Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { return nbt; }
		public void syncData(World world) {
			this.markDirty();
			if (world.isRemote) AstrotweaksMod.PACKET_HANDLER.sendToServer(new WorldSavedDataSyncMessage(1, this));
			else 				AstrotweaksMod.PACKET_HANDLER.sendToDimension(new WorldSavedDataSyncMessage(1, this), world.provider.getDimension());
		}
		public static WorldVariables get(World world) {
			WorldVariables instance = (WorldVariables) world.getPerWorldStorage().getOrLoadData(WorldVariables.class, DATA_NAME);
			if (instance == null) {
				instance = new WorldVariables();
				world.getPerWorldStorage().setData(DATA_NAME, instance);
			}
			return instance;
		}
	}

	public static class WorldSavedDataSyncMessageHandler implements IMessageHandler<WorldSavedDataSyncMessage, IMessage> {
		@Override public IMessage onMessage(WorldSavedDataSyncMessage message, MessageContext context) {
			if (context.side == Side.SERVER)
				context.getServerHandler().player.getServerWorld().addScheduledTask(() -> syncData(message, context, context.getServerHandler().player.world));
			else Minecraft.getMinecraft().addScheduledTask(() -> syncData(message, context, Minecraft.getMinecraft().player.world));
			return null;
		}
		private void syncData(WorldSavedDataSyncMessage message, MessageContext context, World world) {
			if (context.side == Side.SERVER) {
				message.data.markDirty();
				if (message.type == 0)	AstrotweaksMod.PACKET_HANDLER.sendToAll(message);
				else					AstrotweaksMod.PACKET_HANDLER.sendToDimension(message, world.provider.getDimension());
			}
			if (message.type == 0)	world.getMapStorage().setData(MapVariables.DATA_NAME, message.data);
			else 					world.getPerWorldStorage().setData(WorldVariables.DATA_NAME, message.data);
			
		}
	}

	public static class WorldSavedDataSyncMessage implements IMessage {
		public int type;
		public WorldSavedData data;
		public WorldSavedDataSyncMessage() {}
		public WorldSavedDataSyncMessage(int type, WorldSavedData data) {
			this.type = type;
			this.data = data;
		}
		@Override
		public void toBytes(io.netty.buffer.ByteBuf buf) {
			buf.writeInt(this.type);
			ByteBufUtils.writeTag(buf, this.data.writeToNBT(new NBTTagCompound()));
		}
		@Override
		public void fromBytes(io.netty.buffer.ByteBuf buf) {
			this.type = buf.readInt();
			if (this.type == 0)	this.data = new MapVariables();
			else				this.data = new WorldVariables();
			this.data.readFromNBT(ByteBufUtils.readTag(buf));
		}
	}






}
