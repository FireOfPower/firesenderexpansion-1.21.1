package net.fireofpower.firesenderexpansion.entities.spells.InfiniteVoid;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class InfiniteVoidRenderer extends GeoEntityRenderer<InfiniteVoid> {
    public InfiniteVoidRenderer(EntityRendererProvider.Context context) {
        super(context, new InfiniteVoidModel(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "infinite_void")));
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 0.5f;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull InfiniteVoid infiniteVoid) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "textures/entity/infinite_void.png");
    }
}
