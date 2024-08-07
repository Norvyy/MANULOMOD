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
        PackOutput p_331885_, CompletableFuture<HolderLookup.Provider> p_332184_, CompletableFuture<TagsProvider.TagLookup<BannerPattern>> p_331555_
    ) {
        super(p_331885_, Registries.BANNER_PATTERN, p_332184_, p_331555_);
    }

    @Override
    protected void addTags(HolderLookup.Provider p_335769_) {
        this.tag(BannerPatternTags.PATTERN_ITEM_FLOW).add(BannerPatterns.FLOW);
        this.tag(BannerPatternTags.PATTERN_ITEM_GUSTER).add(BannerPatterns.GUSTER);
    }
}