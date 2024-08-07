package net.minecraft.world.item.enchantment;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class DigDurabilityEnchantment extends Enchantment {
    protected DigDurabilityEnchantment(Enchantment.EnchantmentDefinition pDefinition) {
        super(pDefinition);
    }

    public static boolean shouldIgnoreDurabilityDrop(ItemStack pStack, int pLevel, RandomSource pRandom) {
        return pStack.getItem() instanceof ArmorItem && pRandom.nextFloat() < 0.6F ? false : pRandom.nextInt(pLevel + 1) > 0;
    }
}