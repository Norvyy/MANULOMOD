package net.minecraft.data.loot.packs;

import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class UpdateOneTwentyOneEquipmentLoot implements LootTableSubProvider {
    @Override
    public void generate(HolderLookup.Provider pRegistries, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> pGenerator) {
        HolderLookup.RegistryLookup<TrimPattern> registrylookup = pRegistries.lookup(Registries.TRIM_PATTERN).orElseThrow();
        HolderLookup.RegistryLookup<TrimMaterial> registrylookup1 = pRegistries.lookup(Registries.TRIM_MATERIAL).orElseThrow();
        ArmorTrim armortrim = new ArmorTrim(
            registrylookup1.get(TrimMaterials.COPPER).orElseThrow(), registrylookup.get(TrimPatterns.FLOW).orElseThrow()
        );
        ArmorTrim armortrim1 = new ArmorTrim(
            registrylookup1.get(TrimMaterials.COPPER).orElseThrow(), registrylookup.get(TrimPatterns.BOLT).orElseThrow()
        );
        pGenerator.accept(
            BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(NestedLootTable.inlineLootTable(trialChamberEquipment(Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, armortrim1).build()).setWeight(4))
                        .add(NestedLootTable.inlineLootTable(trialChamberEquipment(Items.IRON_HELMET, Items.IRON_CHESTPLATE, armortrim).build()).setWeight(2))
                        .add(NestedLootTable.inlineLootTable(trialChamberEquipment(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, armortrim).build()).setWeight(1))
                )
        );
        pGenerator.accept(
            BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_MELEE,
            LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(NestedLootTable.lootTableReference(BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER)))
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.IRON_SWORD).setWeight(4))
                        .add(
                            LootItem.lootTableItem(Items.IRON_SWORD)
                                .apply(new SetEnchantmentsFunction.Builder().withEnchantment(Enchantments.SHARPNESS, ConstantValue.exactly(1.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(Items.IRON_SWORD)
                                .apply(new SetEnchantmentsFunction.Builder().withEnchantment(Enchantments.KNOCKBACK, ConstantValue.exactly(1.0F)))
                        )
                        .add(LootItem.lootTableItem(Items.DIAMOND_SWORD))
                )
        );
        pGenerator.accept(
            BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_RANGED,
            LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(NestedLootTable.lootTableReference(BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER)))
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.BOW).setWeight(2))
                        .add(
                            LootItem.lootTableItem(Items.BOW)
                                .apply(new SetEnchantmentsFunction.Builder().withEnchantment(Enchantments.POWER, ConstantValue.exactly(1.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(Items.BOW)
                                .apply(new SetEnchantmentsFunction.Builder().withEnchantment(Enchantments.PUNCH, ConstantValue.exactly(1.0F)))
                        )
                )
        );
    }

    public static LootTable.Builder trialChamberEquipment(Item pHelmetItem, Item pChestplateItem, ArmorTrim pArmorTrim) {
        return LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .when(LootItemRandomChanceCondition.randomChance(0.5F))
                    .add(
                        LootItem.lootTableItem(pHelmetItem)
                            .apply(SetComponentsFunction.setComponent(DataComponents.TRIM, pArmorTrim))
                            .apply(
                                new SetEnchantmentsFunction.Builder()
                                    .withEnchantment(Enchantments.PROTECTION, ConstantValue.exactly(4.0F))
                                    .withEnchantment(Enchantments.PROJECTILE_PROTECTION, ConstantValue.exactly(4.0F))
                                    .withEnchantment(Enchantments.FIRE_PROTECTION, ConstantValue.exactly(4.0F))
                            )
                    )
            )
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .when(LootItemRandomChanceCondition.randomChance(0.5F))
                    .add(
                        LootItem.lootTableItem(pChestplateItem)
                            .apply(SetComponentsFunction.setComponent(DataComponents.TRIM, pArmorTrim))
                            .apply(
                                new SetEnchantmentsFunction.Builder()
                                    .withEnchantment(Enchantments.PROTECTION, ConstantValue.exactly(4.0F))
                                    .withEnchantment(Enchantments.PROJECTILE_PROTECTION, ConstantValue.exactly(4.0F))
                                    .withEnchantment(Enchantments.FIRE_PROTECTION, ConstantValue.exactly(4.0F))
                            )
                    )
            );
    }
}