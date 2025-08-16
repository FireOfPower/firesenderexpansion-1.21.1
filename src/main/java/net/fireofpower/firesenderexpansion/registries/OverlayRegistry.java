package net.fireofpower.firesenderexpansion.registries;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.gui.overlays.ScreenEffectsOverlay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = FiresEnderExpansion.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class OverlayRegistry {
    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiLayersEvent event) {

        event.registerAboveAll(FiresEnderExpansion.id("screen_effects"), ScreenEffectsOverlay.instance);
    }
}
