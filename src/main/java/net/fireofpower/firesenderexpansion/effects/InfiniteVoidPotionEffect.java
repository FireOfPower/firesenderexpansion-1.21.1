package net.fireofpower.firesenderexpansion.effects;


import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.spells.holy.HasteSpell;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class InfiniteVoidPotionEffect extends MagicMobEffect {
    public InfiniteVoidPotionEffect() {
        super(MobEffectCategory.BENEFICIAL, 5984177);
        addAttributeModifier(AttributeRegistry.MANA_REGEN, ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID,"infinite_void_effect"), (double)20.0F, AttributeModifier.Operation.ADD_VALUE);
    }
}
