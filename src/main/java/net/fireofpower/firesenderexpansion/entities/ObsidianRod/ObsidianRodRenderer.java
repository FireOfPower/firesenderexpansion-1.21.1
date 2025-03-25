package net.fireofpower.firesenderexpansion.entities.ObsidianRod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.HollowCrystal.HollowCrystal;
import net.fireofpower.firesenderexpansion.entities.HollowCrystal.HollowCrystalModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ObsidianRodRenderer extends GeoEntityRenderer<ObsidianRod> {
    public ObsidianRodRenderer(EntityRendererProvider.Context context) {
        super(context, new ObsidianRodModel(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "obsidian_rod")));
        this.shadowRadius = 0.5f;
    }

    @Override
    public ResourceLocation getTextureLocation(ObsidianRod obsidianRod) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "textures/entity/obsidian_rod.png");
    }

    @Override
    public void preRender(PoseStack poseStack, ObsidianRod animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        Vec3 motion = animatable.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
