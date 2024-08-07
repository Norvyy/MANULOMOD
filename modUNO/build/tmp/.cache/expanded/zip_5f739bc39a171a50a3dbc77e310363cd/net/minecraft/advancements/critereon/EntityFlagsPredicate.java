package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public record EntityFlagsPredicate(
    Optional<Boolean> isOnFire, Optional<Boolean> isCrouching, Optional<Boolean> isSprinting, Optional<Boolean> isSwimming, Optional<Boolean> isBaby
) {
    public static final Codec<EntityFlagsPredicate> CODEC = RecordCodecBuilder.create(
        p_325210_ -> p_325210_.group(
                    Codec.BOOL.optionalFieldOf("is_on_fire").forGetter(EntityFlagsPredicate::isOnFire),
                    Codec.BOOL.optionalFieldOf("is_sneaking").forGetter(EntityFlagsPredicate::isCrouching),
                    Codec.BOOL.optionalFieldOf("is_sprinting").forGetter(EntityFlagsPredicate::isSprinting),
                    Codec.BOOL.optionalFieldOf("is_swimming").forGetter(EntityFlagsPredicate::isSwimming),
                    Codec.BOOL.optionalFieldOf("is_baby").forGetter(EntityFlagsPredicate::isBaby)
                )
                .apply(p_325210_, EntityFlagsPredicate::new)
    );

    public boolean matches(Entity pEntity) {
        if (this.isOnFire.isPresent() && pEntity.isOnFire() != this.isOnFire.get()) {
            return false;
        } else if (this.isCrouching.isPresent() && pEntity.isCrouching() != this.isCrouching.get()) {
            return false;
        } else if (this.isSprinting.isPresent() && pEntity.isSprinting() != this.isSprinting.get()) {
            return false;
        } else if (this.isSwimming.isPresent() && pEntity.isSwimming() != this.isSwimming.get()) {
            return false;
        } else {
            if (this.isBaby.isPresent() && pEntity instanceof LivingEntity livingentity && livingentity.isBaby() != this.isBaby.get()) {
                return false;
            }

            return true;
        }
    }

    public static class Builder {
        private Optional<Boolean> isOnFire = Optional.empty();
        private Optional<Boolean> isCrouching = Optional.empty();
        private Optional<Boolean> isSprinting = Optional.empty();
        private Optional<Boolean> isSwimming = Optional.empty();
        private Optional<Boolean> isBaby = Optional.empty();

        public static EntityFlagsPredicate.Builder flags() {
            return new EntityFlagsPredicate.Builder();
        }

        public EntityFlagsPredicate.Builder setOnFire(Boolean pOnFire) {
            this.isOnFire = Optional.of(pOnFire);
            return this;
        }

        public EntityFlagsPredicate.Builder setCrouching(Boolean pIsCrouching) {
            this.isCrouching = Optional.of(pIsCrouching);
            return this;
        }

        public EntityFlagsPredicate.Builder setSprinting(Boolean pIsSprinting) {
            this.isSprinting = Optional.of(pIsSprinting);
            return this;
        }

        public EntityFlagsPredicate.Builder setSwimming(Boolean pIsSwimming) {
            this.isSwimming = Optional.of(pIsSwimming);
            return this;
        }

        public EntityFlagsPredicate.Builder setIsBaby(Boolean pIsBaby) {
            this.isBaby = Optional.of(pIsBaby);
            return this;
        }

        public EntityFlagsPredicate build() {
            return new EntityFlagsPredicate(this.isOnFire, this.isCrouching, this.isSprinting, this.isSwimming, this.isBaby);
        }
    }
}