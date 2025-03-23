package net.fireofpower.firesenderexpansion.entities.HollowCrystal;


import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class HollowCrystalModel extends DefaultedEntityGeoModel<HollowCrystal> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "geo/hollow_crystal_stuff_model.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "textures/entity/hollow_crystal/hollow_crystal_model.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "animations/entity/hollow_crystal/hollow_crystal_animations.animations.json");

    public HollowCrystalModel(ResourceLocation assetSubpath) {
        super(assetSubpath);
    }

    @Override
    public ResourceLocation getModelResource(HollowCrystal Object){
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(HollowCrystal object){
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(HollowCrystal object) {
        return this.animations;
    }
}
