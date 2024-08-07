package net.minecraft.world.damagesource;

import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class CombatRules {
    public static final float MAX_ARMOR = 20.0F;
    public static final float ARMOR_PROTECTION_DIVIDER = 25.0F;
    public static final float BASE_ARMOR_TOUGHNESS = 2.0F;
    public static final float MIN_ARMOR_RATIO = 0.2F;
    private static final int NUM_ARMOR_ITEMS = 4;

    public static float getDamageAfterAbsorb(float pDamage, DamageSource pSource, float pArmor, float pArmorToughness) {
        float f = 2.0F + pArmorToughness / 4.0F;
        float f1 = Mth.clamp(pArmor - pDamage / f, pArmor * 0.2F, 20.0F);
        float f2 = f1 / 25.0F;
        float f3 = EnchantmentHelper.calculateArmorBreach(pSource.getEntity(), f2);
        float f4 = 1.0F - f3;
        return pDamage * f4;
    }

    public static float getDamageAfterMagicAbsorb(float pDamage, float pEnchantModifiers) {
        float f = Mth.clamp(pEnchantModifiers, 0.0F, 20.0F);
        return pDamage * (1.0F - f / 25.0F);
    }
}