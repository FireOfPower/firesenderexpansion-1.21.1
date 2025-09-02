package net.fireofpower.firesenderexpansion.entities.spells.BinaryStars;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.spells.ObsidianRod.ObsidianRod;
import net.fireofpower.firesenderexpansion.entities.spells.ObsidianRod.ObsidianRodModel;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.UnstableWeaponEntity;
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

public class BinaryStarRenderer extends GeoEntityRenderer<BinaryStarEntity> {
    public BinaryStarRenderer(EntityRendererProvider.Context renderManager, Supplier<GeoModel<BinaryStarEntity>> model) {
        super(renderManager, model.get());
    }

    @Override
    public void preRender(PoseStack poseStack, BinaryStarEntity entity, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTicks, int packedLight, int packedOverlay, int colour) {
        Vec3 motion = animatable.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(motion.y, motion.horizontalDistance()) * (double) (180F / (float) Math.PI)));
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 270.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTicks, packedLight, packedOverlay, colour);
    }

    @Override
    public Color getRenderColor(BinaryStarEntity animatable, float partialTick, int packedLight) {
        return Color.LIGHT_GRAY;
    }
}
