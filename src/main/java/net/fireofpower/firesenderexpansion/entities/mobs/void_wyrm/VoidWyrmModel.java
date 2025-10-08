package net.fireofpower.firesenderexpansion.entities.mobs.void_wyrm;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class VoidWyrmModel extends AbstractSpellCastingMobModel {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "textures/entity/void_wyrm.png");

    @Override
    public ResourceLocation getTextureResource(AbstractSpellCastingMob abstractSpellCastingMob) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getModelResource(AbstractSpellCastingMob object) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID,"geo/entity/void_wyrm.geo.json");
    }

    @Override
    public void handleAnimations(AbstractSpellCastingMob entity, long instanceId, AnimationState<AbstractSpellCastingMob> animationState, float partialTick) {
        //super.handleAnimations(entity, instanceId, animationState, partialTick);
    }
}
