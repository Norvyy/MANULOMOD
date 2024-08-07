package net.minecraft.world.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public abstract class ProjectileWeaponItem extends Item {
    public static final Predicate<ItemStack> ARROW_ONLY = p_43017_ -> p_43017_.is(ItemTags.ARROWS);
    public static final Predicate<ItemStack> ARROW_OR_FIREWORK = ARROW_ONLY.or(p_43015_ -> p_43015_.is(Items.FIREWORK_ROCKET));

    public ProjectileWeaponItem(Item.Properties pProperties) {
        super(pProperties);
    }

    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return this.getAllSupportedProjectiles();
    }

    public abstract Predicate<ItemStack> getAllSupportedProjectiles();

    public static ItemStack getHeldProjectile(LivingEntity pShooter, Predicate<ItemStack> pIsAmmo) {
        if (pIsAmmo.test(pShooter.getItemInHand(InteractionHand.OFF_HAND))) {
            return pShooter.getItemInHand(InteractionHand.OFF_HAND);
        } else {
            return pIsAmmo.test(pShooter.getItemInHand(InteractionHand.MAIN_HAND)) ? pShooter.getItemInHand(InteractionHand.MAIN_HAND) : ItemStack.EMPTY;
        }
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    public abstract int getDefaultProjectileRange();

    protected void shoot(
        Level pLevel,
        LivingEntity pShooter,
        InteractionHand pHand,
        ItemStack pWeapon,
        List<ItemStack> pProjectileItems,
        float pVelocity,
        float pInaccuracy,
        boolean pIsCrit,
        @Nullable LivingEntity pTarget
    ) {
        float f = 10.0F;
        float f1 = pProjectileItems.size() == 1 ? 0.0F : 20.0F / (float)(pProjectileItems.size() - 1);
        float f2 = (float)((pProjectileItems.size() - 1) % 2) * f1 / 2.0F;
        float f3 = 1.0F;

        for (int i = 0; i < pProjectileItems.size(); i++) {
            ItemStack itemstack = pProjectileItems.get(i);
            if (!itemstack.isEmpty()) {
                float f4 = f2 + f3 * (float)((i + 1) / 2) * f1;
                f3 = -f3;
                pWeapon.hurtAndBreak(this.getDurabilityUse(itemstack), pShooter, LivingEntity.getSlotForHand(pHand));
                Projectile projectile = this.createProjectile(pLevel, pShooter, pWeapon, itemstack, pIsCrit);
                this.shootProjectile(pShooter, projectile, i, pVelocity, pInaccuracy, f4, pTarget);
                pLevel.addFreshEntity(projectile);
            }
        }
    }

    protected int getDurabilityUse(ItemStack pStack) {
        return 1;
    }

    protected abstract void shootProjectile(
        LivingEntity pShooter, Projectile pProjectile, int pIndex, float pVelocity, float pInaccuracy, float pAngle, @Nullable LivingEntity pTarget
    );

    protected Projectile createProjectile(Level pLevel, LivingEntity pShooter, ItemStack pWeapon, ItemStack pAmmo, boolean pIsCrit) {
        ArrowItem arrowitem = pAmmo.getItem() instanceof ArrowItem arrowitem1 ? arrowitem1 : (ArrowItem)Items.ARROW;
        AbstractArrow abstractarrow = arrowitem.createArrow(pLevel, pAmmo, pShooter);
        abstractarrow = customArrow(abstractarrow);
        if (pIsCrit) {
            abstractarrow.setCritArrow(true);
        }

        int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER, pWeapon);
        if (k > 0) {
            abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + (double)k * 0.5 + 0.5);
        }

        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH, pWeapon);
        if (i > 0) {
            abstractarrow.setKnockback(i);
        }

        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAME, pWeapon) > 0) {
            abstractarrow.igniteForSeconds(100);
        }

        int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, pWeapon);
        if (j > 0) {
            abstractarrow.setPierceLevel((byte)j);
        }

        return abstractarrow;
    }

    protected static boolean hasInfiniteArrows(ItemStack pWeapon, ItemStack pAmmo, boolean pHasInfiniteMaterials) {
        return pHasInfiniteMaterials || pAmmo.is(Items.ARROW) && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY, pWeapon) > 0;
    }

    protected static List<ItemStack> draw(ItemStack pWeapon, ItemStack pAmmo, LivingEntity pShooter) {
        if (pAmmo.isEmpty()) {
            return List.of();
        } else {
            int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, pWeapon);
            int j = i == 0 ? 1 : 3;
            List<ItemStack> list = new ArrayList<>(j);
            ItemStack itemstack = pAmmo.copy();
            boolean infinite = pAmmo.getItem() instanceof ArrowItem arrow && arrow.isInfinite(pAmmo, pWeapon, pShooter);

            for (int k = 0; k < j; k++) {
                list.add(useAmmo(pWeapon, k == 0 ? pAmmo : itemstack, pShooter, k > 0 || infinite));
            }

            return list;
        }
    }

    protected static ItemStack useAmmo(ItemStack pWeapon, ItemStack pAmmo, LivingEntity pShooter, boolean pIntangable) {
        boolean flag = !pIntangable && !hasInfiniteArrows(pWeapon, pAmmo, pShooter.hasInfiniteMaterials());
        if (!flag) {
            ItemStack itemstack1 = pAmmo.copyWithCount(1);
            itemstack1.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
            return itemstack1;
        } else {
            ItemStack itemstack = pAmmo.split(1);
            if (pAmmo.isEmpty() && pShooter instanceof Player player) {
                player.getInventory().removeItem(pAmmo);
            }

            return itemstack;
        }
    }

    public AbstractArrow customArrow(AbstractArrow arrow) {
        return arrow;
    }
}
