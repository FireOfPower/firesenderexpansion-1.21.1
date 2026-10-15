package net.fireofpower.firesenderexpansion.entities.spells.ChainGrapple;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnderChainHookRenderer extends EntityRenderer<EnderChainHookEntity> {
    public static final ResourceLocation HOOK_TEXTURE = FiresEnderExpansion.id("textures/entity/ender_chain_hook.png");

    public EnderChainHookRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0f;
    }

    @Override
    public ResourceLocation getTextureLocation(EnderChainHookEntity entity) {
        return HOOK_TEXTURE;
    }
}
