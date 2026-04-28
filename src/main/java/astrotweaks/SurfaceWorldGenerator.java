package astrotweaks.world;

import java.util.Random;
import java.util.Set;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import astrotweaks.AstrotweaksModVariables;
import astrotweaks.ModVariables;


public final class SurfaceWorldGenerator {
	private SurfaceWorldGenerator() {}
	/**
	* Attempts to place blocks on the surface within the chunk.
	* @param rand Random - source of randomness
	* @param chunkXorBase chunkX: can be chunk index (x) *or* block coordinate; the function does not multiply itself
	* @param chunkZorBase chunkZ: similarly for Z
	* @param world world
	* @param block block to set (not null)
	* @param allowedBiomes ResourceLocation set of allowed biomes
	* @param attempts number of attempts per chunk
	* @param minY minimum Y for searching surface
	* @param maxY maximum Y for searching surface
	*/
    // Blocks on which decoration CANNOT be placed
    private static final Set<Block> FORBIDDEN_BLOCKS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
        Blocks.COBBLESTONE,
        Blocks.PLANKS,
        Blocks.RED_MUSHROOM_BLOCK,
        Blocks.BROWN_MUSHROOM_BLOCK
        // forbidden
    ))); 

	public static void generateSurface(Random rand, int chunkX, int chunkZ, World world, Block block, Set<Biome> allowedBiomes, double gen_attempts, int minY, int maxY) {
		//try {
			if (world == null/* || block == null*/) return;
			if (gen_attempts <= 0) return;
			if (minY > maxY) return;
			if (allowedBiomes == null) allowedBiomes = ModVariables.GEN_DEFAULT_BIOMES;
			if (gen_attempts > 999) gen_attempts = 999;
			if (minY < 1) minY = 1;
			if (maxY > 255) maxY = 250; // max Y scan Pos
		//} catch (Exception e) {
			//System.out.println("Error init AstroTweaks World Decorator! Please, edit mod config.");
		//}

	    int attempts; // check attempts
	    if (gen_attempts < 1.0) {
	        if (rand.nextFloat() >= gen_attempts) return;
	        attempts = 1;
	    } else {
	        int guaranteed = (int) gen_attempts;
	        double fractional = gen_attempts - guaranteed;
	        attempts = guaranteed;
	        if (fractional > 0.0 && rand.nextDouble() < fractional) {
	            attempts++;
	        }
	    }
		// biome check at chunk center
		BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos(chunkX + 8, 64, chunkZ + 8);
		//probe.setPos(chunkX + 8, 64, chunkZ + 8);
		Biome centerBiome = world.getBiome(probe);
		if (!allowedBiomes.contains(centerBiome)) return;

		BlockPos.MutableBlockPos placePos = new BlockPos.MutableBlockPos();

		for (int i = 0; i < attempts; i++) {
			// needs a Border outline of Chunk
            int x = chunkX + 1 + rand.nextInt(14);
            int z = chunkZ + 1 + rand.nextInt(14);
			// find Y up Solid block in HEIGHT_LINE
			int topY = world.getHeight(x, z);

			//BlockPos probe = new BlockPos(x, topY - 1, z);
			//BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos(x, topY - 1, z);
			//probe.setPos(x, world.getHeight(x, z) - 1, z);
            //IBlockState probeState = world.getBlockState(probe); 

			probe.setPos(x, topY - 1, z);
			IBlockState probeState = world.getBlockState(probe);
            while (probe.getY() > minY) {
            	boolean air = world.isAirBlock(probe);
            	if (!air && !isLeavesBlock(probeState) && !isLogBlock(probeState)) break;
                probe.move(EnumFacing.DOWN);
                probeState = world.getBlockState(probe);
            }
            int surfaceY = probe.getY();
            if (surfaceY < minY || surfaceY > maxY) continue;

            // Check surface block is valid
            Block groundBlock = probeState.getBlock();
            //boolean baseIsSolid = probeState.isOpaqueCube() &&  probeState.isFullCube() && probeState.isSideSolid(world, probe, EnumFacing.UP);
		    if (!probeState.isOpaqueCube()) continue; // cheap check first
		    if (!probeState.isSideSolid(world, probe, EnumFacing.UP) || !probeState.isFullCube()) continue;
		    if (FORBIDDEN_BLOCKS.contains(groundBlock)) continue;
            
            //if (!baseIsSolid) continue;
            //if (FORBIDDEN_BLOCKS.contains(groundBlock)) continue;

		    placePos.setPos(probe.getX(), probe.getY() + 1, probe.getZ());
		    if (placePos.getY() < 0 || placePos.getY() > 255) continue;

		    IBlockState placeState = world.getBlockState(placePos);
		    Block placeBlock = placeState.getBlock();
		    if (placeBlock == block) continue;
		    if (!world.isAirBlock(placePos) && !placeState.getMaterial().isReplaceable() && !isLeavesBlock(placeState)) continue;

			// place
			world.setBlockState(placePos, block.getDefaultState(), 2);
		}
	}
	private static boolean isLeavesBlock(IBlockState state) {
		if (state == null) return false;
		Block b = state.getBlock();
		// mods can also add their own leaves - you can check the material
		return b == Blocks.LEAVES || b == Blocks.LEAVES2 || state.getMaterial() == net.minecraft.block.material.Material.LEAVES;
	}
    private static boolean isLogBlock(IBlockState state) {
        if (state == null) return false;
        Block b = state.getBlock();
        return b == Blocks.LOG || b == Blocks.LOG2;
    }
}
