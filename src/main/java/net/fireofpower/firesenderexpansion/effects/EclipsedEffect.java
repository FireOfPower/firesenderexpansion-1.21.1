package net.fireofpower.firesenderexpansion.effects;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber
public class EclipsedEffect extends MagicMobEffect {

    private static final Map<LivingEntity,Integer> buffTracking = new HashMap<>();

    public EclipsedEffect() {
        super(MobEffectCategory.HARMFUL, Utils.rgbToInt(0,0,0));
    }

    public static void updateAttributeModifiers(LivingEntity pLivingEntity) {
        AttributeInstance instanceAtkSpeed = pLivingEntity.getAttributes().getInstance(Attributes.ATTACK_SPEED);
        AttributeInstance instanceAtkPower = pLivingEntity.getAttributes().getInstance(Attributes.ATTACK_DAMAGE);

        if (instanceAtkPower != null && instanceAtkSpeed != null && buffTracking.containsKey(pLivingEntity)) {
            instanceAtkPower.addOrUpdateTransientModifier(new AttributeModifier(FiresEnderExpansion.id("eclipsed.power"), buffTracking.get(pLivingEntity) * -0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            instanceAtkSpeed.addOrUpdateTransientModifier(new AttributeModifier(FiresEnderExpansion.id("eclipsed.speed"), buffTracking.get(pLivingEntity) * -0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
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
        AttributeInstance instanceAtkSpeed = pLivingEntity.getAttributes().getInstance(Attributes.ATTACK_SPEED);
        AttributeInstance instanceAtkPower = pLivingEntity.getAttributes().getInstance(Attributes.ATTACK_DAMAGE);

        if(buffTracking.containsKey(pLivingEntity)) {
            buffTracking.remove(pLivingEntity);
        }

        if (instanceAtkPower != null && instanceAtkSpeed != null) {
            instanceAtkPower.removeModifier(FiresEnderExpansion.id("eclipsed.power"));
            instanceAtkSpeed.removeModifier(FiresEnderExpansion.id("eclipsed.speed"));
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
            updateAttributeModifiers(event.getEntity());
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
            updateAttributeModifiers(event.getEntity());
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
        updateAttributeModifiers(event.getEntity());
    }

}
