package net.fireofpower.firesenderexpansion.util;

import net.fireofpower.firesenderexpansion.spells.FEESpellAnimations;
import net.minecraft.client.Minecraft;

import java.util.UUID;

import static io.redspace.ironsspellbooks.player.ClientSpellCastHelper.animatePlayerStart;

public class ClientSpellCastHelper {
    public static void handleClientBoundOnFinalCastDone(UUID castingEntityId, String spellId) {
        var player = Minecraft.getInstance().player.level().getPlayerByUUID(castingEntityId);
        animatePlayerStart(player, FEESpellAnimations.ANIMATION_HOLLOW_CRYSTAL_CAST.getForPlayer().get());
    }
}
