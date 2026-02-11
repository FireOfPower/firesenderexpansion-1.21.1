package net.fireofpower.firesenderexpansion.network;

import net.fireofpower.firesenderexpansion.ClientConfig;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.joml.Vector3f;

@EventBusSubscriber(modid = FiresEnderExpansion.MODID)
public class PayloadHandler {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar payloadRegistrar = event.registrar(FiresEnderExpansion.MODID).versioned("1.0.0").optional();


        payloadRegistrar.playToClient(SyncFinalCastPacket.TYPE, SyncFinalCastPacket.STREAM_CODEC, SyncFinalCastPacket::handle);
        payloadRegistrar.playToClient(AddShaderEffectPacket.TYPE, AddShaderEffectPacket.STREAM_CODEC, AddShaderEffectPacket::handle);
        payloadRegistrar.playToClient(RemoveShaderEffectPacket.TYPE, RemoveShaderEffectPacket.STREAM_CODEC, RemoveShaderEffectPacket::handle);
        payloadRegistrar.playToClient(DoParticleBurstPacket.TYPE, DoParticleBurstPacket.STREAM_CODEC, DoParticleBurstPacket::handle);

    }

    public static void handleClientBoundShaderEffect(String modid, String location){
        Minecraft mc = Minecraft.getInstance();
        GameRenderer render = mc.gameRenderer;
        LocalPlayer clientPlayer = mc.player;
        if(clientPlayer != null && ClientConfig.HOLLOW_CRYSTAL_FLASH.get()) {
            render.loadEffect(ResourceLocation.fromNamespaceAndPath(modid,location));
        }
    }

    public static void removeClientBoundShaderEffect(){
        Minecraft mc = Minecraft.getInstance();
        GameRenderer render = mc.gameRenderer;
        LocalPlayer clientPlayer = mc.player;
        if(clientPlayer != null && ClientConfig.HOLLOW_CRYSTAL_FLASH.get()) {
            render.shutdownEffect();
        }
    }

    public static void doParticleBurst(double xPos, double yPos, double zPos, double xRot, double yRot){
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null && world.isClientSide()) {
            double radius = 0.05;
            double angleIncrement = 1.0 * Math.toRadians(0.5 / radius);
            float speedFactor = 0.1f;

            for (double angle = 0; angle < Math.PI * 2; angle += angleIncrement) {
                double xOffset = Math.cos(angle) * radius;
                double yOffset = Math.sin(angle) * radius;
                double cosPsi = Math.cos(Math.toRadians(yRot));
                double sinPsi = Math.sin(Math.toRadians(yRot));
                double cosTheta = Math.cos(Math.toRadians(xRot));
                double sinTheta = Math.sin(Math.toRadians(xRot));
                Vec3 origin = new Vec3(xOffset * cosPsi - yOffset * sinTheta * sinPsi, yOffset * cosTheta, xOffset * sinPsi + yOffset * sinTheta * cosPsi).normalize();
                Vec3 spots = new Vec3(xPos,yPos,zPos).add(origin);

                world.addParticle(ParticleTypes.END_ROD,
                        spots.x, spots.y, spots.z,
                        origin.x * speedFactor, origin.y * speedFactor, origin.z * speedFactor);
            }
        }
    }
}
