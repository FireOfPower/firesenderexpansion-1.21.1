package net.fireofpower.firesenderexpansion.effects;

import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class TurnLockedEffect extends MobEffect {
    public TurnLockedEffect() {
        super(MobEffectCategory.NEUTRAL, Utils.rgbToInt(255,255,255));
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        livingEntity.setDeltaMovement(0,0,0);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
