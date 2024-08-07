package net.minecraft.world.item.enchantment;

public class MultiShotEnchantment extends Enchantment {
    public MultiShotEnchantment(Enchantment.EnchantmentDefinition pDefinition) {
        super(pDefinition);
    }

    @Override
    public boolean checkCompatibility(Enchantment pEnch) {
        return super.checkCompatibility(pEnch) && pEnch != Enchantments.PIERCING;
    }
}