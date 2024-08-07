package net.minecraft.data.tags;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

public class UpdateOneTwentyOneDamageTypes {
    public static void bootstrap(BootstrapContext<DamageType> p_331535_) {
        p_331535_.register(DamageTypes.WIND_CHARGE, new DamageType("mob", 0.1F));
    }
}