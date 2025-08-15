package net.fireofpower.firesenderexpansion.network;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.util.ClientSpellCastHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public class RemoveShaderEffectPacket implements CustomPacketPayload {
    public static final Type<RemoveShaderEffectPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "remove_shader_effect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RemoveShaderEffectPacket> STREAM_CODEC = CustomPacketPayload.codec(RemoveShaderEffectPacket::write, RemoveShaderEffectPacket::new);


    public RemoveShaderEffectPacket() {
    }

    public RemoveShaderEffectPacket(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
    }


    public void write(FriendlyByteBuf buf) {
        
    }

    public static void handle(RemoveShaderEffectPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientSpellCastHelper.removeClientBoundShaderEffect();
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
