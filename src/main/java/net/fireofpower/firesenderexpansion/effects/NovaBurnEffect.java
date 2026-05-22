package net.fireofpower.firesenderexpansion.effects;

import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.fireofpower.firesenderexpansion.damage.NovaBurnDamageSource;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber
public class NovaBurnEffect extends MagicMobEffect {
    private static final Map<LivingEntity,Integer> buffTracking = new HashMap<>();

    public NovaBurnEffect() {
        super(MobEffectCategory.HARMFUL, Utils.rgbToInt(255,154,255));
    }

    @SubscribeEvent
    public static void onSpellCastEvent(SpellPreCastEvent event){
        LivingEntity caster = event.getEntity();
        if(caster.hasEffect(EffectRegistry.NOVA_BURN_EFFECT)){
            System.out.println("Hurting " + caster + " for 3 * " + getNumPositiveEffects(caster) + " * " + caster.getEffect(EffectRegistry.NOVA_BURN_EFFECT).getAmplifier());
            float amount = 5 * getNumPositiveEffects(caster) * caster.getEffect(EffectRegistry.NOVA_BURN_EFFECT).getAmplifier();
            caster.hurt(new NovaBurnDamageSource(caster),amount);
        }
    }

    public static int getNumPositiveEffects(LivingEntity livingEntity){
        //get number of positive effects
        int numPositiveEffects = 0;
        for (MobEffectInstance instance : livingEntity.getActiveEffects()) {
            if (instance.getEffect().value().isBeneficial()) {
                numPositiveEffects++;
            }
        }
        return numPositiveEffects;
    }

    @Override
    public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
        if(buffTracking.containsKey(pLivingEntity)) {
            buffTracking.remove(pLivingEntity);
        }
        super.onEffectRemoved(pLivingEntity, pAmplifier);
    }

    @SubscribeEvent
    public static void onEffectsGained(MobEffectEvent.Added event){
        if(event.getEntity().hasEffect(EffectRegistry.ECLIPSED_EFFECT)){
            //if we don't already track them then we need to manually figure out how many positive effects they have
            if(!buffTracking.containsKey(event.getEntity())){
                buffTracking.put(event.getEntity(), getNumPositiveEffects(event.getEntity()));
            }
            //increment if necessary
            if(event.getEffectInstance().getEffect().value().isBeneficial()){
                buffTracking.replace(event.getEntity(), buffTracking.get(event.getEntity()) + 1);
            }
        }
    }

    @SubscribeEvent
    public static void onEffectsRemoved(MobEffectEvent.Remove event){
        if(event.getEntity().hasEffect(EffectRegistry.ECLIPSED_EFFECT)){
            if(event.getEffectInstance().getEffect().value().isBeneficial()){
                if(buffTracking.containsKey(event.getEntity())) {
                    buffTracking.replace(event.getEntity(), buffTracking.get(event.getEntity()) - 1);
                }else{
                    buffTracking.put(event.getEntity(), getNumPositiveEffects(event.getEntity()));
                }
            }
        }
    }

    //Got to account for it being cleared and for it expiring...
    @SubscribeEvent
    public static void onEffectsExpired(MobEffectEvent.Expired event){
        if(event.getEntity().hasEffect(EffectRegistry.ECLIPSED_EFFECT)){
            if(event.getEffectInstance().getEffect().value().isBeneficial()){
                if(buffTracking.containsKey(event.getEntity())) {
                    buffTracking.replace(event.getEntity(), buffTracking.get(event.getEntity()) - 1);
                }else{
                    buffTracking.put(event.getEntity(), getNumPositiveEffects(event.getEntity()));
                }
            }
        }
    }
}
