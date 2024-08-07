package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;

public class UpdateOneTwentyOneStructureTagsProvider extends TagsProvider<Structure> {
    public UpdateOneTwentyOneStructureTagsProvider(
        PackOutput p_333592_, CompletableFuture<HolderLookup.Provider> p_330073_, CompletableFuture<TagsProvider.TagLookup<Structure>> p_334333_
    ) {
        super(p_333592_, Registries.STRUCTURE, p_330073_, p_334333_);
    }

    @Override
    protected void addTags(HolderLookup.Provider p_333058_) {
        this.tag(StructureTags.ON_TRIAL_CHAMBERS_MAPS).add(BuiltinStructures.TRIAL_CHAMBERS);
    }
}