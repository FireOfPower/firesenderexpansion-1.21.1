package net.fireofpower.firesenderexpansion.network;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = FiresEnderExpansion.MODID)
public class PayloadHandler {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar payloadRegistrar = event.registrar(FiresEnderExpansion.MODID).versioned("1.0.0").optional();


        payloadRegistrar.playToClient(SyncFinalCastPacket.TYPE, SyncFinalCastPacket.STREAM_CODEC, SyncFinalCastPacket::handle);
        payloadRegistrar.playToClient(PlayShaderEffectPacket.TYPE, PlayShaderEffectPacket.STREAM_CODEC, PlayShaderEffectPacket::handle);
        payloadRegistrar.playToClient(RemoveShaderEffectPacket.TYPE, RemoveShaderEffectPacket.STREAM_CODEC, RemoveShaderEffectPacket::handle);

    }
}
