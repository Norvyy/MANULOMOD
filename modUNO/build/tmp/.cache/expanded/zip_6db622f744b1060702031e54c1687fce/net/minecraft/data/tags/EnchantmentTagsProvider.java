package net.minecraft.data.tags;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.enchantment.Enchantment;

public abstract class EnchantmentTagsProvider extends IntrinsicHolderTagsProvider<Enchantment> {
    private final FeatureFlagSet enabledFeatures;

    public EnchantmentTagsProvider(PackOutput p_332794_, CompletableFuture<HolderLookup.Provider> p_331070_, FeatureFlagSet p_328046_) {
        this(p_332794_, p_331070_, p_328046_, "vanilla", null);
    }

    public EnchantmentTagsProvider(PackOutput p_332794_, CompletableFuture<HolderLookup.Provider> p_331070_, FeatureFlagSet p_328046_, String modId, @org.jetbrains.annotations.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
        super(p_332794_, Registries.ENCHANTMENT, p_331070_, p_328708_ -> p_328708_.builtInRegistryHolder().key(), modId, existingFileHelper);
        this.enabledFeatures = p_328046_;
    }

    protected void tooltipOrder(HolderLookup.Provider p_335292_, Enchantment... p_336160_) {
        this.tag(EnchantmentTags.TOOLTIP_ORDER).add(p_336160_);
        Set<Enchantment> set = Set.of(p_336160_);
        List<String> list = p_335292_.lookupOrThrow(Registries.ENCHANTMENT)
            .listElements()
            .filter(p_328295_ -> p_328295_.value().requiredFeatures().isSubsetOf(this.enabledFeatures))
            .filter(p_329769_ -> !set.contains(p_329769_.value()))
            .map(Holder::getRegisteredName)
            .collect(Collectors.toList());
        if (!list.isEmpty()) {
            throw new IllegalStateException("Not all enchantments were registered for tooltip ordering. Missing: " + String.join(", ", list));
        }
    }
}
