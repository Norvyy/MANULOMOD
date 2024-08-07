package net.minecraft.world.level.chunk.status;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.Blender;

public class ChunkStatusTasks {
    private static boolean isLighted(ChunkAccess pChunk) {
        return pChunk.getStatus().isOrAfter(ChunkStatus.LIGHT) && pChunk.isLightCorrect();
    }

    static CompletableFuture<ChunkAccess> generateEmpty(
        WorldGenContext pContext, ChunkStatus pStatus, Executor pExecutor, ToFullChunk pToFullChunk, List<ChunkAccess> pCache, ChunkAccess pLoadingChunk
    ) {
        return CompletableFuture.completedFuture(pLoadingChunk);
    }

    static CompletableFuture<ChunkAccess> loadPassThrough(WorldGenContext pContext, ChunkStatus pStatus, ToFullChunk pToFullChunk, ChunkAccess pLoadingChunk) {
        return CompletableFuture.completedFuture(pLoadingChunk);
    }

    static CompletableFuture<ChunkAccess> generateStructureStarts(
        WorldGenContext pContext, ChunkStatus pStatus, Executor pExecutor, ToFullChunk pToFullChunk, List<ChunkAccess> pCache, ChunkAccess pLoadingChunk
    ) {
        ServerLevel serverlevel = pContext.level();
        if (serverlevel.getServer().getWorldData().worldGenOptions().generateStructures()) {
            pContext.generator()
                .createStructures(serverlevel.registryAccess(), serverlevel.getChunkSource().getGeneratorState(), serverlevel.structureManager(), pLoadingChunk, pContext.structureManager());
        }

        serverlevel.onStructureStartsAvailable(pLoadingChunk);
        return CompletableFuture.completedFuture(pLoadingChunk);
    }

    static CompletableFuture<ChunkAccess> loadStructureStarts(WorldGenContext pContext, ChunkStatus pStatus, ToFullChunk pToFullChunk, ChunkAccess pLoadingChunk) {
        pContext.level().onStructureStartsAvailable(pLoadingChunk);
        return CompletableFuture.completedFuture(pLoadingChunk);
    }

    static CompletableFuture<ChunkAccess> generateStructureReferences(
        WorldGenContext pContext, ChunkStatus pStatus, Executor pExecutor, ToFullChunk pToFullChunk, List<ChunkAccess> pCache, ChunkAccess pLoadingChunk
    ) {
        ServerLevel serverlevel = pContext.level();
        WorldGenRegion worldgenregion = new WorldGenRegion(serverlevel, pCache, pStatus, -1);
        pContext.generator().createReferences(worldgenregion, serverlevel.structureManager().forWorldGenRegion(worldgenregion), pLoadingChunk);
        return CompletableFuture.completedFuture(pLoadingChunk);
    }

    static CompletableFuture<ChunkAccess> generateBiomes(
        WorldGenContext pContext, ChunkStatus pStatus, Executor pExecutor, ToFullChunk pToFullChunk, List<ChunkAccess> pCache, ChunkAccess pLoadingChunk
    ) {
        ServerLevel serverlevel = pContext.level();
        WorldGenRegion worldgenregion = new WorldGenRegion(serverlevel, pCache, pStatus, -1);
        return pContext.generator()
            .createBiomes(
                pExecutor, serverlevel.getChunkSource().randomState(), Blender.of(worldgenregion), serverlevel.structureManager().forWorldGenRegion(worldgenregion), pLoadingChunk
            );
    }

    static CompletableFuture<ChunkAccess> generateNoise(
        WorldGenContext pContext, ChunkStatus pStatus, Executor pExecutor, ToFullChunk pToFullChunk, List<ChunkAccess> pCache, ChunkAccess pLoadingChunk
    ) {
        ServerLevel serverlevel = pContext.level();
        WorldGenRegion worldgenregion = new WorldGenRegion(serverlevel, pCache, pStatus, 0);
        return pContext.generator()
            .fillFromNoise(
                pExecutor, Blender.of(worldgenregion), serverlevel.getChunkSource().randomState(), serverlevel.structureManager().forWorldGenRegion(worldgenregion), pLoadingChunk
            )
            .thenApply(p_328030_ -> {
                if (p_328030_ instanceof ProtoChunk protochunk) {
                    BelowZeroRetrogen belowzeroretrogen = protochunk.getBelowZeroRetrogen();
                    if (belowzeroretrogen != null) {
                        BelowZeroRetrogen.replaceOldBedrock(protochunk);
                        if (belowzeroretrogen.hasBedrockHoles()) {
                            belowzeroretrogen.applyBedrockMask(protochunk);
                        }
                    }
                }

                return (ChunkAccess)p_328030_;
            });
    }

    static CompletableFuture<ChunkAccess> generateSurface(
        WorldGenContext pContext, ChunkStatus pStatus, Executor pExecutor, ToFullChunk pToFullChunk, List<ChunkAccess> pCache, ChunkAccess pLoadingChunk
    ) {
        ServerLevel serverlevel = pContext.level();
        WorldGenRegion worldgenregion = new WorldGenRegion(serverlevel, pCache, pStatus, 0);
        pContext.generator().buildSurface(worldgenregion, serverlevel.structureManager().forWorldGenRegion(worldgenregion), serverlevel.getChunkSource().randomState(), pLoadingChunk);
        return CompletableFuture.completedFuture(pLoadingChunk);
    }

    static CompletableFuture<ChunkAccess> generateCarvers(
        WorldGenContext pContext, ChunkStatus pStatus, Executor pExecutor, ToFullChunk pToFullChunk, List<ChunkAccess> pCache, ChunkAccess pLoadingChunk
    ) {
        ServerLevel serverlevel = pContext.level();
        WorldGenRegion worldgenregion = new WorldGenRegion(serverlevel, pCache, pStatus, 0);
        if (pLoadingChunk instanceof ProtoChunk protochunk) {
            Blender.addAroundOldChunksCarvingMaskFilter(worldgenregion, protochunk);
        }

        pContext.generator()
            .applyCarvers(
                worldgenregion,
                serverlevel.getSeed(),
                serverlevel.getChunkSource().randomState(),
                serverlevel.getBiomeManager(),
                serverlevel.structureManager().forWorldGenRegion(worldgenregion),
                pLoadingChunk,
                GenerationStep.Carving.AIR
            );
        return CompletableFuture.completedFuture(pLoadingChunk);
    }

    static CompletableFuture<ChunkAccess> generateFeatures(
        WorldGenContext pContext, ChunkStatus pStatus, Executor pExecutor, ToFullChunk pToFullChunk, List<ChunkAccess> pCache, ChunkAccess pLoadingChunk
    ) {
        ServerLevel serverlevel = pContext.level();
        Heightmap.primeHeightmaps(
            pLoadingChunk,
            EnumSet.of(Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE)
        );
        WorldGenRegion worldgenregion = new WorldGenRegion(serverlevel, pCache, pStatus, 1);
        pContext.generator().applyBiomeDecoration(worldgenregion, pLoadingChunk, serverlevel.structureManager().forWorldGenRegion(worldgenregion));
        Blender.generateBorderTicks(worldgenregion, pLoadingChunk);
        return CompletableFuture.completedFuture(pLoadingChunk);
    }

    static CompletableFuture<ChunkAccess> generateInitializeLight(
        WorldGenContext pContext, ChunkStatus pStatus, Executor pExecutor, ToFullChunk pToFullChunk, List<ChunkAccess> pCache, ChunkAccess pLoadingChunk
    ) {
        return initializeLight(pContext.lightEngine(), pLoadingChunk);
    }

    static CompletableFuture<ChunkAccess> loadInitializeLight(WorldGenContext pContext, ChunkStatus pStatus, ToFullChunk pToFullChunk, ChunkAccess pLoadingChunk) {
        return initializeLight(pContext.lightEngine(), pLoadingChunk);
    }

    private static CompletableFuture<ChunkAccess> initializeLight(ThreadedLevelLightEngine pLightEngine, ChunkAccess pLoadingChunk) {
        pLoadingChunk.initializeLightSources();
        ((ProtoChunk)pLoadingChunk).setLightEngine(pLightEngine);
        boolean flag = isLighted(pLoadingChunk);
        return pLightEngine.initializeLight(pLoadingChunk, flag);
    }

    static CompletableFuture<ChunkAccess> generateLight(
        WorldGenContext pContext, ChunkStatus pStatus, Executor pExecutor, ToFullChunk pToFullChunk, List<ChunkAccess> pCache, ChunkAccess pLoadingChunk
    ) {
        return lightChunk(pContext.lightEngine(), pLoadingChunk);
    }

    static CompletableFuture<ChunkAccess> loadLight(WorldGenContext pContext, ChunkStatus pStatus, ToFullChunk pToFullChunk, ChunkAccess pLoadingChunk) {
        return lightChunk(pContext.lightEngine(), pLoadingChunk);
    }

    private static CompletableFuture<ChunkAccess> lightChunk(ThreadedLevelLightEngine pLightEngine, ChunkAccess pLoadingChunk) {
        boolean flag = isLighted(pLoadingChunk);
        return pLightEngine.lightChunk(pLoadingChunk, flag);
    }

    static CompletableFuture<ChunkAccess> generateSpawn(
        WorldGenContext pContext, ChunkStatus pStatus, Executor pExecutor, ToFullChunk pToFullChunk, List<ChunkAccess> pCache, ChunkAccess pLoadingChunk
    ) {
        if (!pLoadingChunk.isUpgrading()) {
            pContext.generator().spawnOriginalMobs(new WorldGenRegion(pContext.level(), pCache, pStatus, -1));
        }

        return CompletableFuture.completedFuture(pLoadingChunk);
    }

    static CompletableFuture<ChunkAccess> generateFull(
        WorldGenContext pContext, ChunkStatus pStatus, Executor pExecutor, ToFullChunk pToFullChunk, List<ChunkAccess> pCache, ChunkAccess pLoadingChunk
    ) {
        return pToFullChunk.apply(pLoadingChunk);
    }

    static CompletableFuture<ChunkAccess> loadFull(WorldGenContext pContext, ChunkStatus pStatus, ToFullChunk pToFullChunk, ChunkAccess pLoadingChunk) {
        return pToFullChunk.apply(pLoadingChunk);
    }
}