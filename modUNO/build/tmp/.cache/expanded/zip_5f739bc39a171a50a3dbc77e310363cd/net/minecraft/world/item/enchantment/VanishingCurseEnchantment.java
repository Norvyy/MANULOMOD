package net.minecraft.world.item.enchantment;

public class VanishingCurseEnchantment extends Enchantment {
    public VanishingCurseEnchantment(Enchantment.EnchantmentDefinition p_333561_) {
        super(p_333561_);
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