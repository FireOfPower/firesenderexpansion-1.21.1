package net.fireofpower.firesenderexpansion.events;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerHeartTypeEvent;

@EventBusSubscriber(modid = FiresEnderExpansion.MODID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onPlayerHeartTypeEvent(PlayerHeartTypeEvent event) {
        Player player = event.getEntity();

        if (player.level().isClientSide() && player.hasEffect(EffectRegistry.VOIDTORN_EFFECT)) {
            event.setType(Gui.HeartType.valueOf("firesenderexpansion_voidtorn"));
        }
    }
}
