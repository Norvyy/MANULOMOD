package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Set;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

/**
 * Applies a random enchantment to the stack.
 * 
 * @see EnchantmentHelper#enchantItem
 */
public class EnchantWithLevelsFunction extends LootItemConditionalFunction {
    public static final MapCodec<EnchantWithLevelsFunction> CODEC = RecordCodecBuilder.mapCodec(
        p_298285_ -> commonFields(p_298285_)
                .and(
                    p_298285_.group(
                        NumberProviders.CODEC.fieldOf("levels").forGetter(p_298991_ -> p_298991_.levels),
                        Codec.BOOL.fieldOf("treasure").orElse(false).forGetter(p_298792_ -> p_298792_.treasure)
                    )
                )
                .apply(p_298285_, EnchantWithLevelsFunction::new)
    );
    private final NumberProvider levels;
    private final boolean treasure;

    EnchantWithLevelsFunction(List<LootItemCondition> p_300816_, NumberProvider p_165194_, boolean p_165195_) {
        super(p_300816_);
        this.levels = p_165194_;
        this.treasure = p_165195_;
    }

    @Override
    public LootItemFunctionType<EnchantWithLevelsFunction> getType() {
        return LootItemFunctions.ENCHANT_WITH_LEVELS;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.levels.getReferencedContextParams();
    }

    @Override
    public ItemStack run(ItemStack pStack, LootContext pContext) {
        RandomSource randomsource = pContext.getRandom();
        return EnchantmentHelper.enchantItem(pContext.getLevel().enabledFeatures(), randomsource, pStack, this.levels.getInt(pContext), this.treasure);
    }

    public static EnchantWithLevelsFunction.Builder enchantWithLevels(NumberProvider pLevels) {
        return new EnchantWithLevelsFunction.Builder(pLevels);
    }

    public static class Builder extends LootItemConditionalFunction.Builder<EnchantWithLevelsFunction.Builder> {
        private final NumberProvider levels;
        private boolean treasure;

        public Builder(NumberProvider pLevels) {
            this.levels = pLevels;
        }

        protected EnchantWithLevelsFunction.Builder getThis() {
            return this;
        }

        public EnchantWithLevelsFunction.Builder allowTreasure() {
            this.treasure = true;
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new EnchantWithLevelsFunction(this.getConditions(), this.levels, this.treasure);
        }
    }
}