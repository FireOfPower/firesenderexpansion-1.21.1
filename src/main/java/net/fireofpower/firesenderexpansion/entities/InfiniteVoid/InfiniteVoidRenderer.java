package net.fireofpower.firesenderexpansion.entities.InfiniteVoid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.ObsidianRod.ObsidianRod;
import net.fireofpower.firesenderexpansion.entities.ObsidianRod.ObsidianRodModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class InfiniteVoidRenderer extends GeoEntityRenderer<InfiniteVoid> {
    public InfiniteVoidRenderer(EntityRendererProvider.Context context) {
        super(context, new InfiniteVoidModel(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "infinite_void")));
        this.shadowRadius = 0.5f;
    }

    @Override
    public ResourceLocation getTextureLocation(InfiniteVoid infiniteVoid) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "textures/entity/infinite_void.png");
    }

    @Override
    public void preRender(PoseStack poseStack, InfiniteVoid animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        Vec3 motion = animatable.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
