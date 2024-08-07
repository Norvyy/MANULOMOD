package net.minecraft.world.entity.projectile.windcharge;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public abstract class AbstractWindCharge extends AbstractHurtingProjectile implements ItemSupplier {
    public static final AbstractWindCharge.WindChargeDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new AbstractWindCharge.WindChargeDamageCalculator();

    public AbstractWindCharge(EntityType<? extends AbstractWindCharge> p_328415_, Level p_334141_) {
        super(p_328415_, p_334141_);
    }

    public AbstractWindCharge(
        EntityType<? extends AbstractWindCharge> p_328691_, Level p_334245_, Entity p_335850_, double p_331511_, double p_328582_, double p_333875_
    ) {
        super(p_328691_, p_331511_, p_328582_, p_333875_, p_334245_);
        this.setOwner(p_335850_);
    }

    AbstractWindCharge(
        EntityType<? extends AbstractWindCharge> p_334984_,
        double p_330891_,
        double p_328515_,
        double p_329380_,
        double p_330076_,
        double p_333356_,
        double p_330731_,
        Level p_329917_
    ) {
        super(p_334984_, p_330891_, p_328515_, p_329380_, p_330076_, p_333356_, p_330731_, p_329917_);
    }

    @Override
    protected AABB makeBoundingBox() {
        float f = this.getType().getDimensions().width() / 2.0F;
        float f1 = this.getType().getDimensions().height();
        float f2 = 0.15F;
        return new AABB(
            this.position().x - (double)f,
            this.position().y - 0.15F,
            this.position().z - (double)f,
            this.position().x + (double)f,
            this.position().y - 0.15F + (double)f1,
            this.position().z + (double)f
        );
    }

    @Override
    public boolean canCollideWith(Entity p_328571_) {
        return p_328571_ instanceof AbstractWindCharge ? false : super.canCollideWith(p_328571_);
    }

    @Override
    protected boolean canHitEntity(Entity p_333197_) {
        if (p_333197_ instanceof AbstractWindCharge) {
            return false;
        } else {
            return p_333197_.getType() == EntityType.END_CRYSTAL ? false : super.canHitEntity(p_333197_);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult p_328561_) {
        super.onHitEntity(p_328561_);
        if (!this.level().isClientSide) {
            LivingEntity livingentity = this.getOwner() instanceof LivingEntity livingentity1 ? livingentity1 : null;
            Entity entity = p_328561_.getEntity().getPassengerClosestTo(p_328561_.getLocation()).orElse(p_328561_.getEntity());
            if (livingentity != null) {
                livingentity.setLastHurtMob(entity);
            }

            entity.hurt(this.damageSources().windCharge(this, livingentity), 1.0F);
            this.explode();
        }
    }

    @Override
    public void push(double p_328125_, double p_336037_, double p_328448_) {
    }

    protected abstract void explode();

    @Override
    protected void onHitBlock(BlockHitResult p_330277_) {
        super.onHitBlock(p_330277_);
        if (!this.level().isClientSide) {
            this.explode();
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult p_328815_) {
        super.onHit(p_328815_);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public ItemStack getItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected float getInertia() {
        return 1.0F;
    }

    @Override
    protected float getLiquidInertia() {
        return this.getInertia();
    }

    @Nullable
    @Override
    protected ParticleOptions getTrailParticle() {
        return null;
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide && this.getBlockY() > this.level().getMaxBuildHeight() + 30) {
            this.explode();
            this.discard();
        } else {
            super.tick();
        }
    }

    public static class WindChargeDamageCalculator extends ExplosionDamageCalculator {
        @Override
        public boolean shouldDamageEntity(Explosion p_329716_, Entity p_327996_) {
            return false;
        }

        @Override
        public Optional<Float> getBlockExplosionResistance(Explosion p_335115_, BlockGetter p_329011_, BlockPos p_330601_, BlockState p_331417_, FluidState p_330040_) {
            return p_331417_.is(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS) ? Optional.of(3600000.0F) : Optional.empty();
        }
    }
}