package net.fireofpower.firesenderexpansion.effects;

import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

@EventBusSubscriber
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

    @SubscribeEvent
    public static void preventMilk(MobEffectEvent.Remove event){
        if(event.getCure() != null && (event.getCure().equals(EffectCures.MILK) || event.getCure().equals(EffectCures.PROTECTED_BY_TOTEM))){
            event.setCanceled(true);
        }
    }
}
