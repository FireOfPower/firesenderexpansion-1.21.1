package net.fireofpower.firesenderexpansion.entities.spells.GateOfEnder;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.spells.HollowCrystal.HollowCrystal;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GatePortalRenderer extends GeoEntityRenderer<GatePortal> {
    public GatePortalRenderer(EntityRendererProvider.Context context) {
        super(context, new GatePortalModel(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "gate_portal")));
        //addRenderLayer(new AutoGlowingGeoLayer<>(this));

        this.shadowRadius = 0.0f;
    }

    @Override
    public ResourceLocation getTextureLocation(GatePortal gatePortal) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "textures/entity/gate_portal.png");
    }

    @Override
    public @Nullable RenderType getRenderType(GatePortal animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderHelper.CustomerRenderType.magic(texture);
    }

    @Override
    public void preRender(PoseStack poseStack, GatePortal animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if(animatable.getOwner() != null) {
            Vec3 motion = animatable.getOwner().getLookAngle();
            float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
            float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
            poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
            super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        }
    }
}
