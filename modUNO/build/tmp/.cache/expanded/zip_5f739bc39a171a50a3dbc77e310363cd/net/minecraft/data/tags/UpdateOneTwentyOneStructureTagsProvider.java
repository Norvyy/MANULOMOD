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
        PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagsProvider.TagLookup<Structure>> pParentProvider
    ) {
        super(pOutput, Registries.STRUCTURE, pLookupProvider, pParentProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(StructureTags.ON_TRIAL_CHAMBERS_MAPS).add(BuiltinStructures.TRIAL_CHAMBERS);
    }
}