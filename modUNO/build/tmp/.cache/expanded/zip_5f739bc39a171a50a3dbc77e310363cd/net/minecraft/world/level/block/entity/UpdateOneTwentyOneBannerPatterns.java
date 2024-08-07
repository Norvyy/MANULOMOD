package net.minecraft.world.level.block.entity;

import net.minecraft.data.worldgen.BootstrapContext;

public interface UpdateOneTwentyOneBannerPatterns {
    static void bootstrap(BootstrapContext<BannerPattern> pContext) {
        BannerPatterns.register(pContext, BannerPatterns.FLOW);
        BannerPatterns.register(pContext, BannerPatterns.GUSTER);
    }
}