package net.fireofpower.firesenderexpansion.effects;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class VoidBurnPotionEffect extends MagicMobEffect {
    public VoidBurnPotionEffect() {
        super(MobEffectCategory.HARMFUL, 5984177);

    }
    public boolean applyEffectTick(LivingEntity p_296279_, int p_294798_) {
        p_296279_.hurt(p_296279_.damageSources().genericKill(), 999F);
        //p_296279_.kill();
        return true;
    }

    public boolean shouldApplyEffectTickThisTick(int p_295629_, int p_295734_) {
        int i = 40 >> p_295734_;
        return i == 0 || p_295629_ % i == 0;
    }
}
