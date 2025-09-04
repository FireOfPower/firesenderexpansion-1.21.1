package net.fireofpower.firesenderexpansion.util;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.spells.FEESpellAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static io.redspace.ironsspellbooks.player.ClientSpellCastHelper.animatePlayerStart;

public class ClientSpellCastHelper {
    public static void handleClientBoundOnFinalCastDone(UUID castingEntityId, String spellId) {
        var player = Minecraft.getInstance().player.level().getPlayerByUUID(castingEntityId);
        animatePlayerStart(player, FEESpellAnimations.ANIMATION_HOLLOW_CRYSTAL_CAST.getForPlayer().get());
    }

    public static void handleClientBoundShaderEffect(){
        Minecraft mc = Minecraft.getInstance();
        GameRenderer render = mc.gameRenderer;
        LocalPlayer clientPlayer = mc.player;
        if(clientPlayer != null) {
            render.loadEffect(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID,"shaders/pink_shader.json"));
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
