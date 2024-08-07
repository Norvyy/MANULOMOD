package net.minecraft.world.item.enchantment;

import net.minecraft.util.random.WeightedEntry;

/**
 * Defines an immutable instance of an enchantment and its level.
 */
public class EnchantmentInstance extends WeightedEntry.IntrusiveBase {
    public final Enchantment enchantment;
    public final int level;

    public EnchantmentInstance(Enchantment pEnchantment, int pLevel) {
        super(pEnchantment.getWeight());
        this.enchantment = pEnchantment;
        this.level = pLevel;
    }
}