package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public interface EnchantmentTags {
    TagKey<Enchantment> TOOLTIP_ORDER = create("tooltip_order");

    private static TagKey<Enchantment> create(String p_334094_) {
        return TagKey.create(Registries.ENCHANTMENT, new ResourceLocation("minecraft", p_334094_));
    }
}