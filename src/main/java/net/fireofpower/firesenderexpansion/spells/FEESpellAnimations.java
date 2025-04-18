package net.fireofpower.firesenderexpansion.spells;

import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.resources.ResourceLocation;

public class FEESpellAnimations {
    public static ResourceLocation ANIMATION_RESOURCE = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "animation");

    public static final AnimationHolder ANIMATION_HOLLOW_CRYSTAL_CHARGE = new AnimationHolder(FiresEnderExpansion.MODID + ":hollow_crystal_charge", true);
    public static final AnimationHolder ANIMATION_HOLLOW_CRYSTAL_CAST = new AnimationHolder(FiresEnderExpansion.MODID + ":hollow_crystal_cast", true, true);
    public static final AnimationHolder ANIMATION_INFINITE_VOID_CAST = new AnimationHolder(FiresEnderExpansion.MODID + ":infinite_void_cast", true);
}
