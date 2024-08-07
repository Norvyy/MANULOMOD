package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BannerPatternTags;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;

public class UpdateOneTwentyOneBannerPatternTagsProvider extends TagsProvider<BannerPattern> {
    public UpdateOneTwentyOneBannerPatternTagsProvider(
        PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagsProvider.TagLookup<BannerPattern>> pParentProvider
    ) {
        super(pOutput, Registries.BANNER_PATTERN, pLookupProvider, pParentProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(BannerPatternTags.PATTERN_ITEM_FLOW).add(BannerPatterns.FLOW);
        this.tag(BannerPatternTags.PATTERN_ITEM_GUSTER).add(BannerPatterns.GUSTER);
    }
}