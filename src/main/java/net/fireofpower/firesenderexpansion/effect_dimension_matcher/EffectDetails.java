package net.fireofpower.firesenderexpansion.effect_dimension_matcher;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

import javax.annotation.Nullable;

public class EffectDetails {
    @Nullable
    Holder<MobEffect> effect;
    int duration;

    public EffectDetails(Holder<MobEffect> effect, int duration){
        this.effect = effect;
        this.duration = duration;
    }

    public Holder<MobEffect> getEffect() {
        return effect;
    }

    public void setEffect(Holder<MobEffect> effect) {
        this.effect = effect;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
