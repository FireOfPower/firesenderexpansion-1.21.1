package net.fireofpower.firesenderexpansion.network;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.util.ClientSpellCastHelper;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public class PlayShaderEffectPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayShaderEffectPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "play_shader_effect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayShaderEffectPacket> STREAM_CODEC = CustomPacketPayload.codec(PlayShaderEffectPacket::write, PlayShaderEffectPacket::new);


    public PlayShaderEffectPacket() {
    }

    public PlayShaderEffectPacket(FriendlyByteBuf buf) {
    }


    public void write(FriendlyByteBuf buf) {
    }

    public static void handle(PlayShaderEffectPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientSpellCastHelper.handleClientBoundShaderEffect();
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
