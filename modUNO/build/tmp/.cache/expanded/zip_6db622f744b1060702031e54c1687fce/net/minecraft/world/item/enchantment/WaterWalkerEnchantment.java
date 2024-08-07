package net.minecraft.world.item.enchantment;

public class WaterWalkerEnchantment extends Enchantment {
    public WaterWalkerEnchantment(Enchantment.EnchantmentDefinition p_333414_) {
        super(p_333414_);
    }

    @Override
    public boolean checkCompatibility(Enchantment p_45286_) {
        return super.checkCompatibility(p_45286_) && p_45286_ != Enchantments.FROST_WALKER;
    }
}