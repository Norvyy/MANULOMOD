package net.minecraft.world.item.enchantment;

public class WaterWalkerEnchantment extends Enchantment {
    public WaterWalkerEnchantment(Enchantment.EnchantmentDefinition pDefinition) {
        super(pDefinition);
    }

    @Override
    public boolean checkCompatibility(Enchantment pEnch) {
        return super.checkCompatibility(pEnch) && pEnch != Enchantments.FROST_WALKER;
    }
}