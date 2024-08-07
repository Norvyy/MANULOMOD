package net.minecraft.data.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class UpdateOneTwentyOnePools {
    public static final ResourceKey<StructureTemplatePool> EMPTY = createKey("empty");

    public static ResourceKey<StructureTemplatePool> createKey(String pLocation) {
        return ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(pLocation));
    }

    public static void register(BootstrapContext<StructureTemplatePool> pContext, String pName, StructureTemplatePool pPool) {
        Pools.register(pContext, pName, pPool);
    }

    public static void bootstrap(BootstrapContext<StructureTemplatePool> pContext) {
        TrialChambersStructurePools.bootstrap(pContext);
    }
}