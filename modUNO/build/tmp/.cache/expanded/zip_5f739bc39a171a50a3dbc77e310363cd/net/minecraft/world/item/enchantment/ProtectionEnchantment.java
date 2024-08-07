package net.minecraft.world.item.enchantment;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class ProtectionEnchantment extends Enchantment {
    public final ProtectionEnchantment.Type type;

    public ProtectionEnchantment(Enchantment.EnchantmentDefinition pDefinition, ProtectionEnchantment.Type pType) {
        super(pDefinition);
        this.type = pType;
    }

    @Override
    public int getDamageProtection(int pLevel, DamageSource pSource) {
        if (pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return 0;
        } else if (this.type == ProtectionEnchantment.Type.ALL) {
            return pLevel;
        } else if (this.type == ProtectionEnchantment.Type.FIRE && pSource.is(DamageTypeTags.IS_FIRE)) {
            return pLevel * 2;
        } else if (this.type == ProtectionEnchantment.Type.FALL && pSource.is(DamageTypeTags.IS_FALL)) {
            return pLevel * 3;
        } else if (this.type == ProtectionEnchantment.Type.EXPLOSION && pSource.is(DamageTypeTags.IS_EXPLOSION)) {
            return pLevel * 2;
        } else {
            return this.type == ProtectionEnchantment.Type.PROJECTILE && pSource.is(DamageTypeTags.IS_PROJECTILE) ? pLevel * 2 : 0;
        }
    }

    @Override
    public boolean checkCompatibility(Enchantment pEnch) {
        if (pEnch instanceof ProtectionEnchantment protectionenchantment) {
            return this.type == protectionenchantment.type
                ? false
                : this.type == ProtectionEnchantment.Type.FALL || protectionenchantment.type == ProtectionEnchantment.Type.FALL;
        } else {
            return super.checkCompatibility(pEnch);
        }
    }

    public static int getFireAfterDampener(LivingEntity pLivingEntity, int pLevel) {
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, pLivingEntity);
        if (i > 0) {
            pLevel -= Mth.floor((float)pLevel * (float)i * 0.15F);
        }

        return pLevel;
    }

    public static double getExplosionKnockbackAfterDampener(LivingEntity pLivingEntity, double pDamage) {
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, pLivingEntity);
        if (i > 0) {
            pDamage *= Mth.clamp(1.0 - (double)i * 0.15, 0.0, 1.0);
        }

        return pDamage;
    }

    public static enum Type {
        ALL,
        FIRE,
        FALL,
        EXPLOSION,
        PROJECTILE;
    }
}