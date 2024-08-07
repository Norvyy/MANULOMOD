package net.minecraft.world.item.armortrim;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Items;

public class UpdateOneTwentyOneArmorTrims {
    public static void bootstrap(BootstrapContext<TrimPattern> pContext) {
        TrimPatterns.register(pContext, Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, TrimPatterns.FLOW);
        TrimPatterns.register(pContext, Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, TrimPatterns.BOLT);
    }
}