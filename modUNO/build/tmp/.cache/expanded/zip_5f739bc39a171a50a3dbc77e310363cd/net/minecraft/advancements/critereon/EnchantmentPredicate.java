package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public record EnchantmentPredicate(Optional<Holder<Enchantment>> enchantment, MinMaxBounds.Ints level) {
    public static final Codec<EnchantmentPredicate> CODEC = RecordCodecBuilder.create(
        p_325205_ -> p_325205_.group(
                    BuiltInRegistries.ENCHANTMENT.holderByNameCodec().optionalFieldOf("enchantment").forGetter(EnchantmentPredicate::enchantment),
                    MinMaxBounds.Ints.CODEC.optionalFieldOf("levels", MinMaxBounds.Ints.ANY).forGetter(EnchantmentPredicate::level)
                )
                .apply(p_325205_, EnchantmentPredicate::new)
    );

    public EnchantmentPredicate(Enchantment pEnchantment, MinMaxBounds.Ints pLevel) {
        this(Optional.of(pEnchantment.builtInRegistryHolder()), pLevel);
    }

    public boolean containedIn(ItemEnchantments pEnchantments) {
        if (this.enchantment.isPresent()) {
            Enchantment enchantment = this.enchantment.get().value();
            int i = pEnchantments.getLevel(enchantment);
            if (i == 0) {
                return false;
            }

            if (this.level != MinMaxBounds.Ints.ANY && !this.level.matches(i)) {
                return false;
            }
        } else if (this.level != MinMaxBounds.Ints.ANY) {
            for (Entry<Holder<Enchantment>> entry : pEnchantments.entrySet()) {
                if (this.level.matches(entry.getIntValue())) {
                    return true;
                }
            }

            return false;
        }

        return true;
    }
}