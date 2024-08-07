package net.minecraft.world.level.storage.loot;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public interface ContainerComponentManipulator<T> {
    DataComponentType<T> type();

    T empty();

    T setContents(T p_331842_, Stream<ItemStack> p_327717_);

    Stream<ItemStack> getContents(T p_336301_);

    default void setContents(ItemStack p_333844_, T p_334408_, Stream<ItemStack> p_331739_) {
        T t = p_333844_.getOrDefault(this.type(), p_334408_);
        T t1 = this.setContents(t, p_331739_);
        p_333844_.set(this.type(), t1);
    }

    default void setContents(ItemStack p_331343_, Stream<ItemStack> p_333653_) {
        this.setContents(p_331343_, this.empty(), p_333653_);
    }

    default void modifyItems(ItemStack p_335094_, UnaryOperator<ItemStack> p_330990_) {
        T t = p_335094_.get(this.type());
        if (t != null) {
            UnaryOperator<ItemStack> unaryoperator = p_327931_ -> p_327931_.isEmpty() ? p_327931_ : p_330990_.apply(p_327931_);
            this.setContents(p_335094_, this.getContents(t).map(unaryoperator));
        }
    }
}