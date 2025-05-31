package net.fireofpower.firesenderexpansion.entities.mobs;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class PorphyromancerModel extends DefaultedEntityGeoModel<Porphyromancer> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "textures/entity/porphyromancer.png");

    public PorphyromancerModel(ResourceLocation assetSubpath) {
        super(assetSubpath);
    }
}
