package net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;

import java.util.function.Supplier;

public class UnstableSummonedSwordRenderer extends GeoEntityRenderer<UnstableWeaponEntity> {
    public UnstableSummonedSwordRenderer(EntityRendererProvider.Context renderManager, Supplier<GeoModel<UnstableWeaponEntity>> model) {
        super(renderManager, model.get());
    }

    @Override
    public void preRender(PoseStack poseStack, UnstableWeaponEntity entity, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTicks, int packedLight, int packedOverlay, int colour) {
        Vec3 motion = animatable.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(motion.y, motion.horizontalDistance()) * (double) (180F / (float) Math.PI)));
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 270.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTicks, packedLight, packedOverlay, colour);
    }

    @Override
    public @Nullable RenderType getRenderType(UnstableWeaponEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderHelper.CustomerRenderType.magic(texture);
    }

    @Override
    public Color getRenderColor(UnstableWeaponEntity animatable, float partialTick, int packedLight) {
        return Color.LIGHT_GRAY;
    }
}