package net.fireofpower.firesenderexpansion.entities.spells.ChainGrapple;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.render.RenderHelper;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EnderGrappleChainRenderer extends EntityRenderer<EnderGrappleChain> {
    public static final ResourceLocation CHAIN_TEXTURE = IronsSpellbooks.id("textures/entity/ender_chain.png");

    public EnderGrappleChainRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull EnderGrappleChain entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        renderChains(entity, partialTick, poseStack, bufferSource);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EnderGrappleChain enderGrappleChain) {
        return CHAIN_TEXTURE;
    }

    private void renderChains(EnderGrappleChain entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        Entity owner = entity.getOwner();
        Entity victim = entity.getVictim();
        if (owner == null || owner.isRemoved() || victim == null || victim.isRemoved()) {
            return;
        }
        Vec3 renderOrigin = entity.getPosition(partialTick);
        Vec3 anchor = entity.getChainAnchor(owner, partialTick).subtract(renderOrigin); //owner position

        Vec3 headPos = victim.getPosition(partialTick)
                .add(0, victim.getBbHeight() * 0.5, 0)
                .subtract(renderOrigin);
        renderChainBetween(anchor, headPos, poseStack, bufferSource, CHAIN_TEXTURE, 1f);
    }

    public static void renderChainBetween(Vec3 start, Vec3 to, PoseStack poseStack, MultiBufferSource bufferSource) {
        renderChainBetween(start.subtract(0,0.6,0), to, poseStack, bufferSource, CHAIN_TEXTURE, 1f);
    }

    public static void renderChainBetween(Vec3 start, Vec3 to, PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation texture, float warmupPercent) {
        Vec3 delta = to.subtract(start);
        float distance = (float) delta.length();
        if (distance < 1.0E-4f) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(start.x, start.y, start.z);

        var consumer = bufferSource.getBuffer(RenderHelper.CustomerRenderType.magicNoCull(texture));

        Vec3 direction = delta.normalize();
        Quaternionf rotation = new Quaternionf().rotationTo(
                new Vector3f(0, 0, 1),
                new Vector3f((float) direction.x, (float) direction.y, (float) direction.z));
        poseStack.mulPose(rotation);

        PoseStack.Pose pose = poseStack.last();
        Vec3 origin = Vec3.ZERO;
        Vec3 destination = new Vec3(0, 0, distance);
        if (warmupPercent < 1f) {
            origin = destination.scale(1f - warmupPercent);
        }

        SpellRenderingHelper.drawQuad(origin, destination, 1, 0, pose, consumer, 255, 255, 255, 255, 0, distance);
        SpellRenderingHelper.drawQuad(origin, destination, 0, 1, pose, consumer, 255, 255, 255, 255, 0, distance);

        poseStack.popPose();
    }
}
