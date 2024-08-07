package net.minecraft.world.entity.projectile.windcharge;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class WindCharge extends AbstractWindCharge {
    private static final WindCharge.WindChargePlayerDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new WindCharge.WindChargePlayerDamageCalculator();
    private static final float RADIUS = 1.2F;

    public WindCharge(EntityType<? extends AbstractWindCharge> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public WindCharge(Player pPlayer, Level pLevel, double pX, double pY, double pZ) {
        super(EntityType.WIND_CHARGE, pLevel, pPlayer, pX, pY, pZ);
    }

    public WindCharge(Level pLevel, double pX, double pY, double pZ, double pOffsetX, double pOffsetY, double pOffsetZ) {
        super(EntityType.WIND_CHARGE, pX, pY, pZ, pOffsetX, pOffsetY, pOffsetZ, pLevel);
    }

    @Override
    protected void explode() {
        this.level()
            .explode(
                this,
                null,
                EXPLOSION_DAMAGE_CALCULATOR,
                this.getX(),
                this.getY(),
                this.getZ(),
                1.2F,
                false,
                Level.ExplosionInteraction.BLOW,
                ParticleTypes.GUST_EMITTER_SMALL,
                ParticleTypes.GUST_EMITTER_LARGE,
                SoundEvents.WIND_CHARGE_BURST
            );
    }

    public static final class WindChargePlayerDamageCalculator extends AbstractWindCharge.WindChargeDamageCalculator {
        @Override
        public float getKnockbackMultiplier(Entity p_327945_) {
            return 1.1F;
        }
    }
}