package net.fireofpower.firesenderexpansion.effects;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AscendedCasterPotionEffect extends MagicMobEffect {

    public AscendedCasterPotionEffect() {
        super(MobEffectCategory.BENEFICIAL, 5984177);
        addAttributeModifier(AttributeRegistry.MANA_REGEN, ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID,"ascended_caster_effect"), (double)20.0F, AttributeModifier.Operation.ADD_VALUE);
    }
}
