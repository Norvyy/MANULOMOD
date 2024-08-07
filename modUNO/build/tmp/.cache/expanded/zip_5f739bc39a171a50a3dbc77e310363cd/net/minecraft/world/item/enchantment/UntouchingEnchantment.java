package net.minecraft.world.item.enchantment;

public class UntouchingEnchantment extends Enchantment {
    protected UntouchingEnchantment(Enchantment.EnchantmentDefinition pDefinition) {
        super(pDefinition);
    }

    @Override
    public boolean checkCompatibility(Enchantment pEnch) {
        return super.checkCompatibility(pEnch) && pEnch != Enchantments.FORTUNE;
    }
}