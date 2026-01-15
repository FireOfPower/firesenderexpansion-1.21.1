package net.fireofpower.firesenderexpansion.entities.spells.InfiniteVoid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class InfiniteVoidRenderer extends GeoEntityRenderer<InfiniteVoid> {
    public InfiniteVoidRenderer(EntityRendererProvider.Context context) {
        super(context, new InfiniteVoidModel(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "infinite_void")));
        this.shadowRadius = 0.5f;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull InfiniteVoid infiniteVoid) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "textures/entity/infinite_void.png");
    }
}
