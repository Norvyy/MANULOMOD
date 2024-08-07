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

    public EnchantmentTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, FeatureFlagSet pEnabledFeatures) {
        this(pOutput, pLookupProvider, pEnabledFeatures, "vanilla", null);
    }

    public EnchantmentTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, FeatureFlagSet pEnabledFeatures, String modId, @org.jetbrains.annotations.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
        super(pOutput, Registries.ENCHANTMENT, pLookupProvider, p_328708_ -> p_328708_.builtInRegistryHolder().key(), modId, existingFileHelper);
        this.enabledFeatures = pEnabledFeatures;
    }

    protected void tooltipOrder(HolderLookup.Provider pProvider, Enchantment... pEnchantments) {
        this.tag(EnchantmentTags.TOOLTIP_ORDER).add(pEnchantments);
        Set<Enchantment> set = Set.of(pEnchantments);
        List<String> list = pProvider.lookupOrThrow(Registries.ENCHANTMENT)
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
