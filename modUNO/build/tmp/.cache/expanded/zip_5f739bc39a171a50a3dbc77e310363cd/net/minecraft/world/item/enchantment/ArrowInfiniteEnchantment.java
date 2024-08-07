package net.minecraft.world.item.enchantment;

public class ArrowInfiniteEnchantment extends Enchantment {
    public ArrowInfiniteEnchantment(Enchantment.EnchantmentDefinition pDefinition) {
        super(pDefinition);
    }

    @Override
    public boolean checkCompatibility(Enchantment pEnch) {
        return pEnch instanceof MendingEnchantment ? false : super.checkCompatibility(pEnch);
    }
}