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
    private static boolean isLighted(ChunkAccess p_332575_) {
        return p_332575_.getStatus().isOrAfter(ChunkStatus.LIGHT) && p_332575_.isLightCorrect();
    }

    static CompletableFuture<ChunkAccess> generateEmpty(
        WorldGenContext p_327738_, ChunkStatus p_333908_, Executor p_334944_, ToFullChunk p_336366_, List<ChunkAccess> p_329997_, ChunkAccess p_334414_
    ) {
        return CompletableFuture.completedFuture(p_334414_);
    }

    static CompletableFuture<ChunkAccess> loadPassThrough(WorldGenContext p_328698_, ChunkStatus p_333721_, ToFullChunk p_331952_, ChunkAccess p_336233_) {
        return CompletableFuture.completedFuture(p_336233_);
    }

    static CompletableFuture<ChunkAccess> generateStructureStarts(
        WorldGenContext p_333948_, ChunkStatus p_331528_, Executor p_332991_, ToFullChunk p_332908_, List<ChunkAccess> p_331472_, ChunkAccess p_332160_
    ) {
        ServerLevel serverlevel = p_333948_.level();
        if (serverlevel.getServer().getWorldData().worldGenOptions().generateStructures()) {
            p_333948_.generator()
                .createStructures(serverlevel.registryAccess(), serverlevel.getChunkSource().getGeneratorState(), serverlevel.structureManager(), p_332160_, p_333948_.structureManager());
        }

        serverlevel.onStructureStartsAvailable(p_332160_);
        return CompletableFuture.completedFuture(p_332160_);
    }

    static CompletableFuture<ChunkAccess> loadStructureStarts(WorldGenContext p_330330_, ChunkStatus p_327952_, ToFullChunk p_333344_, ChunkAccess p_335780_) {
        p_330330_.level().onStructureStartsAvailable(p_335780_);
        return CompletableFuture.completedFuture(p_335780_);
    }

    static CompletableFuture<ChunkAccess> generateStructureReferences(
        WorldGenContext p_334657_, ChunkStatus p_335796_, Executor p_328245_, ToFullChunk p_334727_, List<ChunkAccess> p_328000_, ChunkAccess p_335107_
    ) {
        ServerLevel serverlevel = p_334657_.level();
        WorldGenRegion worldgenregion = new WorldGenRegion(serverlevel, p_328000_, p_335796_, -1);
        p_334657_.generator().createReferences(worldgenregion, serverlevel.structureManager().forWorldGenRegion(worldgenregion), p_335107_);
        return CompletableFuture.completedFuture(p_335107_);
    }

    static CompletableFuture<ChunkAccess> generateBiomes(
        WorldGenContext p_334080_, ChunkStatus p_334258_, Executor p_330763_, ToFullChunk p_332437_, List<ChunkAccess> p_328972_, ChunkAccess p_329246_
    ) {
        ServerLevel serverlevel = p_334080_.level();
        WorldGenRegion worldgenregion = new WorldGenRegion(serverlevel, p_328972_, p_334258_, -1);
        return p_334080_.generator()
            .createBiomes(
                p_330763_, serverlevel.getChunkSource().randomState(), Blender.of(worldgenregion), serverlevel.structureManager().forWorldGenRegion(worldgenregion), p_329246_
            );
    }

    static CompletableFuture<ChunkAccess> generateNoise(
        WorldGenContext p_336010_, ChunkStatus p_330181_, Executor p_335460_, ToFullChunk p_329890_, List<ChunkAccess> p_329399_, ChunkAccess p_331391_
    ) {
        ServerLevel serverlevel = p_336010_.level();
        WorldGenRegion worldgenregion = new WorldGenRegion(serverlevel, p_329399_, p_330181_, 0);
        return p_336010_.generator()
            .fillFromNoise(
                p_335460_, Blender.of(worldgenregion), serverlevel.getChunkSource().randomState(), serverlevel.structureManager().forWorldGenRegion(worldgenregion), p_331391_
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
        WorldGenContext p_331242_, ChunkStatus p_334030_, Executor p_330927_, ToFullChunk p_333532_, List<ChunkAccess> p_330810_, ChunkAccess p_329153_
    ) {
        ServerLevel serverlevel = p_331242_.level();
        WorldGenRegion worldgenregion = new WorldGenRegion(serverlevel, p_330810_, p_334030_, 0);
        p_331242_.generator().buildSurface(worldgenregion, serverlevel.structureManager().forWorldGenRegion(worldgenregion), serverlevel.getChunkSource().randomState(), p_329153_);
        return CompletableFuture.completedFuture(p_329153_);
    }

    static CompletableFuture<ChunkAccess> generateCarvers(
        WorldGenContext p_334842_, ChunkStatus p_336206_, Executor p_332025_, ToFullChunk p_330438_, List<ChunkAccess> p_328596_, ChunkAccess p_334473_
    ) {
        ServerLevel serverlevel = p_334842_.level();
        WorldGenRegion worldgenregion = new WorldGenRegion(serverlevel, p_328596_, p_336206_, 0);
        if (p_334473_ instanceof ProtoChunk protochunk) {
            Blender.addAroundOldChunksCarvingMaskFilter(worldgenregion, protochunk);
        }

        p_334842_.generator()
            .applyCarvers(
                worldgenregion,
                serverlevel.getSeed(),
                serverlevel.getChunkSource().randomState(),
                serverlevel.getBiomeManager(),
                serverlevel.structureManager().forWorldGenRegion(worldgenregion),
                p_334473_,
                GenerationStep.Carving.AIR
            );
        return CompletableFuture.completedFuture(p_334473_);
    }

    static CompletableFuture<ChunkAccess> generateFeatures(
        WorldGenContext p_330189_, ChunkStatus p_329895_, Executor p_330502_, ToFullChunk p_329017_, List<ChunkAccess> p_329119_, ChunkAccess p_332579_
    ) {
        ServerLevel serverlevel = p_330189_.level();
        Heightmap.primeHeightmaps(
            p_332579_,
            EnumSet.of(Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE)
        );
        WorldGenRegion worldgenregion = new WorldGenRegion(serverlevel, p_329119_, p_329895_, 1);
        p_330189_.generator().applyBiomeDecoration(worldgenregion, p_332579_, serverlevel.structureManager().forWorldGenRegion(worldgenregion));
        Blender.generateBorderTicks(worldgenregion, p_332579_);
        return CompletableFuture.completedFuture(p_332579_);
    }

    static CompletableFuture<ChunkAccess> generateInitializeLight(
        WorldGenContext p_332413_, ChunkStatus p_328122_, Executor p_328770_, ToFullChunk p_328410_, List<ChunkAccess> p_329168_, ChunkAccess p_330555_
    ) {
        return initializeLight(p_332413_.lightEngine(), p_330555_);
    }

    static CompletableFuture<ChunkAccess> loadInitializeLight(WorldGenContext p_330395_, ChunkStatus p_332979_, ToFullChunk p_334794_, ChunkAccess p_333299_) {
        return initializeLight(p_330395_.lightEngine(), p_333299_);
    }

    private static CompletableFuture<ChunkAccess> initializeLight(ThreadedLevelLightEngine p_330346_, ChunkAccess p_334426_) {
        p_334426_.initializeLightSources();
        ((ProtoChunk)p_334426_).setLightEngine(p_330346_);
        boolean flag = isLighted(p_334426_);
        return p_330346_.initializeLight(p_334426_, flag);
    }

    static CompletableFuture<ChunkAccess> generateLight(
        WorldGenContext p_329903_, ChunkStatus p_331038_, Executor p_327805_, ToFullChunk p_329744_, List<ChunkAccess> p_333018_, ChunkAccess p_328729_
    ) {
        return lightChunk(p_329903_.lightEngine(), p_328729_);
    }

    static CompletableFuture<ChunkAccess> loadLight(WorldGenContext p_331792_, ChunkStatus p_329237_, ToFullChunk p_329001_, ChunkAccess p_328388_) {
        return lightChunk(p_331792_.lightEngine(), p_328388_);
    }

    private static CompletableFuture<ChunkAccess> lightChunk(ThreadedLevelLightEngine p_332619_, ChunkAccess p_329146_) {
        boolean flag = isLighted(p_329146_);
        return p_332619_.lightChunk(p_329146_, flag);
    }

    static CompletableFuture<ChunkAccess> generateSpawn(
        WorldGenContext p_329644_, ChunkStatus p_333967_, Executor p_334858_, ToFullChunk p_331727_, List<ChunkAccess> p_333311_, ChunkAccess p_329794_
    ) {
        if (!p_329794_.isUpgrading()) {
            p_329644_.generator().spawnOriginalMobs(new WorldGenRegion(p_329644_.level(), p_333311_, p_333967_, -1));
        }

        return CompletableFuture.completedFuture(p_329794_);
    }

    static CompletableFuture<ChunkAccess> generateFull(
        WorldGenContext p_329930_, ChunkStatus p_329028_, Executor p_331683_, ToFullChunk p_335663_, List<ChunkAccess> p_334524_, ChunkAccess p_334958_
    ) {
        return p_335663_.apply(p_334958_);
    }

    static CompletableFuture<ChunkAccess> loadFull(WorldGenContext p_330808_, ChunkStatus p_330720_, ToFullChunk p_328107_, ChunkAccess p_335635_) {
        return p_328107_.apply(p_335635_);
    }
}