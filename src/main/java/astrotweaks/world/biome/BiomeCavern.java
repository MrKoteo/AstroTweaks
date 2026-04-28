package astrotweaks.world;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.init.Blocks;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.EnumCreatureType;

import net.minecraft.util.ResourceLocation;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import astrotweaks.AstrotweaksModVariables;

import java.util.Random;

import astrotweaks.ElementsAstrotweaksMod;

@ElementsAstrotweaksMod.ModElement.Tag
public class BiomeCavern extends ElementsAstrotweaksMod.ModElement {
	@GameRegistry.ObjectHolder("astrotweaks:cavern")
	public static final BiomeGenCustom biome = null;
	public BiomeCavern(ElementsAstrotweaksMod instance) {super(instance, 680);}

	@Override
	public void initElements() {
		if (!AstrotweaksModVariables.Enable_Depths_Dimension) return;

		elements.biomes.add(() -> new BiomeGenCustom());
	}
	@Override
	public void init(FMLInitializationEvent event) {
	}

	static class BiomeGenCustom extends Biome {
	    public BiomeGenCustom() {
	        super(new Biome.BiomeProperties("Cavern")
	            .setRainfall(0F)
	            .setBaseHeight(0.1F)
	            .setHeightVariation(0.2F)
	            .setTemperature(1.1F) // 0.0 - 2.0
	            .setWaterColor(0x305080));
	        setRegistryName("cavern");

	        topBlock = Blocks.STONE.getDefaultState(); // temporarly
			fillerBlock = Blocks.STONE.getDefaultState();

	        // Remove all plants
	        decorator.treesPerChunk = 0;
	        decorator.flowersPerChunk = 0;
	        decorator.grassPerChunk = 0;
	        decorator.mushroomsPerChunk = 0;
	        decorator.bigMushroomsPerChunk = 0;
	        decorator.reedsPerChunk = 0;
	        decorator.cactiPerChunk = 0;
	        decorator.sandPatchesPerChunk = 0;
	        decorator.gravelPatchesPerChunk = 0;

	        // Clear mob spawn
	        //this.spawnableMonsterList.clear();
	        this.spawnableCreatureList.clear();
	        this.spawnableWaterCreatureList.clear();
	        //this.spawnableCaveCreatureList.clear();

	        this.getSpawnableList(EnumCreatureType.MONSTER).removeIf(entry -> entry.entityClass == EntitySpider.class);
	    }
	    @Override public WorldGenAbstractTree getRandomTreeFeature(Random rand) {return null; /*No trees*/}
	    @Override public int getSkyColorByTemp(float currentTemperature) {return -1;}
		// Redefining decorate to avoid NPE
	    @Override public void decorate(World worldIn, Random rand, BlockPos pos) {/*nothing*/}
	}
}
