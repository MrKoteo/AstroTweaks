package astrotweaks.qts;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import astrotweaks.qts.BlockQTPSupressor;
import astrotweaks.qts.SuppressorEventHandler;

public final class SuppressorManager {
    // For each world: chunk -> a set of suppressor positions covering this chunk
    private static final Map<World, Map<ChunkPos, Set<BlockPos>>> worldChunkMap = new ConcurrentHashMap<>();
    // For every world: position -> radius (quick access)
    private static final Map<World, Map<BlockPos, Integer>> worldPosRangeMap = new ConcurrentHashMap<>();

    // Add suppressor
    public static void addSuppressor(World world, BlockPos pos, int range) {
        if (world.isRemote) return;
        Map<BlockPos, Integer> posMap = worldPosRangeMap.computeIfAbsent(world, k -> new ConcurrentHashMap<>());
        posMap.put(pos, range);
        updateChunksForSuppressor(world, pos, range, true);
    }

    // Remove suppressor
    public static void removeSuppressor(World world, BlockPos pos) {
        if (world.isRemote) return;
        Map<BlockPos, Integer> posMap = worldPosRangeMap.get(world);
        if (posMap != null) {
            Integer range = posMap.remove(pos);
            if (range != null) {
                updateChunksForSuppressor(world, pos, range, false);
            }
            if (posMap.isEmpty()) {
                worldPosRangeMap.remove(world);
            }
        }
    }

    // Update radius
    public static void updateRange(World world, BlockPos pos, int newRange) {
        if (world.isRemote) return;
        Map<BlockPos, Integer> posMap = worldPosRangeMap.get(world);
        if (posMap != null) {
            Integer oldRange = posMap.get(pos);
            if (oldRange != null && oldRange != newRange) {
                // del old chunks
                updateChunksForSuppressor(world, pos, oldRange, false);
                // add new
                posMap.put(pos, newRange);
                updateChunksForSuppressor(world, pos, newRange, true);
            }
        }
    }

    // Check if teleportation from from to to (in the same world) is blocked
    public static boolean isTeleportationBlocked(World world, BlockPos from, BlockPos to) {
        if (world.isRemote) return false;
        return isPositionBlocked(world, from) || isPositionBlocked(world, to);
    }

    // Check whether the point is in the area of any suppressor
    public static boolean isPositionBlocked(World world, BlockPos pos) {
        if (world.isRemote) return false;
        Map<ChunkPos, Set<BlockPos>> chunkMap = worldChunkMap.get(world);
        if (chunkMap == null) return false;
        ChunkPos cp = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
        Set<BlockPos> suppressors = chunkMap.get(cp);
        if (suppressors == null) return false;
        Map<BlockPos, Integer> posMap = worldPosRangeMap.get(world);
        if (posMap == null) return false;
        for (BlockPos sp : suppressors) {
            Integer range = posMap.get(sp);
            if (range != null && sp.distanceSq(pos) <= (long) range * range) {
                return true;
            }
        }
        return false;
    }

    // Update the chunk binding for the suppressor (add or remove)
    private static void updateChunksForSuppressor(World world, BlockPos pos, int range, boolean add) {
        if (world.isRemote) return;
        Map<ChunkPos, Set<BlockPos>> chunkMap = worldChunkMap.computeIfAbsent(world, k -> new ConcurrentHashMap<>());
        int minX = (pos.getX() - range) >> 4;
        int maxX = (pos.getX() + range) >> 4;
        int minZ = (pos.getZ() - range) >> 4;
        int maxZ = (pos.getZ() + range) >> 4;
        for (int cx = minX; cx <= maxX; cx++) {
            for (int cz = minZ; cz <= maxZ; cz++) {
                ChunkPos cp = new ChunkPos(cx, cz);
                Set<BlockPos> set = chunkMap.computeIfAbsent(cp, k -> ConcurrentHashMap.newKeySet());
                if (add) {
                    set.add(pos);
                } else {
                    set.remove(pos);
                    if (set.isEmpty()) {
                        chunkMap.remove(cp);
                    }
                }
            }
        }
        if (chunkMap.isEmpty()) {
            worldChunkMap.remove(world);
        }
    }
}