package net.fireofpower.firesenderexpansion.entities.spells.BinaryStars.NovaStar;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.spells.BinaryStars.BinaryStarEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NovaStarModel extends GeoModel<BinaryStarEntity> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "textures/entity/nova_star.png");
    public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "geo/entity/nova_star.geo.json");
    public static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "animations/entity/binary_stars.animation.json");

    @Override
    public ResourceLocation getModelResource(BinaryStarEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(BinaryStarEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(BinaryStarEntity animatable) {
        return ANIMATIONS;
    }
}
