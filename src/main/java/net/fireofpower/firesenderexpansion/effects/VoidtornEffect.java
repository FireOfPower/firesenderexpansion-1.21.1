package net.fireofpower.firesenderexpansion.effects;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class VoidtornEffect extends MagicMobEffect {
    public VoidtornEffect() {
        super(MobEffectCategory.HARMFUL, Utils.rgbToInt(0,0,0));
        addAttributeModifier(AttributeRegistry.MANA_REGEN, ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID,"voidtorn_mana_regen"), -0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID,"voidtorn_armor"),-5,AttributeModifier.Operation.ADD_VALUE);
    }
}
