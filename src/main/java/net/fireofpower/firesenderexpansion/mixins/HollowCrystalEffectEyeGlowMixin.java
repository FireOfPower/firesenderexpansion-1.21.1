package net.fireofpower.firesenderexpansion.mixins;

import io.redspace.ironsspellbooks.render.GlowingEyesLayer;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GlowingEyesLayer.class)
public class HollowCrystalEffectEyeGlowMixin {

    @Inject(method="getEyeType",at=@At("HEAD"), cancellable = true)
    private static void getEyeType(LivingEntity entity, CallbackInfoReturnable<GlowingEyesLayer.EyeType> cir){
        if(entity.hasEffect(EffectRegistry.HOLLOW_CRYSTAL_EFFECT)){
            cir.setReturnValue(GlowingEyesLayer.EyeType.Planar_Sight);
        }
    }

    @Inject(method="getEyeScale",at=@At("HEAD"), cancellable = true)
    private static void getEyeScale(LivingEntity entity, CallbackInfoReturnable<Float> cir){
        if(entity.hasEffect(EffectRegistry.HOLLOW_CRYSTAL_EFFECT)){
            cir.setReturnValue(GlowingEyesLayer.EyeType.Planar_Sight.scale);
        }
    }
}
