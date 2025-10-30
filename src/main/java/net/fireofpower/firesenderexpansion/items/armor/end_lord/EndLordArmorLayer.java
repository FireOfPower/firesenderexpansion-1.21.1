package net.fireofpower.firesenderexpansion.items.armor.end_lord;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class EndLordArmorLayer extends GeoRenderLayer<EndLordArmorItem> {
    private static final ResourceLocation LAYER = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "textures/armor/end_lord_armor_glowmask.png");

    public EndLordArmorLayer(GeoRenderer<EndLordArmorItem> entityRenderer) {
        super(entityRenderer);
    }

    public void render(PoseStack poseStack, EndLordArmorItem animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType glowRenderType = RenderType.eyes(LAYER);


        this.getRenderer()
                .reRender(this.getDefaultBakedModel(animatable),
                        poseStack,
                        bufferSource,
                        animatable,
                        glowRenderType,
                        bufferSource.getBuffer(glowRenderType),
                        partialTick,
                        packedLight,
                        OverlayTexture.NO_OVERLAY,
                        0xe2d1ff
                );
    }
}
