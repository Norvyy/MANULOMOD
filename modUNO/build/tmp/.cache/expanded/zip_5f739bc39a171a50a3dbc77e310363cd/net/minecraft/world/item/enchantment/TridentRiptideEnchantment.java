package net.minecraft.world.item.enchantment;

public class TridentRiptideEnchantment extends Enchantment {
    public TridentRiptideEnchantment(Enchantment.EnchantmentDefinition pDefinition) {
        super(pDefinition);
    }

    @Override
    public boolean checkCompatibility(Enchantment pEnch) {
        return super.checkCompatibility(pEnch) && pEnch != Enchantments.LOYALTY && pEnch != Enchantments.CHANNELING;
    }
}