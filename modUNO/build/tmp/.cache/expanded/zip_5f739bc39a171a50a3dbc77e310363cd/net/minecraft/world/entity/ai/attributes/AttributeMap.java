package net.minecraft.world.entity.ai.attributes;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class AttributeMap {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<Holder<Attribute>, AttributeInstance> attributes = new Object2ObjectOpenHashMap<>();
    private final Set<AttributeInstance> dirtyAttributes = new ObjectOpenHashSet<>();
    private final AttributeSupplier supplier;

    public AttributeMap(AttributeSupplier pSupplier) {
        this.supplier = pSupplier;
    }

    private void onAttributeModified(AttributeInstance p_22158_) {
        if (p_22158_.getAttribute().value().isClientSyncable()) {
            this.dirtyAttributes.add(p_22158_);
        }
    }

    public Set<AttributeInstance> getDirtyAttributes() {
        return this.dirtyAttributes;
    }

    public Collection<AttributeInstance> getSyncableAttributes() {
        return this.attributes.values().stream().filter(p_326797_ -> p_326797_.getAttribute().value().isClientSyncable()).collect(Collectors.toList());
    }

    @Nullable
    public AttributeInstance getInstance(Holder<Attribute> pAttribute) {
        return this.attributes.computeIfAbsent(pAttribute, p_326793_ -> this.supplier.createInstance(this::onAttributeModified, (Holder<Attribute>)p_326793_));
    }

    public boolean hasAttribute(Holder<Attribute> pAttribute) {
        return this.attributes.get(pAttribute) != null || this.supplier.hasAttribute(pAttribute);
    }

    public boolean hasModifier(Holder<Attribute> pAttribute, UUID pUuid) {
        AttributeInstance attributeinstance = this.attributes.get(pAttribute);
        return attributeinstance != null ? attributeinstance.getModifier(pUuid) != null : this.supplier.hasModifier(pAttribute, pUuid);
    }

    public double getValue(Holder<Attribute> pAttribute) {
        AttributeInstance attributeinstance = this.attributes.get(pAttribute);
        return attributeinstance != null ? attributeinstance.getValue() : this.supplier.getValue(pAttribute);
    }

    public double getBaseValue(Holder<Attribute> pAttribute) {
        AttributeInstance attributeinstance = this.attributes.get(pAttribute);
        return attributeinstance != null ? attributeinstance.getBaseValue() : this.supplier.getBaseValue(pAttribute);
    }

    public double getModifierValue(Holder<Attribute> pAttribute, UUID pUuid) {
        AttributeInstance attributeinstance = this.attributes.get(pAttribute);
        return attributeinstance != null ? attributeinstance.getModifier(pUuid).amount() : this.supplier.getModifierValue(pAttribute, pUuid);
    }

    public void assignValues(AttributeMap pManager) {
        pManager.attributes.values().forEach(p_326796_ -> {
            AttributeInstance attributeinstance = this.getInstance(p_326796_.getAttribute());
            if (attributeinstance != null) {
                attributeinstance.replaceFrom(p_326796_);
            }
        });
    }

    public ListTag save() {
        ListTag listtag = new ListTag();

        for (AttributeInstance attributeinstance : this.attributes.values()) {
            listtag.add(attributeinstance.save());
        }

        return listtag;
    }

    public void load(ListTag pNbt) {
        for (int i = 0; i < pNbt.size(); i++) {
            CompoundTag compoundtag = pNbt.getCompound(i);
            String s = compoundtag.getString("Name");
            ResourceLocation resourcelocation = ResourceLocation.tryParse(s);
            if (resourcelocation != null) {
                Util.ifElse(BuiltInRegistries.ATTRIBUTE.getHolder(resourcelocation), p_326795_ -> {
                    AttributeInstance attributeinstance = this.getInstance(p_326795_);
                    if (attributeinstance != null) {
                        attributeinstance.load(compoundtag);
                    }
                }, () -> LOGGER.warn("Ignoring unknown attribute '{}'", resourcelocation));
            } else {
                LOGGER.warn("Ignoring malformed attribute '{}'", s);
            }
        }
    }
}