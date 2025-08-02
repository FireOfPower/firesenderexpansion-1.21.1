package net.fireofpower.firesenderexpansion.entities.mobs;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class PorphyromancerRenderer extends AbstractSpellCastingMobRenderer {
    public PorphyromancerRenderer(EntityRendererProvider.Context context) {
        super(context, new PorphyromancerModel());
    }
}
