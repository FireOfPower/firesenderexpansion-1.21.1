package net.fireofpower.firesenderexpansion.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.fireofpower.firesenderexpansion.spells.HollowCrystalSpell;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(MouseHandler.class)
public class MouseMixin {
    @WrapOperation(
            method = "turnPlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V")
    )
    private void firesEnderExpansion$playerModifyTurnMovement(LocalPlayer player, double yRot, double xRot, Operation<Void> original)
    {
        HollowCrystalSpell blast = new HollowCrystalSpell();
        if (player.hasEffect(EffectRegistry.LOCKED_CAMERA_EFFECT))
        {
            // Fix camera movement to where the player is looking
            original.call(player, 0.0, 0.0);
        }
        else
        {
            // Normal camera movement here
            original.call(player, yRot, xRot);
        }
    }
}