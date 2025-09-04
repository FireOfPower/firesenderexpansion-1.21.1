package net.fireofpower.firesenderexpansion.network;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
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
        payloadRegistrar.playToClient(AddShaderEffectPacket.TYPE, AddShaderEffectPacket.STREAM_CODEC, AddShaderEffectPacket::handle);
        payloadRegistrar.playToClient(RemoveShaderEffectPacket.TYPE, RemoveShaderEffectPacket.STREAM_CODEC, RemoveShaderEffectPacket::handle);

    }

    public static void handleClientBoundShaderEffect(String modid, String location){
        Minecraft mc = Minecraft.getInstance();
        GameRenderer render = mc.gameRenderer;
        LocalPlayer clientPlayer = mc.player;
        if(clientPlayer != null) {
            render.loadEffect(ResourceLocation.fromNamespaceAndPath(modid,location));
        }
    }

    public static void removeClientBoundShaderEffect(){
        Minecraft mc = Minecraft.getInstance();
        GameRenderer render = mc.gameRenderer;
        LocalPlayer clientPlayer = mc.player;
        if(clientPlayer != null) {
            render.shutdownEffect();
        }
    }
}
