package net.fireofpower.firesenderexpansion.mixins;

import io.redspace.ironsspellbooks.capabilities.magic.PortalManager;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalEntity;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.fireofpower.firesenderexpansion.events.ServerEvents.convertTicksToTime;

@Mixin(PortalManager.class)
public class AnchoredPortalStopperMixin {


    @Inject(method = "canUsePortal*", at = @At("HEAD"), cancellable = true)
    public void canUsePortal(PortalEntity portalEntity, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if(entity instanceof LivingEntity living){
            if(living.hasEffect(EffectRegistry.ANCHORED_EFFECT)){
                cir.setReturnValue(false);

                if (living instanceof ServerPlayer serverPlayer)
                {
                    // display a message to the player
                    serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("msg.firesenderexpansion.cannot_teleport")
                            .withStyle(s -> s.withColor(TextColor.fromRgb(0xF35F5F)))));
                    serverPlayer.level().playSound(null , living.getX() , living.getY() , living.getZ() ,
                            SoundEvents.FIRE_EXTINGUISH , SoundSource.PLAYERS , 0.5f , 1f);
                }
            }
        }
    }

}
