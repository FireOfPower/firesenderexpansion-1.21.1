package net.fireofpower.firesenderexpansion.entities.spells.MagicEndCrystal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class MagicEndCrystalRenderer extends EntityRenderer<MagicEndCrystal> {
    //this is just from the Minecraft end crystal code I needed to make it use something other than a BlockPos...
    private static final ResourceLocation END_CRYSTAL_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/end_crystal/end_crystal.png");
    public static final ResourceLocation CRYSTAL_BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/end_crystal/end_crystal_beam.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(END_CRYSTAL_LOCATION);
    private static final float SIN_45 = (float)Math.sin(Math.PI / 4);
    private static final String GLASS = "glass";
    private static final String BASE = "base";
    private static final RenderType BEAM = RenderType.entitySmoothCutout(CRYSTAL_BEAM_LOCATION);
    private final ModelPart cube;
    private final ModelPart glass;
    private final ModelPart base;
    private final HashMap<Entity,Entity> cachedOwner = new HashMap<>();

    public MagicEndCrystalRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0F;
        ModelPart modelpart = context.bakeLayer(ModelLayers.END_CRYSTAL);
        this.glass = modelpart.getChild("glass");
        this.cube = modelpart.getChild("cube");
        this.base = modelpart.getChild("base");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("glass", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 16).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public void render(MagicEndCrystal entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float f = getY(entity, partialTicks);
        float f1 = ((float)entity.time + partialTicks) * 3.0F;
        VertexConsumer vertexconsumer = buffer.getBuffer(RENDER_TYPE);
        poseStack.pushPose();
        poseStack.scale(2.0F, 2.0F, 2.0F);
        poseStack.translate(0.0F, -0.5F, 0.0F);
        int i = OverlayTexture.NO_OVERLAY;
        if (entity.showsBottom()) {
            this.base.render(poseStack, vertexconsumer, packedLight, i);
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(f1));
        poseStack.translate(0.0F, 1.5F + f / 2.0F, 0.0F);
        poseStack.mulPose(new Quaternionf().setAngleAxis((float) (Math.PI / 3), SIN_45, 0.0F, SIN_45));
        this.glass.render(poseStack, vertexconsumer, packedLight, i);
        float f2 = 0.875F;
        poseStack.scale(0.875F, 0.875F, 0.875F);
        poseStack.mulPose(new Quaternionf().setAngleAxis((float) (Math.PI / 3), SIN_45, 0.0F, SIN_45));
        poseStack.mulPose(Axis.YP.rotationDegrees(f1));
        this.glass.render(poseStack, vertexconsumer, packedLight, i);
        poseStack.scale(0.875F, 0.875F, 0.875F);
        poseStack.mulPose(new Quaternionf().setAngleAxis((float) (Math.PI / 3), SIN_45, 0.0F, SIN_45));
        poseStack.mulPose(Axis.YP.rotationDegrees(f1));
        this.cube.render(poseStack, vertexconsumer, packedLight, i);
        poseStack.popPose();
        poseStack.popPose();

        if(cachedOwner.get(entity) == null) {
            List<Entity> entityList = entity.level().getEntities(entity, new AABB(entity.position().subtract(32, 32, 32), entity.position().add(32, 32, 32)));
            entityList.forEach(e -> {
                if (entity.getBeamTarget().isPresent() && e.getUUID().equals(entity.getBeamTarget().get())) {
                    cachedOwner.put(entity,e);
                }
            });
        }
        Entity owner = cachedOwner.get(entity);
        if (owner != null && Objects.requireNonNull(entity.getBeamTarget()).isPresent()) {
            poseStack.pushPose();
            float f3 = (float) owner.position().x();
            float f4 = (float) owner.position().y() - owner.getBbHeight()/2;
            float f5 = (float) owner.position().z();
            float f6 = (float) (f3 - entity.getX());
            float f7 = (float) (f4 - entity.getY());
            float f8 = (float) (f5 - entity.getZ());
            poseStack.translate(f6, f7, f8);
            renderCrystalBeams(-f6, -f7 + f, -f8, partialTicks, entity.time, poseStack, buffer, packedLight);
            poseStack.popPose();
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    public static void renderCrystalBeams(float x, float y, float z, float partialTick, int tickCount, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float f = Mth.sqrt(x * x + z * z);
        float f1 = Mth.sqrt(x * x + y * y + z * z);
        poseStack.pushPose();
        poseStack.translate(0.0F, 2.0F, 0.0F);
        poseStack.mulPose(Axis.YP.rotation((float)(-Math.atan2((double)z, (double)x)) - ((float)Math.PI / 2F)));
        poseStack.mulPose(Axis.XP.rotation((float)(-Math.atan2((double)f, (double)y)) - ((float)Math.PI / 2F)));
        VertexConsumer vertexconsumer = bufferSource.getBuffer(BEAM);
        float f2 = 0.0F - ((float)tickCount + partialTick) * 0.01F;
        float f3 = Mth.sqrt(x * x + y * y + z * z) / 32.0F - ((float)tickCount + partialTick) * 0.01F;
        int i = 8;
        float f4 = 0.0F;
        float f5 = 0.75F;
        float f6 = 0.0F;
        PoseStack.Pose posestack$pose = poseStack.last();

        for(int j = 1; j <= 8; ++j) {
            float f7 = Mth.sin((float)j * ((float)Math.PI * 2F) / 8.0F) * 0.75F;
            float f8 = Mth.cos((float)j * ((float)Math.PI * 2F) / 8.0F) * 0.75F;
            float f9 = (float)j / 8.0F;
            vertexconsumer.addVertex(posestack$pose, f4 * 0.2F, f5 * 0.2F, 0.0F).setColor(ChatFormatting.DARK_GRAY.getColor()).setUv(f6, f2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(posestack$pose, 0.0F, -1.0F, 0.0F);
            vertexconsumer.addVertex(posestack$pose, f4, f5, f1).setColor(-1).setUv(f6, f3).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(posestack$pose, 0.0F, -1.0F, 0.0F);
            vertexconsumer.addVertex(posestack$pose, f7, f8, f1).setColor(-1).setUv(f9, f3).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(posestack$pose, 0.0F, -1.0F, 0.0F);
            vertexconsumer.addVertex(posestack$pose, f7 * 0.2F, f8 * 0.2F, 0.0F).setColor(ChatFormatting.LIGHT_PURPLE.getColor()).setUv(f9, f2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(posestack$pose, 0.0F, -1.0F, 0.0F);
            f4 = f7;
            f5 = f8;
            f6 = f9;
        }

        poseStack.popPose();
    }

    public static float getY(MagicEndCrystal endCrystal, float partialTick) {
        float f = (float)endCrystal.time + partialTick;
        float f1 = Mth.sin(f * 0.2F) / 2.0F + 0.5F;
        f1 = (f1 * f1 + f1) * 0.4F;
        return f1 - 1.4F;
    }

    /**
     * Returns the location of an entity's texture.
     */
    public @NotNull ResourceLocation getTextureLocation(@NotNull MagicEndCrystal entity) {
        return END_CRYSTAL_LOCATION;
    }

    public boolean shouldRender(MagicEndCrystal livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return super.shouldRender(livingEntity, camera, camX, camY, camZ) || livingEntity.getBeamTarget() != null;
    }
}
