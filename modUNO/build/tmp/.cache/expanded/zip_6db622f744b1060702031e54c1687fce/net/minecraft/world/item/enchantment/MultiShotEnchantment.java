package net.minecraft.world.item.enchantment;

public class MultiShotEnchantment extends Enchantment {
    public MultiShotEnchantment(Enchantment.EnchantmentDefinition p_334415_) {
        super(p_334415_);
    }

    @Override
    public boolean checkCompatibility(Enchantment p_45113_) {
        return super.checkCompatibility(p_45113_) && p_45113_ != Enchantments.PIERCING;
    }
}