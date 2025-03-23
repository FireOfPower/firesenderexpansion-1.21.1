package net.fireofpower.firesenderexpansion.entities.HollowCrystal;

// Made with Blockbench 4.12.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class HollowCrystalModel<T extends HollowCrystal> extends HierarchicalModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "hollow_crystal"), "main");
    private final ModelPart Everything;
    private final ModelPart CenterHolder;
    private final ModelPart bone3;
    private final ModelPart bone;
    private final ModelPart bone2;

    public HollowCrystalModel(ModelPart root) {
        this.Everything = root.getChild("Everything");
        this.CenterHolder = this.Everything.getChild("CenterHolder");
        this.bone3 = this.Everything.getChild("bone3");
        this.bone = this.Everything.getChild("bone");
        this.bone2 = this.Everything.getChild("bone2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Everything = partdefinition.addOrReplaceChild("Everything", CubeListBuilder.create(), PartPose.offset(1.0F, 17.0F, -1.0F));

        PartDefinition CenterHolder = Everything.addOrReplaceChild("CenterHolder", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -9.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, 0.0F));

        PartDefinition bone3 = Everything.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition bone = Everything.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(4.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition bone2 = Everything.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Ring4_r1 = bone2.addOrReplaceChild("Ring4_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(4.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, -1.5708F, 1.5708F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(HollowCrystal entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animate(entity.idleAnimationState, HollowCrystalAnimations.ANIMATION_HOLLOW_CRYSTAL_PASSIVE, ageInTicks, 1f);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, int i2) {
        Everything.render(poseStack, vertexConsumer, i, i1, i2);
    }


    public ModelPart root(){
        return Everything;
    }
}
