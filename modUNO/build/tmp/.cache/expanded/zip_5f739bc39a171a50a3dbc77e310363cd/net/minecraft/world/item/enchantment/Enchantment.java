package net.minecraft.world.item.enchantment;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class Enchantment implements FeatureElement, net.minecraftforge.common.extensions.IForgeEnchantment {
    private final Enchantment.EnchantmentDefinition definition;
    @Nullable
    protected String descriptionId;
    private final Holder.Reference<Enchantment> builtInRegistryHolder = BuiltInRegistries.ENCHANTMENT.createIntrusiveHolder(this);

    public static Enchantment.Cost constantCost(int pCost) {
        return new Enchantment.Cost(pCost, 0);
    }

    public static Enchantment.Cost dynamicCost(int pBase, int pPerLevel) {
        return new Enchantment.Cost(pBase, pPerLevel);
    }

    public static Enchantment.EnchantmentDefinition definition(
        TagKey<Item> pSupportedItems,
        TagKey<Item> pPrimaryItems,
        int pWeight,
        int pMaxLevel,
        Enchantment.Cost pMinCost,
        Enchantment.Cost pMaxCost,
        int pAnvilCost,
        EquipmentSlot... pSlots
    ) {
        return new Enchantment.EnchantmentDefinition(
            pSupportedItems, Optional.of(pPrimaryItems), pWeight, pMaxLevel, pMinCost, pMaxCost, pAnvilCost, FeatureFlags.DEFAULT_FLAGS, pSlots
        );
    }

    public static Enchantment.EnchantmentDefinition definition(
        TagKey<Item> pSupportedItems, int pWeight, int pMaxLevel, Enchantment.Cost pMinCost, Enchantment.Cost pMaxCost, int pAnvilCost, EquipmentSlot... pSlots
    ) {
        return new Enchantment.EnchantmentDefinition(
            pSupportedItems, Optional.empty(), pWeight, pMaxLevel, pMinCost, pMaxCost, pAnvilCost, FeatureFlags.DEFAULT_FLAGS, pSlots
        );
    }

    public static Enchantment.EnchantmentDefinition definition(
        TagKey<Item> pSupportedItems,
        int pWeight,
        int pMaxLevel,
        Enchantment.Cost pMinCost,
        Enchantment.Cost pMaxCost,
        int pAnvilCost,
        FeatureFlagSet pRequiredFeatures,
        EquipmentSlot... pSlots
    ) {
        return new Enchantment.EnchantmentDefinition(pSupportedItems, Optional.empty(), pWeight, pMaxLevel, pMinCost, pMaxCost, pAnvilCost, pRequiredFeatures, pSlots);
    }

    @Nullable
    public static Enchantment byId(int pId) {
        return BuiltInRegistries.ENCHANTMENT.byId(pId);
    }

    public Enchantment(Enchantment.EnchantmentDefinition pDefinition) {
        this.definition = pDefinition;
    }

    public Map<EquipmentSlot, ItemStack> getSlotItems(LivingEntity pEntity) {
        Map<EquipmentSlot, ItemStack> map = Maps.newEnumMap(EquipmentSlot.class);

        for (EquipmentSlot equipmentslot : this.definition.slots()) {
            ItemStack itemstack = pEntity.getItemBySlot(equipmentslot);
            if (!itemstack.isEmpty()) {
                map.put(equipmentslot, itemstack);
            }
        }

        return map;
    }

    public final TagKey<Item> getSupportedItems() {
        return this.definition.supportedItems();
    }

    public final boolean isPrimaryItem(ItemStack pStack) {
        return this.definition.primaryItems.isEmpty() || pStack.is(this.definition.primaryItems.get());
    }

    public final int getWeight() {
        return this.definition.weight();
    }

    public final int getAnvilCost() {
        return this.definition.anvilCost();
    }

    public final int getMinLevel() {
        return 1;
    }

    public final int getMaxLevel() {
        return this.definition.maxLevel();
    }

    public final int getMinCost(int pLevel) {
        return this.definition.minCost().calculate(pLevel);
    }

    public final int getMaxCost(int pLevel) {
        return this.definition.maxCost().calculate(pLevel);
    }

    public int getDamageProtection(int pLevel, DamageSource pSource) {
        return 0;
    }

    @Deprecated // Forge: Use ItemStack aware version in IForgeEnchantment
    public float getDamageBonus(int pLevel, @Nullable EntityType<?> pCreatureType) {
        return 0.0F;
    }

    public final boolean isCompatibleWith(Enchantment pOther) {
        return this.checkCompatibility(pOther) && pOther.checkCompatibility(this);
    }

    protected boolean checkCompatibility(Enchantment pOther) {
        return this != pOther;
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("enchantment", BuiltInRegistries.ENCHANTMENT.getKey(this));
        }

        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    public Component getFullname(int pLevel) {
        MutableComponent mutablecomponent = Component.translatable(this.getDescriptionId());
        if (this.isCurse()) {
            mutablecomponent.withStyle(ChatFormatting.RED);
        } else {
            mutablecomponent.withStyle(ChatFormatting.GRAY);
        }

        if (pLevel != 1 || this.getMaxLevel() != 1) {
            mutablecomponent.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + pLevel));
        }

        return mutablecomponent;
    }

    public boolean canEnchant(ItemStack pStack) {
        return pStack.getItem().builtInRegistryHolder().is(this.definition.supportedItems());
    }

    public void doPostAttack(LivingEntity pAttacker, Entity pTarget, int pLevel) {
    }

    public void doPostHurt(LivingEntity pTarget, Entity pAttacker, int pLevel) {
    }

    public void doPostItemStackHurt(LivingEntity pAttacker, Entity pTarget, int pLevel) {
    }

    public boolean isTreasureOnly() {
        return false;
    }

    public boolean isCurse() {
        return false;
    }

    public boolean isTradeable() {
        return true;
    }

    public boolean isDiscoverable() {
        return true;
    }

    @Deprecated
    public Holder.Reference<Enchantment> builtInRegistryHolder() {
        return this.builtInRegistryHolder;
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.definition.requiredFeatures();
    }

    public static record Cost(int base, int perLevel) {
        public int calculate(int pLevel) {
            return this.base + this.perLevel * (pLevel - 1);
        }
    }

    public static record EnchantmentDefinition(
        TagKey<Item> supportedItems,
        Optional<TagKey<Item>> primaryItems,
        int weight,
        int maxLevel,
        Enchantment.Cost minCost,
        Enchantment.Cost maxCost,
        int anvilCost,
        FeatureFlagSet requiredFeatures,
        EquipmentSlot[] slots
    ) {
    }
}
