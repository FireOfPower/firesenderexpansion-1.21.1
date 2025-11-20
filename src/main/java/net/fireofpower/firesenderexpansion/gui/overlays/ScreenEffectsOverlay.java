package net.fireofpower.firesenderexpansion.gui.overlays;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import java.util.Timer;
import java.util.TimerTask;

public class ScreenEffectsOverlay implements LayeredDraw.Layer {
    public static final ScreenEffectsOverlay instance = new ScreenEffectsOverlay();

    public final static ResourceLocation VIGINETTE_TEXTURE = ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/gui/overlays/vignette.png");

    public void render(GuiGraphics guiHelper, DeltaTracker deltaTracker) {
        if (Minecraft.getInstance().options.hideGui || Minecraft.getInstance().player.isSpectator()) {
            return;
        }
        var screenWidth = guiHelper.guiWidth();
        var screenHeight = guiHelper.guiHeight();

//        if(!FMLLoader.isProduction()){
//            guiHelper.drawString(Minecraft.getInstance().font, String.format("ice:   %s", Minecraft.getInstance().player.getAttributeValue(AttributeRegistry.ICE_SPELL_POWER)), 10, 10, 0xFFFFFF);
//            guiHelper.drawString(Minecraft.getInstance().font, String.format("blood: %s", Minecraft.getInstance().player.getAttributeValue(AttributeRegistry.BLOOD_SPELL_POWER)), 10, 20, 0xFFFFFF);
//        }

        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        if (player.hasEffect(EffectRegistry.HOLLOW_CRYSTAL_EFFECT)) {
            renderOverlayAdditive(guiHelper, VIGINETTE_TEXTURE, 0.25f * player.getEffect(EffectRegistry.HOLLOW_CRYSTAL_EFFECT).getAmplifier(), 0, 0.25f * player.getEffect(EffectRegistry.HOLLOW_CRYSTAL_EFFECT).getAmplifier(), .25f, screenWidth, screenHeight);
        }
        if (player.hasEffect(EffectRegistry.LOCKED_CAMERA_EFFECT)) {
            MobEffectInstance inst = player.getEffect(EffectRegistry.LOCKED_CAMERA_EFFECT);
            renderOverlayAdditive(guiHelper, VIGINETTE_TEXTURE, 0.35f * inst.getAmplifier(), 0, 0.35f * inst.getAmplifier(), .25f, (int)(screenWidth / (inst.getDuration() / 10f - 1)), (int)(screenHeight / (inst.getDuration() / 10f - 1)));
        }
    }

    private static void renderOverlayAdditive(GuiGraphics gui, ResourceLocation texture, float r, float g, float b, float a, int screenWidth, int screenHeight) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE
        );
        gui.setColor(r, g, b, a);
        gui.blit(texture, (gui.guiWidth() - screenWidth)/2, (gui.guiHeight()-screenHeight)/2, -90, 0.0F, 0.0F, screenWidth, screenHeight, screenWidth, screenHeight);
        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    private static void renderOverlay(GuiGraphics gui, ResourceLocation texture, float r, float g, float b, float a, int screenWidth, int screenHeight) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        gui.setColor(r, g, b, a);
        gui.blit(texture, 0, 0, -90, 0.0F, 0.0F, screenWidth, screenHeight, screenWidth, screenHeight);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }


}
