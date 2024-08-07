package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.UUID;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttributeCommand {
    private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType(
        p_308625_ -> Component.translatableEscape("commands.attribute.failed.entity", p_308625_)
    );
    private static final Dynamic2CommandExceptionType ERROR_NO_SUCH_ATTRIBUTE = new Dynamic2CommandExceptionType(
        (p_308616_, p_308617_) -> Component.translatableEscape("commands.attribute.failed.no_attribute", p_308616_, p_308617_)
    );
    private static final Dynamic3CommandExceptionType ERROR_NO_SUCH_MODIFIER = new Dynamic3CommandExceptionType(
        (p_308629_, p_308630_, p_308631_) -> Component.translatableEscape("commands.attribute.failed.no_modifier", p_308630_, p_308629_, p_308631_)
    );
    private static final Dynamic3CommandExceptionType ERROR_MODIFIER_ALREADY_PRESENT = new Dynamic3CommandExceptionType(
        (p_308626_, p_308627_, p_308628_) -> Component.translatableEscape("commands.attribute.failed.modifier_already_present", p_308628_, p_308627_, p_308626_)
    );

    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher, CommandBuildContext pContext) {
        pDispatcher.register(
            Commands.literal("attribute")
                .requires(p_212441_ -> p_212441_.hasPermission(2))
                .then(
                    Commands.argument("target", EntityArgument.entity())
                        .then(
                            Commands.argument("attribute", ResourceArgument.resource(pContext, Registries.ATTRIBUTE))
                                .then(
                                    Commands.literal("get")
                                        .executes(
                                            p_248109_ -> getAttributeValue(
                                                    p_248109_.getSource(),
                                                    EntityArgument.getEntity(p_248109_, "target"),
                                                    ResourceArgument.getAttribute(p_248109_, "attribute"),
                                                    1.0
                                                )
                                        )
                                        .then(
                                            Commands.argument("scale", DoubleArgumentType.doubleArg())
                                                .executes(
                                                    p_248104_ -> getAttributeValue(
                                                            p_248104_.getSource(),
                                                            EntityArgument.getEntity(p_248104_, "target"),
                                                            ResourceArgument.getAttribute(p_248104_, "attribute"),
                                                            DoubleArgumentType.getDouble(p_248104_, "scale")
                                                        )
                                                )
                                        )
                                )
                                .then(
                                    Commands.literal("base")
                                        .then(
                                            Commands.literal("set")
                                                .then(
                                                    Commands.argument("value", DoubleArgumentType.doubleArg())
                                                        .executes(
                                                            p_248102_ -> setAttributeBase(
                                                                    p_248102_.getSource(),
                                                                    EntityArgument.getEntity(p_248102_, "target"),
                                                                    ResourceArgument.getAttribute(p_248102_, "attribute"),
                                                                    DoubleArgumentType.getDouble(p_248102_, "value")
                                                                )
                                                        )
                                                )
                                        )
                                        .then(
                                            Commands.literal("get")
                                                .executes(
                                                    p_248112_ -> getAttributeBase(
                                                            p_248112_.getSource(),
                                                            EntityArgument.getEntity(p_248112_, "target"),
                                                            ResourceArgument.getAttribute(p_248112_, "attribute"),
                                                            1.0
                                                        )
                                                )
                                                .then(
                                                    Commands.argument("scale", DoubleArgumentType.doubleArg())
                                                        .executes(
                                                            p_248106_ -> getAttributeBase(
                                                                    p_248106_.getSource(),
                                                                    EntityArgument.getEntity(p_248106_, "target"),
                                                                    ResourceArgument.getAttribute(p_248106_, "attribute"),
                                                                    DoubleArgumentType.getDouble(p_248106_, "scale")
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(
                                    Commands.literal("modifier")
                                        .then(
                                            Commands.literal("add")
                                                .then(
                                                    Commands.argument("uuid", UuidArgument.uuid())
                                                        .then(
                                                            Commands.argument("name", StringArgumentType.string())
                                                                .then(
                                                                    Commands.argument("value", DoubleArgumentType.doubleArg())
                                                                        .then(
                                                                            Commands.literal("add_value")
                                                                                .executes(
                                                                                    p_326222_ -> addModifier(
                                                                                            p_326222_.getSource(),
                                                                                            EntityArgument.getEntity(p_326222_, "target"),
                                                                                            ResourceArgument.getAttribute(p_326222_, "attribute"),
                                                                                            UuidArgument.getUuid(p_326222_, "uuid"),
                                                                                            StringArgumentType.getString(p_326222_, "name"),
                                                                                            DoubleArgumentType.getDouble(p_326222_, "value"),
                                                                                            AttributeModifier.Operation.ADD_VALUE
                                                                                        )
                                                                                )
                                                                        )
                                                                        .then(
                                                                            Commands.literal("add_multiplied_base")
                                                                                .executes(
                                                                                    p_326223_ -> addModifier(
                                                                                            p_326223_.getSource(),
                                                                                            EntityArgument.getEntity(p_326223_, "target"),
                                                                                            ResourceArgument.getAttribute(p_326223_, "attribute"),
                                                                                            UuidArgument.getUuid(p_326223_, "uuid"),
                                                                                            StringArgumentType.getString(p_326223_, "name"),
                                                                                            DoubleArgumentType.getDouble(p_326223_, "value"),
                                                                                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                                                                                        )
                                                                                )
                                                                        )
                                                                        .then(
                                                                            Commands.literal("add_multiplied_total")
                                                                                .executes(
                                                                                    p_326224_ -> addModifier(
                                                                                            p_326224_.getSource(),
                                                                                            EntityArgument.getEntity(p_326224_, "target"),
                                                                                            ResourceArgument.getAttribute(p_326224_, "attribute"),
                                                                                            UuidArgument.getUuid(p_326224_, "uuid"),
                                                                                            StringArgumentType.getString(p_326224_, "name"),
                                                                                            DoubleArgumentType.getDouble(p_326224_, "value"),
                                                                                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                        .then(
                                            Commands.literal("remove")
                                                .then(
                                                    Commands.argument("uuid", UuidArgument.uuid())
                                                        .executes(
                                                            p_248103_ -> removeModifier(
                                                                    p_248103_.getSource(),
                                                                    EntityArgument.getEntity(p_248103_, "target"),
                                                                    ResourceArgument.getAttribute(p_248103_, "attribute"),
                                                                    UuidArgument.getUuid(p_248103_, "uuid")
                                                                )
                                                        )
                                                )
                                        )
                                        .then(
                                            Commands.literal("value")
                                                .then(
                                                    Commands.literal("get")
                                                        .then(
                                                            Commands.argument("uuid", UuidArgument.uuid())
                                                                .executes(
                                                                    p_248110_ -> getAttributeModifier(
                                                                            p_248110_.getSource(),
                                                                            EntityArgument.getEntity(p_248110_, "target"),
                                                                            ResourceArgument.getAttribute(p_248110_, "attribute"),
                                                                            UuidArgument.getUuid(p_248110_, "uuid"),
                                                                            1.0
                                                                        )
                                                                )
                                                                .then(
                                                                    Commands.argument("scale", DoubleArgumentType.doubleArg())
                                                                        .executes(
                                                                            p_248111_ -> getAttributeModifier(
                                                                                    p_248111_.getSource(),
                                                                                    EntityArgument.getEntity(p_248111_, "target"),
                                                                                    ResourceArgument.getAttribute(p_248111_, "attribute"),
                                                                                    UuidArgument.getUuid(p_248111_, "uuid"),
                                                                                    DoubleArgumentType.getDouble(p_248111_, "scale")
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static AttributeInstance getAttributeInstance(Entity pEntity, Holder<Attribute> pAttribute) throws CommandSyntaxException {
        AttributeInstance attributeinstance = getLivingEntity(pEntity).getAttributes().getInstance(pAttribute);
        if (attributeinstance == null) {
            throw ERROR_NO_SUCH_ATTRIBUTE.create(pEntity.getName(), getAttributeDescription(pAttribute));
        } else {
            return attributeinstance;
        }
    }

    private static LivingEntity getLivingEntity(Entity pTarget) throws CommandSyntaxException {
        if (!(pTarget instanceof LivingEntity)) {
            throw ERROR_NOT_LIVING_ENTITY.create(pTarget.getName());
        } else {
            return (LivingEntity)pTarget;
        }
    }

    private static LivingEntity getEntityWithAttribute(Entity pEntity, Holder<Attribute> pAttribute) throws CommandSyntaxException {
        LivingEntity livingentity = getLivingEntity(pEntity);
        if (!livingentity.getAttributes().hasAttribute(pAttribute)) {
            throw ERROR_NO_SUCH_ATTRIBUTE.create(pEntity.getName(), getAttributeDescription(pAttribute));
        } else {
            return livingentity;
        }
    }

    private static int getAttributeValue(CommandSourceStack pSource, Entity pEntity, Holder<Attribute> pAttribute, double pScale) throws CommandSyntaxException {
        LivingEntity livingentity = getEntityWithAttribute(pEntity, pAttribute);
        double d0 = livingentity.getAttributeValue(pAttribute);
        pSource.sendSuccess(() -> Component.translatable("commands.attribute.value.get.success", getAttributeDescription(pAttribute), pEntity.getName(), d0), false);
        return (int)(d0 * pScale);
    }

    private static int getAttributeBase(CommandSourceStack pSource, Entity pEntity, Holder<Attribute> pAttribute, double pScale) throws CommandSyntaxException {
        LivingEntity livingentity = getEntityWithAttribute(pEntity, pAttribute);
        double d0 = livingentity.getAttributeBaseValue(pAttribute);
        pSource.sendSuccess(() -> Component.translatable("commands.attribute.base_value.get.success", getAttributeDescription(pAttribute), pEntity.getName(), d0), false);
        return (int)(d0 * pScale);
    }

    private static int getAttributeModifier(CommandSourceStack pSource, Entity pEntity, Holder<Attribute> pAttribute, UUID pUuid, double pScale) throws CommandSyntaxException {
        LivingEntity livingentity = getEntityWithAttribute(pEntity, pAttribute);
        AttributeMap attributemap = livingentity.getAttributes();
        if (!attributemap.hasModifier(pAttribute, pUuid)) {
            throw ERROR_NO_SUCH_MODIFIER.create(pEntity.getName(), getAttributeDescription(pAttribute), pUuid);
        } else {
            double d0 = attributemap.getModifierValue(pAttribute, pUuid);
            pSource.sendSuccess(
                () -> Component.translatable(
                        "commands.attribute.modifier.value.get.success", Component.translationArg(pUuid), getAttributeDescription(pAttribute), pEntity.getName(), d0
                    ),
                false
            );
            return (int)(d0 * pScale);
        }
    }

    private static int setAttributeBase(CommandSourceStack pSource, Entity pEntity, Holder<Attribute> pAttribute, double pValue) throws CommandSyntaxException {
        getAttributeInstance(pEntity, pAttribute).setBaseValue(pValue);
        pSource.sendSuccess(() -> Component.translatable("commands.attribute.base_value.set.success", getAttributeDescription(pAttribute), pEntity.getName(), pValue), false);
        return 1;
    }

    private static int addModifier(
        CommandSourceStack pSource,
        Entity pEntity,
        Holder<Attribute> pAttribute,
        UUID pUuid,
        String pName,
        double pAmount,
        AttributeModifier.Operation pOperation
    ) throws CommandSyntaxException {
        AttributeInstance attributeinstance = getAttributeInstance(pEntity, pAttribute);
        AttributeModifier attributemodifier = new AttributeModifier(pUuid, pName, pAmount, pOperation);
        if (attributeinstance.hasModifier(attributemodifier)) {
            throw ERROR_MODIFIER_ALREADY_PRESENT.create(pEntity.getName(), getAttributeDescription(pAttribute), pUuid);
        } else {
            attributeinstance.addPermanentModifier(attributemodifier);
            pSource.sendSuccess(
                () -> Component.translatable("commands.attribute.modifier.add.success", Component.translationArg(pUuid), getAttributeDescription(pAttribute), pEntity.getName()),
                false
            );
            return 1;
        }
    }

    private static int removeModifier(CommandSourceStack pSource, Entity pEntity, Holder<Attribute> pAttribute, UUID pUuid) throws CommandSyntaxException {
        AttributeInstance attributeinstance = getAttributeInstance(pEntity, pAttribute);
        if (attributeinstance.removePermanentModifier(pUuid)) {
            pSource.sendSuccess(
                () -> Component.translatable(
                        "commands.attribute.modifier.remove.success", Component.translationArg(pUuid), getAttributeDescription(pAttribute), pEntity.getName()
                    ),
                false
            );
            return 1;
        } else {
            throw ERROR_NO_SUCH_MODIFIER.create(pEntity.getName(), getAttributeDescription(pAttribute), pUuid);
        }
    }

    private static Component getAttributeDescription(Holder<Attribute> pAttribute) {
        return Component.translatable(pAttribute.value().getDescriptionId());
    }
}