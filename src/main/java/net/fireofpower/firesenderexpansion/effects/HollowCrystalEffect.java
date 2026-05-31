package net.fireofpower.firesenderexpansion.effects;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

@EventBusSubscriber
public class HollowCrystalEffect extends MagicMobEffect {
    public HollowCrystalEffect(){
        super(MobEffectCategory.BENEFICIAL, net.fireofpower.firesenderexpansion.util.Utils.rgbToInt(255,224,255));
    }

    @SubscribeEvent
    public static void preventMilk(MobEffectEvent.Remove event){
        if(event.getCure() != null && (event.getCure().equals(EffectCures.MILK) || event.getCure().equals(EffectCures.PROTECTED_BY_TOTEM))){
            event.setCanceled(true);
        }
    }
}
