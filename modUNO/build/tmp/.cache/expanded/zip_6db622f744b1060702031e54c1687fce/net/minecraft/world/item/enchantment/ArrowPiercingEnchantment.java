package net.minecraft.world.item.enchantment;

public class ArrowPiercingEnchantment extends Enchantment {
    public ArrowPiercingEnchantment(Enchantment.EnchantmentDefinition p_332974_) {
        super(p_332974_);
    }

    @Override
    public boolean checkCompatibility(Enchantment p_44608_) {
        return super.checkCompatibility(p_44608_) && p_44608_ != Enchantments.MULTISHOT;
    }
}