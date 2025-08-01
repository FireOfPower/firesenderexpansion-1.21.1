package net.fireofpower.firesenderexpansion.mixins;

import io.redspace.ironsspellbooks.capabilities.magic.MagicEvents;
import net.fireofpower.firesenderexpansion.capabilities.magic.VoidDimensionManager;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MagicEvents.class)
public class VoidDimensionTickingMixin {

    @Inject(method = "onWorldTick", at = @At("TAIL"))
    private static void onWorldTick(LevelTickEvent.Pre event, CallbackInfo ci){
        VoidDimensionManager.INSTANCE.tick(event.getLevel());
    }
}
