package net.minecraft.world.item.enchantment;

public class MendingEnchantment extends Enchantment {
    public MendingEnchantment(Enchantment.EnchantmentDefinition p_334701_) {
        super(p_334701_);
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }
}