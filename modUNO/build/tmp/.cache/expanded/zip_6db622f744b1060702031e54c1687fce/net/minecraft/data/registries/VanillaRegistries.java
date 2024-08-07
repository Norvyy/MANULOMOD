package net.minecraft.data.registries;

import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.data.worldgen.NoiseData;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.data.worldgen.StructureSets;
import net.minecraft.data.worldgen.Structures;
import net.minecraft.data.worldgen.biome.BiomeData;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.network.chat.ChatType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.animal.WolfVariants;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPresets;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

public class VanillaRegistries {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
        .add(Registries.DIMENSION_TYPE, DimensionTypes::bootstrap)
        .add(Registries.CONFIGURED_CARVER, (RegistrySetBuilder.RegistryBootstrap)Carvers::bootstrap)
        .add(Registries.CONFIGURED_FEATURE, (RegistrySetBuilder.RegistryBootstrap)FeatureUtils::bootstrap)
        .add(Registries.PLACED_FEATURE, PlacementUtils::bootstrap)
        .add(Registries.STRUCTURE, Structures::bootstrap)
        .add(Registries.STRUCTURE_SET, StructureSets::bootstrap)
        .add(Registries.PROCESSOR_LIST, ProcessorLists::bootstrap)
        .add(Registries.TEMPLATE_POOL, Pools::bootstrap)
        .add(Registries.BIOME, BiomeData::bootstrap)
        .add(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterLists::bootstrap)
        .add(Registries.NOISE, NoiseData::bootstrap)
        .add(Registries.DENSITY_FUNCTION, NoiseRouterData::bootstrap)
        .add(Registries.NOISE_SETTINGS, NoiseGeneratorSettings::bootstrap)
        .add(Registries.WORLD_PRESET, WorldPresets::bootstrap)
        .add(Registries.FLAT_LEVEL_GENERATOR_PRESET, FlatLevelGeneratorPresets::bootstrap)
        .add(Registries.CHAT_TYPE, ChatType::bootstrap)
        .add(Registries.TRIM_PATTERN, TrimPatterns::bootstrap)
        .add(Registries.TRIM_MATERIAL, TrimMaterials::bootstrap)
        .add(Registries.WOLF_VARIANT, WolfVariants::bootstrap)
        .add(Registries.DAMAGE_TYPE, DamageTypes::bootstrap)
        .add(Registries.BANNER_PATTERN, BannerPatterns::bootstrap);
    public static final List<? extends net.minecraft.resources.ResourceKey<? extends net.minecraft.core.Registry<?>>> DATAPACK_REGISTRY_KEYS = BUILDER.getEntryKeys();

    private static void validateThatAllBiomeFeaturesHaveBiomeFilter(HolderLookup.Provider p_256242_) {
        validateThatAllBiomeFeaturesHaveBiomeFilter(p_256242_.lookupOrThrow(Registries.PLACED_FEATURE), p_256242_.lookupOrThrow(Registries.BIOME));
    }

    public static void validateThatAllBiomeFeaturesHaveBiomeFilter(HolderGetter<PlacedFeature> p_272963_, HolderLookup<Biome> p_273693_) {
        p_273693_.listElements().forEach(p_256326_ -> {
            ResourceLocation resourcelocation = p_256326_.key().location();
            List<HolderSet<PlacedFeature>> list = p_256326_.value().getGenerationSettings().features();
            list.stream().flatMap(HolderSet::stream).forEach(p_256657_ -> p_256657_.unwrap().ifLeft(p_325923_ -> {
                    Holder.Reference<PlacedFeature> reference = p_272963_.getOrThrow((ResourceKey<PlacedFeature>)p_325923_);
                    if (!validatePlacedFeature(reference.value())) {
                        Util.logAndPauseIfInIde("Placed feature " + p_325923_.location() + " in biome " + resourcelocation + " is missing BiomeFilter.biome()");
                    }
                }).ifRight(p_325920_ -> {
                    if (!validatePlacedFeature(p_325920_)) {
                        Util.logAndPauseIfInIde("Placed inline feature in biome " + p_256326_ + " is missing BiomeFilter.biome()");
                    }
                }));
        });
    }

    private static boolean validatePlacedFeature(PlacedFeature p_255656_) {
        return p_255656_.placement().contains(BiomeFilter.biome());
    }

    public static HolderLookup.Provider createLookup() {
        RegistryAccess.Frozen registryaccess$frozen = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        HolderLookup.Provider holderlookup$provider = BUILDER.build(registryaccess$frozen);
        validateThatAllBiomeFeaturesHaveBiomeFilter(holderlookup$provider);
        return holderlookup$provider;
    }
}
