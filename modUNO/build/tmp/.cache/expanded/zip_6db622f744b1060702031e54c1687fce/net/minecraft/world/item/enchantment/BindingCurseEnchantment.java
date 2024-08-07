package net.minecraft.world.item.enchantment;

public class BindingCurseEnchantment extends Enchantment {
    public BindingCurseEnchantment(Enchantment.EnchantmentDefinition p_331686_) {
        super(p_331686_);
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isCurse() {
        return true;
    }
}