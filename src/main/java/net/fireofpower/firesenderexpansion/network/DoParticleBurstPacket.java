package net.fireofpower.firesenderexpansion.network;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class DoParticleBurstPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<DoParticleBurstPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "do_particle_burst"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DoParticleBurstPacket> STREAM_CODEC = CustomPacketPayload.codec(DoParticleBurstPacket::write, DoParticleBurstPacket::new);
    private final double xPos;
    private final double yPos;
    private final double zPos;
    private final double xRot;
    private final double yRot;

    public DoParticleBurstPacket(double xPos, double yPos, double zPos, double xRot, double yRot) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
        this.xRot = xRot;
        this.yRot = yRot;
    }

    public DoParticleBurstPacket(FriendlyByteBuf buf) {
        this.xPos = buf.readDouble();
        this.yPos = buf.readDouble();
        this.zPos = buf.readDouble();
        this.xRot = buf.readDouble();
        this.yRot = buf.readDouble();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeDouble(xPos);
        buf.writeDouble(yPos);
        buf.writeDouble(zPos);
        buf.writeDouble(xRot);
        buf.writeDouble(yRot);
    }

    public static void handle(DoParticleBurstPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            // Server to Client
            PayloadHandler.doParticleBurst(packet.xPos, packet.yPos, packet.zPos, packet.xRot, packet.yRot);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
