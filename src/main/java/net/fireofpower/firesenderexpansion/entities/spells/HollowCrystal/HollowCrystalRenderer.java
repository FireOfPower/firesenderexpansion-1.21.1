package net.fireofpower.firesenderexpansion.entities.spells.HollowCrystal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class HollowCrystalRenderer extends GeoEntityRenderer<HollowCrystal> {
    public HollowCrystalRenderer(EntityRendererProvider.Context context) {
        super(context, new HollowCrystalModel(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "hollow_crystal")));
        addRenderLayer(new AutoGlowingGeoLayer<>(this));

        this.shadowRadius = 0.5f;
    }

    @Override
    public ResourceLocation getTextureLocation(HollowCrystal hollowCrystal) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "textures/entity/hollow_crystal.png");
    }

    @Override
    public void preRender(PoseStack poseStack, HollowCrystal animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(-Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

    }
}
