package net.minecraft.world.item.enchantment;

public class SwiftSneakEnchantment extends Enchantment {
    public SwiftSneakEnchantment(Enchantment.EnchantmentDefinition p_329493_) {
        super(p_329493_);
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }
}