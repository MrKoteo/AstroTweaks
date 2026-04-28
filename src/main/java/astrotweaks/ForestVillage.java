package astrotweaks.world;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.ArrayList;
import java.util.List;


public final class ForestVillage {
    public static final ArrayList<Biome> forestBiomes = new ArrayList<>();

    public ForestVillage() {}

    public void preInit(FMLPreInitializationEvent event) {

        forestBiomes.add(Biomes.FOREST);
        forestBiomes.add(Biomes.ROOFED_FOREST);

        List<Biome> villageBiomes = new ArrayList<>(MapGenVillage.VILLAGE_SPAWN_BIOMES);
        villageBiomes.addAll(forestBiomes);
        MapGenVillage.VILLAGE_SPAWN_BIOMES = villageBiomes;

        MinecraftForge.TERRAIN_GEN_BUS.register(new VillageMaterialHandler());

    }

    public static class VillageMaterialHandler {
        @SubscribeEvent
        public void getVillageBlockID(BiomeEvent.GetVillageBlockID event) {
			if (!ForestVillage.forestBiomes.contains(event.getBiome())) return;

        }
    }
}
