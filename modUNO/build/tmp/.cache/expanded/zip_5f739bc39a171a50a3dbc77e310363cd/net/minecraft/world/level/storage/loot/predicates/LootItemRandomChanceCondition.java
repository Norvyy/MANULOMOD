package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.world.level.storage.loot.LootContext;

public record LootItemRandomChanceCondition(float probability) implements LootItemCondition {
    public static final MapCodec<LootItemRandomChanceCondition> CODEC = RecordCodecBuilder.mapCodec(
        p_297204_ -> p_297204_.group(Codec.FLOAT.fieldOf("chance").forGetter(LootItemRandomChanceCondition::probability))
                .apply(p_297204_, LootItemRandomChanceCondition::new)
    );

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.RANDOM_CHANCE;
    }

    public boolean test(LootContext pContext) {
        return pContext.getRandom().nextFloat() < this.probability;
    }

    public static LootItemCondition.Builder randomChance(float pProbability) {
        return () -> new LootItemRandomChanceCondition(pProbability);
    }
}