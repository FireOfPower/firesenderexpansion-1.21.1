package net.fireofpower.firesenderexpansion.effects;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.utils.ChromaticAberrationHandler;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.network.AddShaderEffectPacket;
import net.fireofpower.firesenderexpansion.network.RemoveShaderEffectPacket;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class HollowCrystalEffect extends MagicMobEffect {
    public HollowCrystalEffect(){
        super(MobEffectCategory.BENEFICIAL, net.fireofpower.firesenderexpansion.util.Utils.rgbToInt(255,224,255));
    }

    @Override
    public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if(pLivingEntity instanceof ServerPlayer serverPlayer) {
            ChromaticAberrationHandler.trigger(serverPlayer, 0.15f * pAmplifier, 40);
        }
        return super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 40 == 0;
    }

    @SubscribeEvent
    public static void preventMilk(MobEffectEvent.Remove event){
        if(event.getCure() != null && (event.getCure().equals(EffectCures.MILK) || event.getCure().equals(EffectCures.PROTECTED_BY_TOTEM))){
            if(event.getEffect().equals(EffectRegistry.HOLLOW_CRYSTAL_EFFECT)) {
                event.setCanceled(true);
            }
        }
    }
}
