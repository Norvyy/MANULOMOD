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

    public AbstractWindCharge(EntityType<? extends AbstractWindCharge> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public AbstractWindCharge(
        EntityType<? extends AbstractWindCharge> pEntityType, Level pLevel, Entity pOwner, double pX, double pY, double pZ
    ) {
        super(pEntityType, pX, pY, pZ, pLevel);
        this.setOwner(pOwner);
    }

    AbstractWindCharge(
        EntityType<? extends AbstractWindCharge> pEntityType,
        double pX,
        double pY,
        double pZ,
        double pOffsetX,
        double pOffsetY,
        double pOffsetZ,
        Level pLevel
    ) {
        super(pEntityType, pX, pY, pZ, pOffsetX, pOffsetY, pOffsetZ, pLevel);
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
    public boolean canCollideWith(Entity pEntity) {
        return pEntity instanceof AbstractWindCharge ? false : super.canCollideWith(pEntity);
    }

    @Override
    protected boolean canHitEntity(Entity pTarget) {
        if (pTarget instanceof AbstractWindCharge) {
            return false;
        } else {
            return pTarget.getType() == EntityType.END_CRYSTAL ? false : super.canHitEntity(pTarget);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level().isClientSide) {
            LivingEntity livingentity = this.getOwner() instanceof LivingEntity livingentity1 ? livingentity1 : null;
            Entity entity = pResult.getEntity().getPassengerClosestTo(pResult.getLocation()).orElse(pResult.getEntity());
            if (livingentity != null) {
                livingentity.setLastHurtMob(entity);
            }

            entity.hurt(this.damageSources().windCharge(this, livingentity), 1.0F);
            this.explode();
        }
    }

    @Override
    public void push(double pX, double pY, double pZ) {
    }

    protected abstract void explode();

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if (!this.level().isClientSide) {
            this.explode();
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
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