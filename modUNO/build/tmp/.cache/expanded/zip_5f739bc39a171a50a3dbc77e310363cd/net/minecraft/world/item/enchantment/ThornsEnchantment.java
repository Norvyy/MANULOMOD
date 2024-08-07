package net.minecraft.world.item.enchantment;

import java.util.Map.Entry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ThornsEnchantment extends Enchantment {
    private static final float CHANCE_PER_LEVEL = 0.15F;

    public ThornsEnchantment(Enchantment.EnchantmentDefinition pDefinition) {
        super(pDefinition);
    }

    @Override
    public void doPostHurt(LivingEntity pUser, Entity pAttacker, int pLevel) {
        RandomSource randomsource = pUser.getRandom();
        Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.getRandomItemWith(Enchantments.THORNS, pUser);
        if (shouldHit(pLevel, randomsource)) {
            if (pAttacker != null) {
                pAttacker.hurt(pUser.damageSources().thorns(pUser), (float)getDamage(pLevel, randomsource));
            }

            if (entry != null) {
                entry.getValue().hurtAndBreak(2, pUser, entry.getKey());
            }
        }
    }

    public static boolean shouldHit(int pLevel, RandomSource pRandom) {
        return pLevel <= 0 ? false : pRandom.nextFloat() < 0.15F * (float)pLevel;
    }

    public static int getDamage(int pLevel, RandomSource pRandom) {
        return pLevel > 10 ? pLevel - 10 : 1 + pRandom.nextInt(4);
    }
}