package net.fireofpower.firesenderexpansion.entities.HollowCrystal;

// Made with Blockbench 4.12.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HollowCrystalModel extends GeoModel<HollowCrystal> {

    @Override
    public ResourceLocation getModelResource(HollowCrystal hollowCrystal) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "geo/hollow_crystal_stuff_geckolib.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HollowCrystal hollowCrystal) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "textures/entities/hollow_crystal/hollow_crystal_model.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HollowCrystal hollowCrystal) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "animations/entity/hollow_crystal/geckolib_hollow_crystal_animations.json");
    }
}
