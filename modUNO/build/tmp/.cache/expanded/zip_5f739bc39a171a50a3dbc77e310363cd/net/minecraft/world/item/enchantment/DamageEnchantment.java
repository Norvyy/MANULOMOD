package net.minecraft.world.item.enchantment;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class DamageEnchantment extends Enchantment {
    private final Optional<TagKey<EntityType<?>>> targets;

    public DamageEnchantment(Enchantment.EnchantmentDefinition pDefinition, Optional<TagKey<EntityType<?>>> pTargets) {
        super(pDefinition);
        this.targets = pTargets;
    }

    @Override
    public float getDamageBonus(int pLevel, @Nullable EntityType<?> pCreatureType) {
        if (this.targets.isEmpty()) {
            return 1.0F + (float)Math.max(0, pLevel - 1) * 0.5F;
        } else {
            return pCreatureType != null && pCreatureType.is(this.targets.get()) ? (float)pLevel * 2.5F : 0.0F;
        }
    }

    @Override
    public boolean checkCompatibility(Enchantment pEnch) {
        return !(pEnch instanceof DamageEnchantment);
    }

    @Override
    public void doPostAttack(LivingEntity pUser, Entity pTarget, int pLevel) {
        if (this.targets.isPresent()
            && pTarget instanceof LivingEntity livingentity
            && this.targets.get() == EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS
            && pLevel > 0
            && livingentity.getType().is(this.targets.get())) {
            int i = 20 + pUser.getRandom().nextInt(10 * pLevel);
            livingentity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, i, 3));
        }
    }
}