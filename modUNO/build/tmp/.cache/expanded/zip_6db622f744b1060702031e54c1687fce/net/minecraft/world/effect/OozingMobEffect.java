package net.minecraft.world.effect;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

class OozingMobEffect extends MobEffect {
    private static final int RADIUS_TO_CHECK_SLIMES = 2;
    public static final int SLIME_SIZE = 2;
    private final ToIntFunction<RandomSource> spawnedCount;

    protected OozingMobEffect(MobEffectCategory p_333140_, int p_332642_, ToIntFunction<RandomSource> p_334869_) {
        super(p_333140_, p_332642_, ParticleTypes.ITEM_SLIME);
        this.spawnedCount = p_334869_;
    }

    @VisibleForTesting
    protected static int numberOfSlimesToSpawn(int p_329727_, int p_333663_, int p_333087_) {
        return Mth.clamp(0, p_329727_ - p_333663_, p_333087_);
    }

    @Override
    public void onMobRemoved(LivingEntity p_329549_, int p_329953_, Entity.RemovalReason p_332875_) {
        if (p_332875_ == Entity.RemovalReason.KILLED) {
            int i = this.spawnedCount.applyAsInt(p_329549_.getRandom());
            Level level = p_329549_.level();
            int j = level.getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
            List<Slime> list = new ArrayList<>();
            level.getEntities(EntityType.SLIME, p_329549_.getBoundingBox().inflate(2.0), p_335983_ -> p_335983_ != p_329549_, list, j);
            int k = numberOfSlimesToSpawn(j, list.size(), i);

            for (int l = 0; l < k; l++) {
                this.spawnSlimeOffspring(p_329549_.level(), p_329549_.getX(), p_329549_.getY() + 0.5, p_329549_.getZ());
            }
        }
    }

    private void spawnSlimeOffspring(Level p_335546_, double p_331630_, double p_328143_, double p_332724_) {
        Slime slime = EntityType.SLIME.create(p_335546_);
        if (slime != null) {
            slime.setSize(2, true);
            slime.moveTo(p_331630_, p_328143_, p_332724_, p_335546_.getRandom().nextFloat() * 360.0F, 0.0F);
            p_335546_.addFreshEntity(slime);
        }
    }
}