package net.minecraft.world.level.storage.loot.functions;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class EnchantRandomlyFunction extends LootItemConditionalFunction {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Codec<HolderSet<Enchantment>> ENCHANTMENT_SET_CODEC = BuiltInRegistries.ENCHANTMENT
        .holderByNameCodec()
        .listOf()
        .xmap(HolderSet::direct, p_297089_ -> p_297089_.stream().toList());
    public static final MapCodec<EnchantRandomlyFunction> CODEC = RecordCodecBuilder.mapCodec(
        p_327568_ -> commonFields(p_327568_)
                .and(ENCHANTMENT_SET_CODEC.optionalFieldOf("enchantments").forGetter(p_297084_ -> p_297084_.enchantments))
                .apply(p_327568_, EnchantRandomlyFunction::new)
    );
    private final Optional<HolderSet<Enchantment>> enchantments;

    EnchantRandomlyFunction(List<LootItemCondition> p_298352_, Optional<HolderSet<Enchantment>> p_297532_) {
        super(p_298352_);
        this.enchantments = p_297532_;
    }

    @Override
    public LootItemFunctionType<EnchantRandomlyFunction> getType() {
        return LootItemFunctions.ENCHANT_RANDOMLY;
    }

    @Override
    public ItemStack run(ItemStack p_80429_, LootContext p_80430_) {
        RandomSource randomsource = p_80430_.getRandom();
        Optional<Holder<Enchantment>> optional = this.enchantments
            .<Holder<Enchantment>>flatMap(p_297095_ -> p_297095_.getRandomElement(randomsource))
            .or(
                () -> {
                    boolean flag = p_80429_.is(Items.BOOK);
                    List<Holder.Reference<Enchantment>> list = BuiltInRegistries.ENCHANTMENT
                        .holders()
                        .filter(p_327564_ -> p_327564_.value().isEnabled(p_80430_.getLevel().enabledFeatures()))
                        .filter(p_297090_ -> p_297090_.value().isDiscoverable())
                        .filter(p_297093_ -> flag || p_297093_.value().canEnchant(p_80429_))
                        .toList();
                    return Util.getRandomSafe(list, randomsource);
                }
            );
        if (optional.isEmpty()) {
            LOGGER.warn("Couldn't find a compatible enchantment for {}", p_80429_);
            return p_80429_;
        } else {
            return enchantItem(p_80429_, optional.get().value(), randomsource);
        }
    }

    private static ItemStack enchantItem(ItemStack p_230980_, Enchantment p_230981_, RandomSource p_230982_) {
        int i = Mth.nextInt(p_230982_, p_230981_.getMinLevel(), p_230981_.getMaxLevel());
        if (p_230980_.is(Items.BOOK)) {
            p_230980_ = new ItemStack(Items.ENCHANTED_BOOK);
        }

        p_230980_.enchant(p_230981_, i);
        return p_230980_;
    }

    public static EnchantRandomlyFunction.Builder randomEnchantment() {
        return new EnchantRandomlyFunction.Builder();
    }

    public static LootItemConditionalFunction.Builder<?> randomApplicableEnchantment() {
        return simpleBuilder(p_297086_ -> new EnchantRandomlyFunction(p_297086_, Optional.empty()));
    }

    public static class Builder extends LootItemConditionalFunction.Builder<EnchantRandomlyFunction.Builder> {
        private final List<Holder<Enchantment>> enchantments = new ArrayList<>();

        protected EnchantRandomlyFunction.Builder getThis() {
            return this;
        }

        public EnchantRandomlyFunction.Builder withEnchantment(Enchantment p_80445_) {
            this.enchantments.add(p_80445_.builtInRegistryHolder());
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new EnchantRandomlyFunction(this.getConditions(), this.enchantments.isEmpty() ? Optional.empty() : Optional.of(HolderSet.direct(this.enchantments)));
        }
    }
}