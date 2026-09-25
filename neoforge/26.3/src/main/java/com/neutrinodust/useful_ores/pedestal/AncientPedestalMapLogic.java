package com.neutrinodust.useful_ores.pedestal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;









public final class AncientPedestalMapLogic {
    public static final ResourceKey<Structure> ANCIENT_PEDESTAL_STRUCTURE = ResourceKey.create(
            Registries.STRUCTURE,
            Identifier.fromNamespaceAndPath("useful_ores", "ancient_pedestal_chamber")
    );

    public static final TagKey<Structure> ANCIENT_PEDESTAL_STRUCTURES = TagKey.create(
            Registries.STRUCTURE,
            Identifier.fromNamespaceAndPath("useful_ores", "ancient_pedestal_chamber")
    );

    
    private static final int SEARCH_MAX_RING = 128;
    private static final int MAX_CANDIDATES = 16384;

    private AncientPedestalMapLogic() {}

    public static ItemStack createMap(ServerLevel level, int originX, int originZ, boolean excludeNearest) {
        if (level.dimension() != Level.OVERWORLD) return ItemStack.EMPTY;

        Holder<Structure> structure = level.registryAccess()
                .lookupOrThrow(Registries.STRUCTURE)
                .getOrThrow(ANCIENT_PEDESTAL_STRUCTURE);

        ChunkGeneratorStructureState state = level.getChunkSource().getGeneratorState();
        RandomSpreadStructurePlacement placement = state.getPlacementsForStructure(structure).stream()
                .filter(RandomSpreadStructurePlacement.class::isInstance)
                .map(RandomSpreadStructurePlacement.class::cast)
                .findFirst()
                .orElse(null);
        if (placement == null) return ItemStack.EMPTY;

        AncientPedestalMapAssignments assignments = AncientPedestalMapAssignments.get(level);
        BlockPos target = findNearestUnclaimedCenter(level, placement, assignments, originX, originZ, excludeNearest);
        if (target == null) return ItemStack.EMPTY;

        long targetKey = targetKey(target.getX(), target.getZ());
        if (!assignments.claim(targetKey)) return ItemStack.EMPTY;

        return makeFilledMap(level, target);
    }

    private static BlockPos findNearestUnclaimedCenter(
            ServerLevel level,
            RandomSpreadStructurePlacement placement,
            AncientPedestalMapAssignments assignments,
            int originX,
            int originZ,
            boolean excludeNearest) {

        final int spacing = placement.spacing();
        final int originChunkX = Math.floorDiv(originX, 16);
        final int originChunkZ = Math.floorDiv(originZ, 16);
        final int baseRegionX = Math.floorDiv(originChunkX, spacing);
        final int baseRegionZ = Math.floorDiv(originChunkZ, spacing);

        BlockPos best = null;
        long bestDistance = Long.MAX_VALUE;
        int candidateCount = 0;

        




        for (int ring = 0; ring <= SEARCH_MAX_RING; ring++) {
            for (int dx = -ring; dx <= ring; dx++) {
                for (int dz = -ring; dz <= ring; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != ring) continue;

                    int regionX = baseRegionX + dx;
                    int regionZ = baseRegionZ + dz;
                    ChunkPos candidateChunk = placement.getPotentialStructureChunk(
                            level.getSeed(), regionX, regionZ);
                    candidateCount++;
                    if (candidateCount > MAX_CANDIDATES) return best;

                    BlockPos candidateCenter = new BlockPos(
                            candidateChunk.getMiddleBlockX(),
                            level.getMinY(),
                            candidateChunk.getMiddleBlockZ());

                    if (excludeNearest && isSamePedestal(candidateCenter, originX, originZ)) continue;
                    if (assignments.isClaimed(targetKey(candidateCenter.getX(), candidateCenter.getZ()))) continue;

                    long distance = squaredDistance(originX, originZ, candidateCenter);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        best = candidateCenter;
                    }
                }
            }

            




            if (best != null && bestDistance <= minimumPossibleSquaredDistanceToUnsearchedRing(
                    originX, originZ, baseRegionX, baseRegionZ, spacing, ring + 1)) {
                return best;
            }
        }

        return best;
    }

    private static boolean isSamePedestal(BlockPos candidate, int originX, int originZ) {
        
        long dx = (long) candidate.getX() - originX;
        long dz = (long) candidate.getZ() - originZ;
        return dx * dx + dz * dz <= 16L * 16L;
    }

    private static long targetKey(int x, int z) {
        return ((long) x << 32) ^ (z & 0xffffffffL);
    }

    private static long minimumPossibleSquaredDistanceToUnsearchedRing(
            int originX,
            int originZ,
            int baseRegionX,
            int baseRegionZ,
            int spacing,
            int ring) {
        long best = Long.MAX_VALUE;
        for (int dx = -ring; dx <= ring; dx++) {
            for (int dz = -ring; dz <= ring; dz++) {
                if (Math.max(Math.abs(dx), Math.abs(dz)) != ring) continue;
                best = Math.min(best, squaredDistanceToRegion(
                        originX, originZ, baseRegionX + dx, baseRegionZ + dz, spacing));
            }
        }
        return best;
    }

    private static long squaredDistanceToRegion(
            int originX,
            int originZ,
            int regionX,
            int regionZ,
            int spacing) {
        long minX = ((long) regionX * spacing) * 16L + 8L;
        long maxX = ((long) (regionX * spacing + spacing - 1)) * 16L + 8L;
        long minZ = ((long) regionZ * spacing) * 16L + 8L;
        long maxZ = ((long) (regionZ * spacing + spacing - 1)) * 16L + 8L;

        long dx = distanceToRange(originX, minX, maxX);
        long dz = distanceToRange(originZ, minZ, maxZ);
        return dx * dx + dz * dz;
    }

    private static long distanceToRange(long value, long min, long max) {
        if (value < min) return min - value;
        if (value > max) return value - max;
        return 0L;
    }

    private static ItemStack makeFilledMap(ServerLevel level, BlockPos target) {
        
        ItemStack map = MapItem.create(level, target.getX(), target.getZ(), (byte) 1, true, true);
        MapItem.renderBiomePreviewMap(level, map);
        MapItemSavedData.addTargetDecoration(map, target, "+", MapDecorationTypes.RED_X);
        map.set(net.minecraft.core.component.DataComponents.ITEM_NAME,
                Component.translatable("filled_map.ancient_pedestal"));
        return map;
    }

    private static long squaredDistance(int originX, int originZ, BlockPos target) {
        long dx = (long) target.getX() - originX;
        long dz = (long) target.getZ() - originZ;
        return dx * dx + dz * dz;
    }
}
