package net.minecraft.data.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicates;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public abstract class EntityLootSubProvider implements LootTableSubProvider {
    protected static final EntityPredicate.Builder ENTITY_ON_FIRE = EntityPredicate.Builder.entity()
        .flags(EntityFlagsPredicate.Builder.flags().setOnFire(true));
    private static final Set<EntityType<?>> SPECIAL_LOOT_TABLE_TYPES = ImmutableSet.of(
        EntityType.PLAYER, EntityType.ARMOR_STAND, EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER
    );
    private final FeatureFlagSet allowed;
    private final FeatureFlagSet required;
    private final Map<EntityType<?>, Map<ResourceKey<LootTable>, LootTable.Builder>> map = Maps.newHashMap();

    protected EntityLootSubProvider(FeatureFlagSet pEnabledFeatures) {
        this(pEnabledFeatures, pEnabledFeatures);
    }

    protected EntityLootSubProvider(FeatureFlagSet pAllowed, FeatureFlagSet pRequired) {
        this.allowed = pAllowed;
        this.required = pRequired;
    }

    protected static LootTable.Builder createSheepTable(ItemLike pWoolItem) {
        return LootTable.lootTable()
            .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(pWoolItem)))
            .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(NestedLootTable.lootTableReference(EntityType.SHEEP.getDefaultLootTable())));
    }

    public abstract void generate();

    protected java.util.stream.Stream<EntityType<?>> getKnownEntityTypes() {
       return BuiltInRegistries.ENTITY_TYPE.stream();
    }

    @Override
    public void generate(HolderLookup.Provider pRegistries, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> pGenerator) {
        this.generate();
        Set<ResourceKey<LootTable>> set = new HashSet<>();
        this.getKnownEntityTypes()
            .map(EntityType::builtInRegistryHolder)
            .forEach(
                p_266624_ -> {
                    EntityType<?> entitytype = p_266624_.value();
                    if (entitytype.isEnabled(this.allowed)) {
                        if (canHaveLootTable(entitytype)) {
                            Map<ResourceKey<LootTable>, LootTable.Builder> map = this.map.remove(entitytype);
                            ResourceKey<LootTable> resourcekey = entitytype.getDefaultLootTable();
                            if (resourcekey != BuiltInLootTables.EMPTY
                                && entitytype.isEnabled(this.required)
                                && (map == null || !map.containsKey(resourcekey))) {
                                throw new IllegalStateException(
                                    String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", resourcekey, p_266624_.key().location())
                                );
                            }

                            if (map != null) {
                                map.forEach(
                                    (p_329509_, p_250972_) -> {
                                        if (!set.add((ResourceKey<LootTable>)p_329509_)) {
                                            throw new IllegalStateException(
                                                String.format(Locale.ROOT, "Duplicate loottable '%s' for '%s'", p_329509_, p_266624_.key().location())
                                            );
                                        } else {
                                            pGenerator.accept((ResourceKey<LootTable>)p_329509_, p_250972_);
                                        }
                                    }
                                );
                            }
                        } else {
                            Map<ResourceKey<LootTable>, LootTable.Builder> map1 = this.map.remove(entitytype);
                            if (map1 != null) {
                                throw new IllegalStateException(
                                    String.format(
                                        Locale.ROOT,
                                        "Weird loottables '%s' for '%s', not a LivingEntity so should not have loot",
                                        map1.keySet().stream().map(p_325849_ -> p_325849_.location().toString()).collect(Collectors.joining(",")),
                                        p_266624_.key().location()
                                    )
                                );
                            }
                        }
                    }
                }
            );
        if (!this.map.isEmpty()) {
            throw new IllegalStateException("Created loot tables for entities not supported by datapack: " + this.map.keySet());
        }
    }

    protected boolean canHaveLootTable(EntityType<?> pEntityType) {
        return SPECIAL_LOOT_TABLE_TYPES.contains(pEntityType) || pEntityType.getCategory() != MobCategory.MISC;
    }

    protected LootItemCondition.Builder killedByFrog() {
        return DamageSourceCondition.hasDamageSource(
            DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().of(EntityType.FROG))
        );
    }

    protected LootItemCondition.Builder killedByFrogVariant(ResourceKey<FrogVariant> p_330466_) {
        return DamageSourceCondition.hasDamageSource(
            DamageSourcePredicate.Builder.damageType()
                .source(
                    EntityPredicate.Builder.entity()
                        .of(EntityType.FROG)
                        .subPredicate(EntitySubPredicates.frogVariant(BuiltInRegistries.FROG_VARIANT.getHolderOrThrow(p_330466_)))
                )
        );
    }

    protected void add(EntityType<?> pEntityType, LootTable.Builder pBuilder) {
        this.add(pEntityType, pEntityType.getDefaultLootTable(), pBuilder);
    }

    protected void add(EntityType<?> pEntityType, ResourceKey<LootTable> pDefaultLootTable, LootTable.Builder pBuilder) {
        this.map.computeIfAbsent(pEntityType, p_251466_ -> new HashMap<>()).put(pDefaultLootTable, pBuilder);
    }
}
