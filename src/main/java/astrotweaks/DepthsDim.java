package astrotweaks.world;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.DimensionManager;

import net.minecraft.world.gen.layer.IntCache;
import net.minecraft.world.gen.layer.GenLayerZoom;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.World;
import net.minecraft.world.DimensionType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ReportedException;
import net.minecraft.init.Blocks;
import net.minecraft.init.Biomes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.CrashReport;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.Entity;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.block.Block;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraftforge.event.terraingen.TerrainGen;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.List;

import astrotweaks.block.*;


//import astrotweaks.ElementsAstrotweaksMod;
import astrotweaks.AstrotweaksModVariables;


public class DepthsDim {
	public static int DIMID = -6000;
	public static final boolean NETHER_TYPE = false;
	public static DimensionType dtype;
	public DepthsDim() {
	}
	//@SubscribeEvent
	public void preInit(FMLPreInitializationEvent event) {
		//if (!AstrotweaksModVariables.Enable_Depths_Dimension) return;
		if (DimensionManager.isDimensionRegistered(DIMID)) {
			DIMID = DimensionManager.getNextFreeDimId();
			System.err.println("Dimension ID for DepthsDim is already registered. Fallback to ID: " + DIMID);
		}
		dtype = DimensionType.register("depths", "_depths", DIMID, WorldProviderMod.class, true); // check
		DimensionManager.registerDimension(DIMID, dtype);
	}

	public static class WorldProviderMod extends WorldProvider { // this's cavern
		@SubscribeEvent
		public void init() {
			//if (!AstrotweaksModVariables.Enable_Depths_Dimension) return;
			this.biomeProvider = new BiomeProviderCustom(this.world.getSeed());
			this.nether = false; // false, because this isn't nether
			this.hasSkyLight = false; // No naturally light
		}
		@Override public void calculateInitialWeather() {}
		@Override public void updateWeather() {}
		@Override public boolean canDoLightning(net.minecraft.world.chunk.Chunk chunk) {return false;}
		@Override public boolean canDoRainSnowIce(net.minecraft.world.chunk.Chunk chunk) {return false;}
		@Override public DimensionType getDimensionType() {return dtype;}
		@SideOnly(Side.CLIENT)
		@Override
		public Vec3d getFogColor(float par1, float par2) {
			return new Vec3d(0.0f, 0.0f, 0.0f);
		}
		@SideOnly(Side.CLIENT)
		@Override public Vec3d getSkyColor(Entity cameraEntity, float partialTicks) { return new Vec3d(0.0D, 0.0D, 0.0D); /* dark sky */ }
		@SideOnly(Side.CLIENT)
		@Override public float calculateCelestialAngle(long worldTime, float partialTicks) { return 0.5F;/* fixed time/bg */ }
		@Override public IChunkGenerator createChunkGenerator() { return new ChunkProviderModded(this.world, this.world.getSeed() - DIMID); }
		@Override public boolean isSurfaceWorld() {return false;}
		@Override public boolean canRespawnHere() {return false;}
		@Override
		@SideOnly(Side.CLIENT)
		public boolean doesXZShowFog(int par1, int par2) {return true;}
		@Override public WorldSleepResult canSleepAt(EntityPlayer player, BlockPos pos) { return WorldSleepResult.DENY; }
		@Override public boolean doesWaterVaporize() {return false;}
		// No Nether/End portals
		@Override public boolean canCoordinateBeSpawn(int x, int z) { return false; }
	}

	public static class ChunkProviderModded implements IChunkGenerator { // this's cavern
		// If Deepslate not exist, use stone as fallback
		private static final IBlockState STONE = Blocks.STONE.getDefaultState();
		private static final IBlockState DEEPSLATE = getBlockState("astrotweaks:deepslate", STONE);
		private static final IBlockState OBSIDIAN = Blocks.OBSIDIAN.getDefaultState();
		private static final IBlockState FLUID = Blocks.LAVA.getDefaultState();
		private static final IBlockState AIR = Blocks.AIR.getDefaultState();
		private static final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
		private static final int SEALEVEL = 40; // Lava level is 40

		private final World world;
		private final Random random;
		//private final MapGenBase genCaves; // Simple caves
		//private final MapGenBase genRavines; // gen Ravines
		private final CustomCaveGen genCustomCaves; // gen custom caves (>128Y)

		// add noise gens (even if we don't use them, they are needed for event)
		private final NoiseGeneratorOctaves lperlinNoise1;
		private final NoiseGeneratorOctaves lperlinNoise2;
		private final NoiseGeneratorOctaves perlinNoise1;
		private final NoiseGeneratorOctaves secondaryStoneNoiseGen;
		private final NoiseGeneratorOctaves depthNoiseGen;
		private final NoiseGeneratorOctaves depthNoise;

		private static IBlockState getBlockState(String registryName, IBlockState fallback) {
			Block block = GameRegistry.findRegistry(Block.class).getValue(new ResourceLocation(registryName));
			return block != null ? block.getDefaultState() : fallback;
		}

		public ChunkProviderModded(World worldIn, long seed) { // constructor
			worldIn.setSeaLevel(SEALEVEL);
			this.world = worldIn;
			this.random = new Random(seed);
			// init noise gens !!!
			this.lperlinNoise1 = new NoiseGeneratorOctaves(this.random, 16);
			this.lperlinNoise2 = new NoiseGeneratorOctaves(this.random, 16);
			this.perlinNoise1 = new NoiseGeneratorOctaves(this.random, 8);
			this.secondaryStoneNoiseGen = new NoiseGeneratorOctaves(this.random, 4);
			this.depthNoiseGen = new NoiseGeneratorOctaves(this.random, 4);
			this.depthNoise = new NoiseGeneratorOctaves(this.random, 16);
			// CALL EVENT InitNoiseGensEvent (important for Forge!!!)
			net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextHell ctx = 
				new net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextHell(
					lperlinNoise1, lperlinNoise2, perlinNoise1, secondaryStoneNoiseGen, depthNoiseGen, depthNoise, null);
			ctx = TerrainGen.getModdedNoiseGenerators(worldIn, this.random, ctx);

			// Simple caves
			//this.genCaves = new MapGenCaves(); // vanilla

			//this.genCaves = new CustomMapGenCaves(); // modified for Deepslate replace
			this.genCustomCaves = new CustomCaveGen();
			//this.genRavines = new MapGenRavine();

		}
	
		@Override
		public Chunk generateChunk(int x, int z) {
			this.random.setSeed((long) x * 347539041L + (long) z * 535358712L);
			ChunkPrimer chunkprimer = new ChunkPrimer();

			this.fillChunk(x, z, chunkprimer);

			//this.genCaves.generate(this.world, x, z, chunkprimer);
			//this.genRavines.generate(this.world, x, z, chunkprimer);
			this.genCustomCaves.generate(this.world, x, z, chunkprimer);

			// All air blocks < X height will be replace to Lava
			for (int y = 0; y < SEALEVEL; y++) {
				for (int localX = 0; localX < 16; localX++) {
					for (int localZ = 0; localZ < 16; localZ++) {
						if (chunkprimer.getBlockState(localX, y, localZ).getBlock() == Blocks.AIR) {
							chunkprimer.setBlockState(localX, y, localZ, FLUID);
						}
					}
				}
			}
			Chunk chunk = new Chunk(this.world, chunkprimer, x, z);
			Biome[] abiome = this.world.getBiomeProvider().getBiomes((Biome[]) null, x * 16, z * 16, 16, 16);
			byte[] abyte = chunk.getBiomeArray();

			for (int i = 0; i < abyte.length; ++i) {
				abyte[i] = (byte) Biome.getIdForBiome(abiome[i]);
			}
			chunk.generateSkylightMap();
			return chunk;
		}

		private static final int DEEPSLATE_LAYER_Y = 100;
		private static final int OBSIDIAN_LAYER_Y = 30;
		private static final int DLY_m2 = DEEPSLATE_LAYER_Y - 2;
		private static final int OLY_m2 = OBSIDIAN_LAYER_Y - 2;

		private final double[] noiseBuffer = new double[256];

		private void fillChunk(int x, int z, ChunkPrimer primer) {
			// Gen noise for smooth layers transitions
			//double[][] noise = new double[16][16];
			
			long baseSeed = this.world.getSeed() + (long)x * 341873128712L + (long)z * 132897987541L;


		    for (int localX = 0; localX < 16; localX++) {
		        for (int localZ = 0; localZ < 16; localZ++) {
		            int idx = (localX << 4) | localZ;
		            // fill noiseBuffer per-column (reuse preallocated array)
		            noiseBuffer[idx] = (this.random.nextDouble() * 2.0 - 1.0) * 5.0;
		
		            // small column-local RNG for transition randomness (deterministic per chunk+col)
		            long colSeed = baseSeed ^ ((localX << 8) | localZ);
		            Random colRand = new Random(colSeed);
		
		            double colNoise = noiseBuffer[idx];
		
		            for (int y = 0; y < 256; y++) {
		                // bedrock at extremes
		                if (y == 0 || y == 255) {
		                    primer.setBlockState(localX, y, localZ, BEDROCK);
		                    continue;
		                }
		                double adjustedY = y + colNoise;

		                // fast common cases first (no RNG)
		                if (adjustedY < OLY_m2) {
		                    primer.setBlockState(localX, y, localZ, OBSIDIAN);
		                } else if (adjustedY >= OBSIDIAN_LAYER_Y + 1 && adjustedY < DLY_m2) {
		                    primer.setBlockState(localX, y, localZ, DEEPSLATE);
		                } else if (adjustedY >= DEEPSLATE_LAYER_Y + 1) {
		                    primer.setBlockState(localX, y, localZ, STONE);
		                } else {
		                    // only in narrow transition zones we need randomness
		                    if (adjustedY < OBSIDIAN_LAYER_Y + 1) { // transition 28..31: obsidian <-> deepslate
		                        double transition = (adjustedY - OLY_m2) / 4.0; // ~0..0.75
		                        primer.setBlockState(localX, y, localZ, (colRand.nextDouble() > transition) ? OBSIDIAN : DEEPSLATE);
		                    } else { // transition 98..101: deepslate <-> stone
		                        double transition = (adjustedY - DLY_m2) / 4.0;
		                        primer.setBlockState(localX, y, localZ, (colRand.nextDouble() > transition) ? DEEPSLATE : STONE);
		                    }
		                }
		            }
		        }
		    }
		}

		@Override
		public void populate(int x, int z) {
			BlockFalling.fallInstantly = true;
			net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(true, this, this.world, this.random, x, z, false);
			int i = x * 16;
			int j = z * 16;
			BlockPos blockpos = new BlockPos(i, 0, j);
			Biome biome = this.world.getBiome(blockpos.add(16, 0, 16));
			ChunkPos chunkpos = new ChunkPos(x, z);

			// ORE GEN - !!!!!
			// Generate ores at all heights
			generateOres(this.world, this.random, i, j);

			// Decoration ONLY if biome have Decorator
			try {
				net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(
					new net.minecraftforge.event.terraingen.DecorateBiomeEvent.Pre(this.world, this.random, blockpos));
				// safety
				if (biome != null) { biome.decorate(this.world, this.random, new BlockPos(i, 0, j)); }
				net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(
					new net.minecraftforge.event.terraingen.DecorateBiomeEvent.Post(this.world, this.random, blockpos));
			} catch (Exception e) {/*ignore deco errors*/}

			// Mobs Spawns
			if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, this.random, x, z, false, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ANIMALS)) {
				WorldEntitySpawner.performWorldGenSpawning(this.world, biome, i + 8, j + 8, 16, 16, this.random);
			}

			net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(false, this, this.world, this.random, x, z, false);
			BlockFalling.fallInstantly = false;
		}

		private void generateOres(World world, Random random, int x, int z) {
			Block stoneBlock = STONE.getBlock();
			Block deepslateB = DEEPSLATE.getBlock();

			//			Block							Replaced block			worldID	Random	X	Z	VeinSize	GenChance	MinY	MaxY
			generateOre(Blocks.COAL_ORE.getDefaultState(), stoneBlock, world, random, x, z, 	30, 20, 128, 252);
			generateOre(Blocks.IRON_ORE.getDefaultState(), stoneBlock, world, random, x, z,		10, 30,  99, 252);
			generateOre(Blocks.GOLD_ORE.getDefaultState(), stoneBlock, world, random, x, z,		 8, 20,  99, 252);
			generateOre(Blocks.DIAMOND_ORE.getDefaultState(),stoneBlock,world,random, x, z,		 8, 15,  93, 252);
			generateOre(Blocks.REDSTONE_ORE.getDefaultState(),stoneBlock,world,random,x, z, 	 8, 20,  99, 252);
			generateOre(Blocks.LAPIS_ORE.getDefaultState(), stoneBlock, world, random,x, z,  	 8, 13,  99, 252);
			generateOre(Blocks.EMERALD_ORE.getDefaultState(),stoneBlock, world,random,x, z, 	 4, 13,  99, 252);
			generateOre(Blocks.COAL_BLOCK.getDefaultState(), stoneBlock, world,random,x, z, 	 5,  4,  99, 252);
			generateOre(Blocks.GRAVEL.getDefaultState(), stoneBlock, world, random, x, z,		24, 16, 140, 252);
			generateOre(Blocks.COBBLESTONE.getDefaultState(),stoneBlock,world,random, x, z,  	20,  9,  99, 252);
			generateOre(Blocks.STONE.getStateFromMeta(5), stoneBlock,world,random, x, z,	 	30,  9, 150, 252);
			generateOre(Blocks.STONE.getStateFromMeta(3), stoneBlock,world,random, x, z,		30, 10,  99, 220);
			generateOre(Blocks.STONE.getStateFromMeta(1), stoneBlock,world,random, x, z,		30,  9,  99, 190);
			generateOre(BlockRubyOre.block.getDefaultState(), stoneBlock,world,random, x, z,  	 4,  7,  99, 252);
			generateOre(BlockQuartzOreStone.block.getDefaultState(),stoneBlock,world,random,x,z,13,  6, 150, 252);
			generateOre(BlockMineralsOre.block.getDefaultState(),stoneBlock,world,random,x,z,	10, 25,  99, 252);

			// deepslate
			generateOre(Blocks.MAGMA.getDefaultState(), deepslateB, world, random, x, z, 				20,  9, 26, 50);
			generateOre(Blocks.OBSIDIAN.getDefaultState(), deepslateB, world, random, x, z,	 			 7,  4, 29, 40);
			generateOre(Blocks.STONE.getStateFromMeta(1), deepslateB,world,random, x, z,				18,  3, 80, 95);

			generateOre(BlockDeepDiamondOre.block.getDefaultState(), deepslateB, world, random, x, z, 	 7,  5, 40, 96);
			generateOre(BlockDeepEmeraldOre.block.getDefaultState(), deepslateB, world, random, x, z, 	 5,  6, 30, 96);
			generateOre(BlockDeepRedstoneOre.block.getDefaultState(),deepslateB, world, random, x, z, 	 7,  5, 60, 96);
			generateOre(BlockDeepLapisOre.block.getDefaultState(),	deepslateB, world, random, x, z, 	 7,  5, 50, 96);
			generateOre(BlockDeepIronOre.block.getDefaultState(),	deepslateB, world, random, x, z, 	 11, 6, 60, 96);
			generateOre(BlockDeepGoldOre.block.getDefaultState(),	deepslateB, world, random, x, z, 	 9,  6, 30, 96);

			generateOre(BlockDeepMinerals.block.getDefaultState(),	deepslateB, world, random, x, z, 	 7,  8, 26, 64);

			// obsidian
			generateOre(BlockDeepRichMinerals.block.getDefaultState(),OBSIDIAN.getBlock(),world,random,x,z,	6, 30, 1, 28);
		}
		private void generateOre(IBlockState ore, Block targetBlock, World world, Random random, int x, int z, int veinSize, int chances, int minHeight, int maxHeight) {
			if (maxHeight < minHeight) {
				int i = minHeight;
				minHeight = maxHeight;
				maxHeight = i;
			} else if (maxHeight == minHeight) {
				if (minHeight < 254) { maxHeight++; } 
				else { minHeight--; }
			}
			for (int chance = 0; chance < chances; chance++) {
				int posX = x + random.nextInt(16);
				int posY = random.nextInt(maxHeight - minHeight) + minHeight;
				int posZ = z + random.nextInt(16);
				(new WorldGenMinable(ore, veinSize, BlockMatcher.forBlock(targetBlock))).generate(world, random, new BlockPos(posX, posY, posZ));
			}
		}
		@Override public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) { return this.world.getBiome(pos).getSpawnableList(creatureType); }
		@Override public void recreateStructures(Chunk chunkIn, int x, int z) {}
		@Override public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {return false;}
		@Override public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {return null;}
		@Override public boolean generateStructures(Chunk chunkIn, int x, int z) {return false;}
	}

	/////
	public static class CustomCaveGen extends MapGenCaves {
		private static final int CHUNK_SKIP_CHANCE = 19; // 1/28 chunks = ~3.6% (was 25)
		private final NoiseGeneratorPerlin caveNoise;
		private final float[] caveSizeCache = new float[256];
		public CustomCaveGen() {
			super();
			this.caveNoise = new NoiseGeneratorPerlin(new Random(0L), 4);
		}

		@Override
		protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int originalX, int originalZ, ChunkPrimer chunkPrimerIn) {
			// Use one Random for the entire chunk for consistency
			long worldSeed = worldIn.getSeed();
			long seed = worldSeed + (long)chunkX * 341873128712L + (long)chunkZ * 132897987541L;
			Random rand = new Random(seed);
			// Chance to skip iteration; 1 chance in `CHUNK_SKIP_CHANCE` per chunk
			if (rand.nextInt(CHUNK_SKIP_CHANCE) != 0) return;

			// Increased the frequency of caves - now they generate almost always
			int caveCount = 3 + rand.nextInt(3); // 3-5 systems
			for (int i = 0; i < caveCount; ++i) {
				generateCaveSystem(rand, originalX, originalZ, chunkPrimerIn, 
					 chunkX * 16 + rand.nextInt(16), 
					 chunkZ * 16 + rand.nextInt(16));
			}
		}

		private void generateCaveSystem(Random rand, int chunkX, int chunkZ, ChunkPrimer primer, double startX, double startZ) {
			// Clearer height distribution with different weights
			int heightType = rand.nextInt(100);
			double startY;
			HeightZone zone;

			if (heightType < 10) {		  // 1-45 (13% - lava)
				startY = 5 + rand.nextDouble() * 45;
				zone = HeightZone.LAVA;
			} else if (heightType < 39) {   // 50-100 (29% - spahetti)
				startY = 45 + rand.nextDouble() * 55;
				zone = HeightZone.SPAGHETTI;
			} else if (heightType < 61) {   // 100-150 (22% - flattened)
				startY = 100 + rand.nextDouble() * 50;
				zone = HeightZone.FLAT;
			} else if (heightType < 82) {   // 150-200 (18% - normal)
				startY = 150 + rand.nextDouble() * 50;
				zone = HeightZone.MEDIUM;
			} else {						// 200-250 (18% - large halls)
				startY = 190 + rand.nextDouble() * 40;
				zone = HeightZone.HALLS;
			}

			// Generate the main cave with zone parameters
			float mainSize = getCaveSizeForZone(zone, rand, startY);
			float mainYaw = rand.nextFloat() * (float)Math.PI * 2.0F;
			float mainPitch = getPitchForZone(zone, rand);
			int mainLength = getLengthForZone(zone, rand);
			double verticalScale = getVerticalScaleForZone(zone, rand, startY);

			this.addTunnel(rand.nextLong(), chunkX, chunkZ, primer, startX, startY, startZ, mainSize, mainYaw, mainPitch, 0, mainLength, verticalScale);
			// Branching based on zone
			generateBranches(rand, chunkX, chunkZ, primer, startX, startY, startZ, mainSize, mainYaw, mainPitch, zone, mainLength);
		}

		private void generateBranches(Random rand, int chunkX, int chunkZ, ChunkPrimer primer, double x, double y, double z, float mainSize, 
						float mainYaw, float mainPitch, HeightZone zone, int mainLength) {
			// Different number of branches depending on the zone
			int branchCount = getBranchCountForZone(zone, rand);

			for (int j = 0; j < branchCount; ++j) {
				float branchYaw  = mainYaw  + (rand.nextFloat() - 0.5F) * getYawSpreadForZone(zone);
				float branchPitch= mainPitch+ (rand.nextFloat() - 0.5F) * 0.5F;
				float branchSize = mainSize * getBranchSizeMultiplier(zone, rand);
				// Offset from the main cave
				double offsetX = (rand.nextDouble() - 0.5) * getBranchOffset(zone);
				double offsetY = (rand.nextDouble() - 0.5) * getVerticalOffset(zone);
				double offsetZ = (rand.nextDouble() - 0.5) * getBranchOffset(zone);

				int branchLength = (int)(mainLength * getBranchLengthMultiplier(zone, rand));

				this.addTunnel(rand.nextLong(), chunkX, chunkZ, primer, x + offsetX, y + offsetY, z + offsetZ, branchSize, branchYaw, branchPitch, 
					0, branchLength, getVerticalScaleForZone(zone, rand, y + offsetY));
			}
		}
		//##############################
		// helpers methods
		private enum HeightZone { LAVA, SPAGHETTI, FLAT, MEDIUM, HALLS }
		private float getCaveSizeForZone(HeightZone zone, Random rand, double y) { // size of caves
			switch (zone) {
				case LAVA:	  	return 2.5F + rand.nextFloat() * 3.5F; // 2.5-6.0
				case SPAGHETTI: return 0.05F + rand.nextFloat() * 0.20F; // 0.1-0.2
				case FLAT:	  	return 1.6F + rand.nextFloat() * 1.4F; // 1.6-3.0
				case MEDIUM:	return 1.1F + rand.nextFloat() * 1.5F; // 1.1-2.6
				case HALLS:	 	return 4.0F + rand.nextFloat() * 9.0F; // 4.0-13.0 (larger)
				default:		return 2.0F;
			}
		}
		private float getPitchForZone(HeightZone zone, Random rand) { // Vertical naklon; + = down, - = up
			switch (zone) {
				case LAVA:	  	return (rand.nextFloat() - 0.5F) * 1.4F; // most vertical
				case SPAGHETTI: return (rand.nextFloat() - 0.5F) * 0.4F;
				case FLAT:	  	return (rand.nextFloat() - 0.5F) * 1.0F; // Large slope
				case MEDIUM:	return (rand.nextFloat() - 0.5F) * 0.75F;
				case HALLS:	 	return (rand.nextFloat() - 0.5F) * 0.3F; // Almost horizontal
				default:		return 0.0F;
			}
		}
		private int getLengthForZone(HeightZone zone, Random rand) { // Lenght of one chain
			switch (zone) {
				case LAVA:	  	return 80 + rand.nextInt(120);   // 80-200
				case SPAGHETTI: return 170 + rand.nextInt(170);  // 170-340 (longer!)
				case FLAT:	  	return 60 + rand.nextInt(120);	// 60-180
				case MEDIUM:	return 50 + rand.nextInt(110);	// 50-160
				case HALLS:	 	return 40 + rand.nextInt(90);	// 40-130 (shorter but wider)
				default:		return 100;
			}
		}
		private double getVerticalScaleForZone(HeightZone zone, Random rand, double y) { // Otnoshenie Vertical osi k Horizontal
			switch (zone) {
				case LAVA: 		return 0.5 + rand.nextDouble() * 1.5;  // Very flat
				case SPAGHETTI: return 0.9 + rand.nextDouble()  * 0.3;   // Almost round
				case FLAT: 		return 0.15 + rand.nextDouble()  * 0.25; // Very flattened
				case MEDIUM: 	return 0.4 + rand.nextDouble()  * 0.35;  // Moderately flattened
				case HALLS: 	return 0.75 + rand.nextDouble()  * 0.5;   // Elongated upward
				default:		return 1.0;
			}
		}
		private int getBranchCountForZone(HeightZone zone, Random rand) { // Count of branches
			switch (zone) {
				case LAVA:	  	return rand.nextInt(3);		  // 0-2 branches
				case SPAGHETTI: return 2 + rand.nextInt(4);	  // 2-5 branches (many!)
				case FLAT:	  	return 2 + rand.nextInt(2);	  // 2-3 branches
				case MEDIUM:	return 2 + rand.nextInt(2);	  // 2-3 branches
				case HALLS:	 	return rand.nextInt(2);		  // 0-1 branches
				default:		return 2;
			}
		}
		private float getYawSpreadForZone(HeightZone zone) { // Stepen' otklonenia v storony;  + = bol'she vilanie, - = bolee prami^e caves
			switch (zone) {
				case LAVA:	  	return (float)Math.PI / (float) 7;	   // Wide spread
				case SPAGHETTI: return (float)Math.PI / (float) 1.1;	//  Small spread
				case FLAT:	  	return (float)Math.PI / (float) 2.0;	 
				case MEDIUM:	return (float)Math.PI / (float) 2.5; 	// Average spread
				case HALLS:	 	return (float)Math.PI / (float) 7;
				default:		return (float)Math.PI / (float) 3;
			}
		}
		private float getBranchSizeMultiplier(HeightZone zone, Random rand) { // size of branches relative of Main; 0.5 = branch size /2 relof Main, 1.0 = tot zhe razmer
			switch (zone) {
				case SPAGHETTI: return 0.1F;// + rand.nextFloat() * 0.2F;  // 0.7-1.0
				case MEDIUM:	return 0.4F + rand.nextFloat() * 0.4F;  // 0.4-0.8
				case HALLS:	 	return 0.7F + rand.nextFloat() * 0.3F;  // 0.7-1.0 (thin branches)
				default:		return 0.6F + rand.nextFloat() * 0.4F;  // 0.5-1.0
			}
		}
		private double getBranchOffset(HeightZone zone) { // Max offset point of start new branch relative of Main cave;	+ = branches far of main, - = near from main
			switch (zone) {
				case SPAGHETTI: return 13.0;  // Near to the main branch
				case FLAT:	  	return 9.0;
				case MEDIUM:	return 15.0;
				case HALLS:	 	return 10.0; // Far from the main branch
				default:		return 6.0;
			}
		}
		private double getVerticalOffset(HeightZone zone) { // max vertical branch offset relative of Main point; + = branches can start most upper/down, - = branches at one level;
			switch (zone) { // raspredelenie po visote
				case LAVA:	  	return 12.0; // LARGE vertical offset
				case SPAGHETTI: return 15.0;  // SMALL vertical offset
				case MEDIUM:	return 15.0;
				case FLAT:		return 9.0;
				default:		return 10.0;
			}
		}
		private float getBranchLengthMultiplier(HeightZone zone, Random rand) { // mnozhitel dlini^ vetki otnositelno Main
			switch (zone) {
				case SPAGHETTI: return 0.7F + rand.nextFloat() * 1.0F;  // 0.6-1.7
				case HALLS:	 	return 0.3F + rand.nextFloat() * 0.3F;  // 0.3-0.6 (short branches)
				default:		return 0.5F + rand.nextFloat() * 0.6F;  // 0.5-1.1
			}
		}

		//##############################
		@Override
		protected void addTunnel(long seed, int chunkX, int chunkZ, ChunkPrimer primer, double posX, double posY, double posZ, float caveSize, 
				float caveAngle, float cavePitch, int startStep, int maxSteps, double verticalScale) {

			if (maxSteps < 20) maxSteps = 20;
			// Height limit
			if (posY > 250) posY = 250;
			if (posY < 7) posY = 7;
			super.addTunnel(seed, chunkX, chunkZ, primer, posX, posY, posZ, caveSize, caveAngle, cavePitch, startStep, maxSteps, verticalScale);
		}
		// #
	    @Override
	    protected boolean canReplaceBlock(IBlockState state, IBlockState upState) {
	        // allow replace obsidian
	        if (state.getBlock() == Blocks.OBSIDIAN || state.getBlock() == BlockDeepslate.block) {
	            return true;
	        }
	        return super.canReplaceBlock(state, upState);
	    }
		
	}

	public static class GenLayerBiomesCustom extends GenLayer {
		private Biome[] allowedBiomes = {Biome.REGISTRY.getObject(new ResourceLocation("astrotweaks:cavern")),};
		public GenLayerBiomesCustom(long seed) {super(seed);}
		@Override
		public int[] getInts(int x, int z, int width, int depth) {
			int[] dest = IntCache.getIntCache(width * depth);
			for (int dz = 0; dz < depth; dz++) {
				for (int dx = 0; dx < width; dx++) {
					this.initChunkSeed(dx + x, dz + z);
					dest[(dx + dz * width)] = Biome.getIdForBiome(this.allowedBiomes[nextInt(this.allowedBiomes.length)]);
				}
			}
			return dest;
		}
	}

	public static class BiomeProviderCustom extends BiomeProvider {
		private GenLayer genBiomes;
		private GenLayer biomeIndexLayer;
		private BiomeCache biomeCache;
		public BiomeProviderCustom() { this.biomeCache = new BiomeCache(this); }
		public BiomeProviderCustom(long seed) {
			this.biomeCache = new BiomeCache(this);
			GenLayer[] agenlayer = makeTheWorld(seed);
			this.genBiomes = agenlayer[0];
			this.biomeIndexLayer = agenlayer[1];
		}
		private GenLayer[] makeTheWorld(long seed) {
			GenLayer biomes = new GenLayerBiomesCustom(1);
			biomes = new GenLayerZoom(1000, biomes);
			biomes = new GenLayerZoom(1001, biomes);
			biomes = new GenLayerZoom(1002, biomes);
			biomes = new GenLayerZoom(1003, biomes);
			biomes = new GenLayerZoom(1004, biomes);
			biomes = new GenLayerZoom(1005, biomes);
			GenLayer genlayervoronoizoom = new GenLayerVoronoiZoom(10, biomes);
			biomes.initWorldGenSeed(seed);
			genlayervoronoizoom.initWorldGenSeed(seed);
			return new GenLayer[]{biomes, genlayervoronoizoom};
		}
		public BiomeProviderCustom(World world) { this(world.getSeed()); }
		@Override public void cleanupCache() { this.biomeCache.cleanupCache(); }
		@Override public Biome getBiome(BlockPos pos) { return this.getBiome(pos, null); }
		@Override
		public Biome getBiome(BlockPos pos, Biome defaultBiome) {
			return this.biomeCache.getBiome(pos.getX(), pos.getZ(), defaultBiome);
		}
		@Override
		public Biome[] getBiomes(Biome[] oldBiomeList, int x, int z, int width, int depth) {
			return this.getBiomes(oldBiomeList, x, z, width, depth, true);
		}
		@Override
		public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height) {
			IntCache.resetIntCache();
			if (biomes == null || biomes.length < width * height) {
				biomes = new Biome[width * height];
			}
			int[] aint = this.genBiomes.getInts(x, z, width, height);
			try {
				for (int i = 0; i < width * height; ++i) {
					biomes[i] = Biome.getBiome(aint[i], Biomes.DEFAULT);
				}
				return biomes;
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("RawBiomeBlock");
				crashreportcategory.addCrashSection("biomes[] size", Integer.valueOf(biomes.length));
				crashreportcategory.addCrashSection("x", Integer.valueOf(x));
				crashreportcategory.addCrashSection("z", Integer.valueOf(z));
				crashreportcategory.addCrashSection("w", Integer.valueOf(width));
				crashreportcategory.addCrashSection("h", Integer.valueOf(height));
				throw new ReportedException(crashreport);
			}
		}
		@Override
		public Biome[] getBiomes(@Nullable Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag) {
			IntCache.resetIntCache();
			if (listToReuse == null || listToReuse.length < width * length) {
				listToReuse = new Biome[width * length];
			}
			if (cacheFlag && width == 16 && length == 16 && (x & 15) == 0 && (z & 15) == 0) {
				Biome[] abiome = this.biomeCache.getCachedBiomes(x, z);
				System.arraycopy(abiome, 0, listToReuse, 0, width * length);
				return listToReuse;
			} else {
				int[] aint = this.biomeIndexLayer.getInts(x, z, width, length);
				for (int i = 0; i < width * length; ++i) {
					listToReuse[i] = Biome.getBiome(aint[i], Biomes.DEFAULT);
				}
				return listToReuse;
			}
		}
		@Override
		public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed) {
			IntCache.resetIntCache();
			int i = x - radius >> 2;
			int j = z - radius >> 2;
			int k = x + radius >> 2;
			int l = z + radius >> 2;
			int i1 = k - i + 1;
			int j1 = l - j + 1;
			int[] aint = this.genBiomes.getInts(i, j, i1, j1);
			try {
				for (int k1 = 0; k1 < i1 * j1; ++k1) {
					Biome biome = Biome.getBiome(aint[k1]);
					if (!allowed.contains(biome)) {
						return false;
					}
				}
				return true;
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Layer");
				crashreportcategory.addCrashSection("Layer", this.genBiomes.toString());
				crashreportcategory.addCrashSection("x", Integer.valueOf(x));
				crashreportcategory.addCrashSection("z", Integer.valueOf(z));
				crashreportcategory.addCrashSection("radius", Integer.valueOf(radius));
				crashreportcategory.addCrashSection("allowed", allowed);
				throw new ReportedException(crashreport);
			}
		}
		@Override
		@Nullable
		public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random) {
			IntCache.resetIntCache();
			int i = x - range >> 2;
			int j = z - range >> 2;
			int k = x + range >> 2;
			int l = z + range >> 2;
			int i1 = k - i + 1;
			int j1 = l - j + 1;
			int[] aint = this.genBiomes.getInts(i, j, i1, j1);
			BlockPos blockpos = null;
			int k1 = 0;
			for (int l1 = 0; l1 < i1 * j1; ++l1) {
				int i2 = i + l1 % i1 << 2;
				int j2 = j + l1 / i1 << 2;
				Biome biome = Biome.getBiome(aint[l1]);
				if (biomes.contains(biome) && (blockpos == null || random.nextInt(k1 + 1) == 0)) {
					blockpos = new BlockPos(i2, 0, j2);
					++k1;
				}
			}
			return blockpos;
		}
	}
}
