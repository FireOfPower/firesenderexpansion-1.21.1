package net.fireofpower.firesenderexpansion.gui.overlays;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public class CustomHearts {
    public static final ResourceLocation FULL = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "hud/heart/voidtorn_heart");
    public static final ResourceLocation FULL_BLINKING = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "hud/heart/voidtorn_heart_blinking");
    public static final ResourceLocation HALF = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "hud/heart/voidtorn_heart_half");
    public static final ResourceLocation HALF_BLINKING = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "hud/heart/voidtorn_heart_half_blinking");
    public static final ResourceLocation HARDCORE_FULL = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "hud/heart/voidtorn_heart_hardcore");
    public static final ResourceLocation HARDCORE_BLINKING = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "hud/heart/voidtorn_heart_hardcore_blinking");
    public static final ResourceLocation HARDCORE_HALF = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "hud/heart/voidtorn_heart_hardcore_half");
    public static final ResourceLocation HARDCORE_HALF_BLINKING = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "hud/heart/voidtorn_heart_hardcore_half_blinking");
    public static final EnumProxy<Gui.HeartType> HEART_TYPE_ENUM_PROXY = new EnumProxy<>(
            Gui.HeartType.class, FULL, FULL_BLINKING, HALF, HALF_BLINKING, HARDCORE_FULL, HARDCORE_BLINKING, HARDCORE_HALF, HARDCORE_HALF_BLINKING
    );
}
