package net.minecraft.data.loot.packs;

import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public class VanillaShearingLoot implements LootTableSubProvider {
    @Override
    public void generate(HolderLookup.Provider p_328989_, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> p_330494_) {
        p_330494_.accept(BuiltInLootTables.BOGGED_SHEAR, LootTable.lootTable());
    }
}