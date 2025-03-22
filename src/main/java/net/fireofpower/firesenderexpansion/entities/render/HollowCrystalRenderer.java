package net.fireofpower.firesenderexpansion.entities.render;

import net.fireofpower.firesenderexpansion.entities.HollowCrystal;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class HollowCrystalRenderer extends EntityRenderer<HollowCrystal> {
    public HollowCrystalRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(HollowCrystal hollowCrystal) {
        return null;
    }
}
