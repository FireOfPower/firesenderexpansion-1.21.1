package net.fireofpower.firesenderexpansion.effects;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EclipsedEffect extends MagicMobEffect {
    public EclipsedEffect() {
        super(MobEffectCategory.HARMFUL, Utils.rgbToInt(0,0,0));
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity.tickCount % 20 == 0) {
            int numPositiveEffects = 0;
            for (MobEffectInstance instance : pLivingEntity.getActiveEffects()) {
                if (instance.getEffect().value().isBeneficial()) {
                    numPositiveEffects++;
                }
            }
            Mth.clamp(numPositiveEffects, 0, 10);
            AttributeInstance instanceAtkSpeed = pLivingEntity.getAttributes().getInstance(Attributes.ATTACK_SPEED);
            AttributeInstance instanceAtkPower = pLivingEntity.getAttributes().getInstance(Attributes.ATTACK_DAMAGE);

            if (instanceAtkPower != null && instanceAtkSpeed != null) {
                instanceAtkPower.removeModifier(FiresEnderExpansion.id("eclipsed.power"));
                instanceAtkPower.addOrUpdateTransientModifier(new AttributeModifier(FiresEnderExpansion.id("eclipsed.power"), numPositiveEffects * -0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

                instanceAtkSpeed.removeModifier(FiresEnderExpansion.id("eclipsed.speed"));
                instanceAtkSpeed.addOrUpdateTransientModifier(new AttributeModifier(FiresEnderExpansion.id("eclipsed.speed"), numPositiveEffects * -0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        }
        return super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
        AttributeInstance instanceAtkSpeed = pLivingEntity.getAttributes().getInstance(Attributes.ATTACK_SPEED);
        AttributeInstance instanceAtkPower = pLivingEntity.getAttributes().getInstance(Attributes.ATTACK_DAMAGE);

        if (instanceAtkPower != null && instanceAtkSpeed != null) {
            instanceAtkPower.removeModifier(FiresEnderExpansion.id("eclipsed.power"));
            instanceAtkSpeed.removeModifier(FiresEnderExpansion.id("eclipsed.speed"));
        }
        super.onEffectRemoved(pLivingEntity, pAmplifier);
    }
}
