package net.fireofpower.firesenderexpansion.effects;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.NeoForgeMod;

public class AscendedCasterEffect extends MagicMobEffect {

    public AscendedCasterEffect() {
        super(MobEffectCategory.BENEFICIAL, Utils.rgbToInt(125,55,171));
        addAttributeModifier(AttributeRegistry.MANA_REGEN, ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID,"ascended_caster_mana_regen"), 0.25, AttributeModifier.Operation.ADD_VALUE);
        addAttributeModifier(AttributeRegistry.SPELL_POWER, ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID,"ascended_caster_spell_power"),0.1,AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        addAttributeModifier(AttributeRegistry.COOLDOWN_REDUCTION, ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID,"ascended_caster_cooldown_reduction"),0.20,AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        addAttributeModifier(NeoForgeMod.CREATIVE_FLIGHT, ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID,"ascended_caster_flight"),1,AttributeModifier.Operation.ADD_VALUE);
    }
}
