package net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.claymore;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.UnstableWeaponEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class UnstableSummonedClaymoreModel extends GeoModel<UnstableWeaponEntity> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/summoned_weapons/summoned_claymore.png");
    public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/summoned_claymore.geo.json");
    public static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/summoned_weapon_animations.json");

    @Override
    public ResourceLocation getModelResource(UnstableWeaponEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(UnstableWeaponEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(UnstableWeaponEntity animatable) {
        return ANIMATIONS;
    }
}