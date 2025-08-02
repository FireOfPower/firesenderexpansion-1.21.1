package net.fireofpower.firesenderexpansion.mixins;

import io.redspace.ironsspellbooks.effect.EvasionEffect;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.fireofpower.firesenderexpansion.events.ServerEvents.convertTicksToTime;

@Mixin(EvasionEffect.class)
public class AnchoredEvasionStopperMixin {

    @Inject(method = "doEffect", at = @At("HEAD"), cancellable = true)
    private static void doEffect(LivingEntity livingEntity, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir){
        if(livingEntity.hasEffect(EffectRegistry.ANCHORED_EFFECT)){
            cir.setReturnValue(false);
            int time = livingEntity.getEffect(EffectRegistry.ANCHORED_EFFECT).getDuration();
            // convert duration to time format  using the method convertTicksToTime
            String formattedTime = convertTicksToTime(time);

            if (livingEntity instanceof ServerPlayer serverPlayer)
            {
                // display a message to the player
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.literal(ChatFormatting.BOLD + "Unable to use teleportation spells for : " + formattedTime)
                        .withStyle(s -> s.withColor(TextColor.fromRgb(0xF35F5F)))));
                serverPlayer.level().playSound(null , livingEntity.getX() , livingEntity.getY() , livingEntity.getZ() ,
                        SoundEvents.FIRE_EXTINGUISH , SoundSource.PLAYERS , 0.5f , 1f);
            }
        }
    }
}
