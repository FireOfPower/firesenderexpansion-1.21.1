package net.fireofpower.firesenderexpansion.entities.HollowCrystal;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class HollowCrystalRenderer extends EntityRenderer<HollowCrystal> {
    public HollowCrystalRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(HollowCrystal hollowCrystal) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "textures/entities/hollow_crystal/hollow_crystal_model.png");
    }

    @Override
    public void render(HollowCrystal p_entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(p_entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
