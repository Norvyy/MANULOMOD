package net.minecraft.world.entity.animal;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record FrogVariant(ResourceLocation texture) {
    public static final ResourceKey<FrogVariant> TEMPERATE = createKey("temperate");
    public static final ResourceKey<FrogVariant> WARM = createKey("warm");
    public static final ResourceKey<FrogVariant> COLD = createKey("cold");

    private static ResourceKey<FrogVariant> createKey(String pName) {
        return ResourceKey.create(Registries.FROG_VARIANT, new ResourceLocation(pName));
    }

    public static FrogVariant bootstrap(Registry<FrogVariant> pRegistry) {
        register(pRegistry, TEMPERATE, "textures/entity/frog/temperate_frog.png");
        register(pRegistry, WARM, "textures/entity/frog/warm_frog.png");
        return register(pRegistry, COLD, "textures/entity/frog/cold_frog.png");
    }

    private static FrogVariant register(Registry<FrogVariant> pRegistry, ResourceKey<FrogVariant> pKey, String pTexture) {
        return Registry.register(pRegistry, pKey, new FrogVariant(new ResourceLocation(pTexture)));
    }
}