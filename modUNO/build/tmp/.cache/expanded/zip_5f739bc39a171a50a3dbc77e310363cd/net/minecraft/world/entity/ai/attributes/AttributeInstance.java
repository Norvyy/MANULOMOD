package net.minecraft.world.entity.ai.attributes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;

public class AttributeInstance {
    private final Holder<Attribute> attribute;
    private final Map<AttributeModifier.Operation, Map<UUID, AttributeModifier>> modifiersByOperation = Maps.newEnumMap(AttributeModifier.Operation.class);
    private final Map<UUID, AttributeModifier> modifierById = new Object2ObjectArrayMap<>();
    private final Map<UUID, AttributeModifier> permanentModifiers = new Object2ObjectArrayMap<>();
    private double baseValue;
    private boolean dirty = true;
    private double cachedValue;
    private final Consumer<AttributeInstance> onDirty;

    public AttributeInstance(Holder<Attribute> pAttribute, Consumer<AttributeInstance> pOnDirty) {
        this.attribute = pAttribute;
        this.onDirty = pOnDirty;
        this.baseValue = pAttribute.value().getDefaultValue();
    }

    public Holder<Attribute> getAttribute() {
        return this.attribute;
    }

    public double getBaseValue() {
        return this.baseValue;
    }

    public void setBaseValue(double pBaseValue) {
        if (pBaseValue != this.baseValue) {
            this.baseValue = pBaseValue;
            this.setDirty();
        }
    }

    @VisibleForTesting
    Map<UUID, AttributeModifier> getModifiers(AttributeModifier.Operation pOperation) {
        return this.modifiersByOperation.computeIfAbsent(pOperation, p_326790_ -> new Object2ObjectOpenHashMap<>());
    }

    public Set<AttributeModifier> getModifiers() {
        return ImmutableSet.copyOf(this.modifierById.values());
    }

    @Nullable
    public AttributeModifier getModifier(UUID pUuid) {
        return this.modifierById.get(pUuid);
    }

    public boolean hasModifier(AttributeModifier pModifier) {
        return this.modifierById.get(pModifier.id()) != null;
    }

    private void addModifier(AttributeModifier pModifier) {
        AttributeModifier attributemodifier = this.modifierById.putIfAbsent(pModifier.id(), pModifier);
        if (attributemodifier != null) {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        } else {
            this.getModifiers(pModifier.operation()).put(pModifier.id(), pModifier);
            this.setDirty();
        }
    }

    public void addOrUpdateTransientModifier(AttributeModifier pModifier) {
        AttributeModifier attributemodifier = this.modifierById.put(pModifier.id(), pModifier);
        if (pModifier != attributemodifier) {
            this.getModifiers(pModifier.operation()).put(pModifier.id(), pModifier);
            this.setDirty();
        }
    }

    public void addTransientModifier(AttributeModifier pModifier) {
        this.addModifier(pModifier);
    }

    public void addPermanentModifier(AttributeModifier pModifier) {
        this.addModifier(pModifier);
        this.permanentModifiers.put(pModifier.id(), pModifier);
    }

    protected void setDirty() {
        this.dirty = true;
        this.onDirty.accept(this);
    }

    public void removeModifier(AttributeModifier pModifier) {
        this.removeModifier(pModifier.id());
    }

    public void removeModifier(UUID pIdentifier) {
        AttributeModifier attributemodifier = this.modifierById.remove(pIdentifier);
        if (attributemodifier != null) {
            this.getModifiers(attributemodifier.operation()).remove(pIdentifier);
            this.permanentModifiers.remove(pIdentifier);
            this.setDirty();
        }
    }

    public boolean removePermanentModifier(UUID pIdentifier) {
        AttributeModifier attributemodifier = this.permanentModifiers.remove(pIdentifier);
        if (attributemodifier == null) {
            return false;
        } else {
            this.getModifiers(attributemodifier.operation()).remove(attributemodifier.id());
            this.modifierById.remove(pIdentifier);
            this.setDirty();
            return true;
        }
    }

    public void removeModifiers() {
        for (AttributeModifier attributemodifier : this.getModifiers()) {
            this.removeModifier(attributemodifier);
        }
    }

    public double getValue() {
        if (this.dirty) {
            this.cachedValue = this.calculateValue();
            this.dirty = false;
        }

        return this.cachedValue;
    }

    private double calculateValue() {
        double d0 = this.getBaseValue();

        for (AttributeModifier attributemodifier : this.getModifiersOrEmpty(AttributeModifier.Operation.ADD_VALUE)) {
            d0 += attributemodifier.amount();
        }

        double d1 = d0;

        for (AttributeModifier attributemodifier1 : this.getModifiersOrEmpty(AttributeModifier.Operation.ADD_MULTIPLIED_BASE)) {
            d1 += d0 * attributemodifier1.amount();
        }

        for (AttributeModifier attributemodifier2 : this.getModifiersOrEmpty(AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)) {
            d1 *= 1.0 + attributemodifier2.amount();
        }

        return this.attribute.value().sanitizeValue(d1);
    }

    private Collection<AttributeModifier> getModifiersOrEmpty(AttributeModifier.Operation pOperation) {
        return this.modifiersByOperation.getOrDefault(pOperation, Map.of()).values();
    }

    public void replaceFrom(AttributeInstance pInstance) {
        this.baseValue = pInstance.baseValue;
        this.modifierById.clear();
        this.modifierById.putAll(pInstance.modifierById);
        this.permanentModifiers.clear();
        this.permanentModifiers.putAll(pInstance.permanentModifiers);
        this.modifiersByOperation.clear();
        pInstance.modifiersByOperation.forEach((p_326791_, p_326792_) -> this.getModifiers(p_326791_).putAll((Map<? extends UUID, ? extends AttributeModifier>)p_326792_));
        this.setDirty();
    }

    public CompoundTag save() {
        CompoundTag compoundtag = new CompoundTag();
        ResourceKey<Attribute> resourcekey = this.attribute
            .unwrapKey()
            .orElseThrow(() -> new IllegalStateException("Tried to serialize unregistered attribute"));
        compoundtag.putString("Name", resourcekey.location().toString());
        compoundtag.putDouble("Base", this.baseValue);
        if (!this.permanentModifiers.isEmpty()) {
            ListTag listtag = new ListTag();

            for (AttributeModifier attributemodifier : this.permanentModifiers.values()) {
                listtag.add(attributemodifier.save());
            }

            compoundtag.put("Modifiers", listtag);
        }

        return compoundtag;
    }

    public void load(CompoundTag pNbt) {
        this.baseValue = pNbt.getDouble("Base");
        if (pNbt.contains("Modifiers", 9)) {
            ListTag listtag = pNbt.getList("Modifiers", 10);

            for (int i = 0; i < listtag.size(); i++) {
                AttributeModifier attributemodifier = AttributeModifier.load(listtag.getCompound(i));
                if (attributemodifier != null) {
                    this.modifierById.put(attributemodifier.id(), attributemodifier);
                    this.getModifiers(attributemodifier.operation()).put(attributemodifier.id(), attributemodifier);
                    this.permanentModifiers.put(attributemodifier.id(), attributemodifier);
                }
            }
        }

        this.setDirty();
    }
}