package net.minecraft.world.item.crafting;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public class RepairItemRecipe extends CustomRecipe {
    public RepairItemRecipe(CraftingBookCategory p_248679_) {
        super(p_248679_);
    }

    @Nullable
    private Pair<ItemStack, ItemStack> getItemsToCombine(CraftingContainer p_335773_) {
        ItemStack itemstack = null;
        ItemStack itemstack1 = null;

        for (int i = 0; i < p_335773_.getContainerSize(); i++) {
            ItemStack itemstack2 = p_335773_.getItem(i);
            if (!itemstack2.isEmpty()) {
                if (itemstack == null) {
                    itemstack = itemstack2;
                } else {
                    if (itemstack1 != null) {
                        return null;
                    }

                    itemstack1 = itemstack2;
                }
            }
        }

        return itemstack != null && itemstack1 != null && canCombine(itemstack, itemstack1) ? Pair.of(itemstack, itemstack1) : null;
    }

    private static boolean canCombine(ItemStack p_335534_, ItemStack p_329259_) {
        return p_329259_.is(p_335534_.getItem())
            && p_335534_.getCount() == 1
            && p_329259_.getCount() == 1
            && p_335534_.has(DataComponents.MAX_DAMAGE)
            && p_329259_.has(DataComponents.MAX_DAMAGE)
            && p_335534_.has(DataComponents.DAMAGE)
            && p_329259_.has(DataComponents.DAMAGE);
    }

    public boolean matches(CraftingContainer p_44138_, Level p_44139_) {
        return this.getItemsToCombine(p_44138_) != null;
    }

    public ItemStack assemble(CraftingContainer p_333623_, HolderLookup.Provider p_331714_) {
        Pair<ItemStack, ItemStack> pair = this.getItemsToCombine(p_333623_);
        if (pair == null) {
            return ItemStack.EMPTY;
        } else {
            ItemStack itemstack = pair.getFirst();
            ItemStack itemstack1 = pair.getSecond();
            int i = Math.max(itemstack.getMaxDamage(), itemstack1.getMaxDamage());
            int j = itemstack.getMaxDamage() - itemstack.getDamageValue();
            int k = itemstack1.getMaxDamage() - itemstack1.getDamageValue();
            int l = j + k + i * 5 / 100;
            ItemStack itemstack2 = new ItemStack(itemstack.getItem());
            itemstack2.set(DataComponents.MAX_DAMAGE, i);
            itemstack2.setDamageValue(Math.max(i - l, 0));
            ItemEnchantments itemenchantments = EnchantmentHelper.getEnchantmentsForCrafting(itemstack);
            ItemEnchantments itemenchantments1 = EnchantmentHelper.getEnchantmentsForCrafting(itemstack1);
            EnchantmentHelper.updateEnchantments(
                itemstack2,
                p_327207_ -> p_331714_.lookupOrThrow(Registries.ENCHANTMENT).listElements().map(Holder::value).filter(Enchantment::isCurse).forEach(p_327203_ -> {
                        int i1 = Math.max(itemenchantments.getLevel(p_327203_), itemenchantments1.getLevel(p_327203_));
                        if (i1 > 0) {
                            p_327207_.upgrade(p_327203_, i1);
                        }
                    })
            );
            return itemstack2;
        }
    }

    @Override
    public boolean canCraftInDimensions(int p_44128_, int p_44129_) {
        return p_44128_ * p_44129_ >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.REPAIR_ITEM;
    }
}