package net.minecraft.world.item;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.DensityEnchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MaceItem extends Item {
    private static final int DEFAULT_ATTACK_DAMAGE = 6;
    private static final float DEFAULT_ATTACK_SPEED = -2.4F;
    private static final float SMASH_ATTACK_FALL_THRESHOLD = 1.5F;
    private static final float SMASH_ATTACK_HEAVY_THRESHOLD = 5.0F;
    public static final float SMASH_ATTACK_KNOCKBACK_RADIUS = 3.5F;
    private static final float SMASH_ATTACK_KNOCKBACK_POWER = 0.7F;
    private static final float SMASH_ATTACK_FALL_DISTANCE_MULTIPLIER = 3.0F;

    public MaceItem(Item.Properties p_329217_) {
        super(p_329217_);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
            .add(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 6.0, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
            )
            .add(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.4F, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
            )
            .build();
    }

    public static Tool createToolProperties() {
        return new Tool(List.of(), 1.0F, 2);
    }

    @Override
    public boolean canAttackBlock(BlockState p_330271_, Level p_332833_, BlockPos p_334020_, Player p_336375_) {
        return !p_336375_.isCreative();
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public boolean hurtEnemy(ItemStack p_329476_, LivingEntity p_332492_, LivingEntity p_333391_) {
        p_329476_.hurtAndBreak(1, p_333391_, EquipmentSlot.MAINHAND);
        if (p_333391_ instanceof ServerPlayer serverplayer && canSmashAttack(serverplayer)) {
            ServerLevel serverlevel = (ServerLevel)p_333391_.level();
            serverplayer.currentImpulseImpactPos = serverplayer.position();
            serverplayer.ignoreFallDamageFromCurrentImpulse = true;
            serverplayer.setDeltaMovement(serverplayer.getDeltaMovement().with(Direction.Axis.Y, 0.01F));
            serverplayer.connection.send(new ClientboundSetEntityMotionPacket(serverplayer));
            if (p_332492_.onGround()) {
                serverplayer.setSpawnExtraParticlesOnFall(true);
                SoundEvent soundevent = serverplayer.fallDistance > 5.0F ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
                serverlevel.playSound(
                    null, serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), soundevent, serverplayer.getSoundSource(), 1.0F, 1.0F
                );
            } else {
                serverlevel.playSound(
                    null, serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), SoundEvents.MACE_SMASH_AIR, serverplayer.getSoundSource(), 1.0F, 1.0F
                );
            }

            knockback(serverlevel, serverplayer, p_332492_);
            return true;
        }

        return false;
    }

    @Override
    public boolean isValidRepairItem(ItemStack p_335618_, ItemStack p_332323_) {
        return p_332323_.is(Items.BREEZE_ROD);
    }

    @Override
    public float getAttackDamageBonus(Player p_336257_, float p_333106_) {
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.DENSITY, p_336257_);
        float f = DensityEnchantment.calculateDamageAddition(i, p_336257_.fallDistance);
        return canSmashAttack(p_336257_) ? 3.0F * p_336257_.fallDistance + f : 0.0F;
    }

    private static void knockback(Level p_332228_, Player p_335060_, Entity p_335011_) {
        p_332228_.levelEvent(2013, p_335011_.getOnPos(), 750);
        p_332228_.getEntitiesOfClass(LivingEntity.class, p_335011_.getBoundingBox().inflate(3.5), knockbackPredicate(p_335060_, p_335011_)).forEach(p_328659_ -> {
            Vec3 vec3 = p_328659_.position().subtract(p_335011_.position());
            double d0 = getKnockbackPower(p_335060_, p_328659_, vec3);
            Vec3 vec31 = vec3.normalize().scale(d0);
            if (d0 > 0.0) {
                p_328659_.push(vec31.x, 0.7F, vec31.z);
            }
        });
    }

    private static Predicate<LivingEntity> knockbackPredicate(Player p_334836_, Entity p_334480_) {
        return p_328244_ -> {
            boolean flag;
            boolean flag1;
            boolean flag2;
            boolean flag5;
            label44: {
                flag = !p_328244_.isSpectator();
                flag1 = p_328244_ != p_334836_ && p_328244_ != p_334480_;
                flag2 = !p_334836_.isAlliedTo(p_328244_);
                if (p_328244_ instanceof ArmorStand armorstand && armorstand.isMarker()) {
                    flag5 = false;
                    break label44;
                }

                flag5 = true;
            }

            boolean flag3 = flag5;
            boolean flag4 = p_334480_.distanceToSqr(p_328244_) <= Math.pow(3.5, 2.0);
            return flag && flag1 && flag2 && flag3 && flag4;
        };
    }

    private static double getKnockbackPower(Player p_328672_, LivingEntity p_334129_, Vec3 p_335583_) {
        return (3.5 - p_335583_.length()) * 0.7F * (double)(p_328672_.fallDistance > 5.0F ? 2 : 1) * (1.0 - p_334129_.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
    }

    public static boolean canSmashAttack(Player p_328263_) {
        return p_328263_.fallDistance > 1.5F && !p_328263_.isFallFlying();
    }
}