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

    public WindCharge(EntityType<? extends AbstractWindCharge> p_330526_, Level p_330063_) {
        super(p_330526_, p_330063_);
    }

    public WindCharge(Player p_336321_, Level p_330515_, double p_330095_, double p_333760_, double p_334828_) {
        super(EntityType.WIND_CHARGE, p_330515_, p_336321_, p_330095_, p_333760_, p_334828_);
    }

    public WindCharge(Level p_333074_, double p_329691_, double p_335041_, double p_329004_, double p_328320_, double p_335487_, double p_331190_) {
        super(EntityType.WIND_CHARGE, p_329691_, p_335041_, p_329004_, p_328320_, p_335487_, p_331190_, p_333074_);
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