package net.minecraft.world.level.storage.loot;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public interface ContainerComponentManipulator<T> {
    DataComponentType<T> type();

    T empty();

    T setContents(T pContents, Stream<ItemStack> pItems);

    Stream<ItemStack> getContents(T pContents);

    default void setContents(ItemStack pStack, T pContents, Stream<ItemStack> pItems) {
        T t = pStack.getOrDefault(this.type(), pContents);
        T t1 = this.setContents(t, pItems);
        pStack.set(this.type(), t1);
    }

    default void setContents(ItemStack pStack, Stream<ItemStack> pItems) {
        this.setContents(pStack, this.empty(), pItems);
    }

    default void modifyItems(ItemStack pStack, UnaryOperator<ItemStack> pModifier) {
        T t = pStack.get(this.type());
        if (t != null) {
            UnaryOperator<ItemStack> unaryoperator = p_327931_ -> p_327931_.isEmpty() ? p_327931_ : pModifier.apply(p_327931_);
            this.setContents(pStack, this.getContents(t).map(unaryoperator));
        }
    }
}