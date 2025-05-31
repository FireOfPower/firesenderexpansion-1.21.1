package net.fireofpower.firesenderexpansion.entities.mobs;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.spells.HollowCrystal.HollowCrystal;
import net.fireofpower.firesenderexpansion.entities.spells.HollowCrystal.HollowCrystalModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PorphyromancerRenderer extends GeoEntityRenderer<Porphyromancer> {
    public PorphyromancerRenderer(EntityRendererProvider.Context context) {
        super(context, new PorphyromancerModel(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "porphyromancer")));
        this.shadowRadius = 0.5f;
    }

    @Override
    public ResourceLocation getTextureLocation(Porphyromancer porphyromancer) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "textures/entity/porphyromancer.png");
    }
}
