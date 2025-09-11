package net.fireofpower.firesenderexpansion.effects;

import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class NovaBurnEffect extends MagicMobEffect {
    public NovaBurnEffect() {
        super(MobEffectCategory.HARMFUL, Utils.rgbToInt(255,154,255));
    }

    @SubscribeEvent
    public static void onSpellCastEvent(SpellPreCastEvent event){
        LivingEntity caster = event.getEntity();
        if(caster.hasEffect(EffectRegistry.NOVA_BURN_EFFECT)){
            float amount = 10 * caster.getEffect(EffectRegistry.NOVA_BURN_EFFECT).getAmplifier();
            caster.hurt(caster.damageSources().magic(),amount);
        }
    }
}
