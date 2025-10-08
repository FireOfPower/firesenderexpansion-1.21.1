package net.fireofpower.firesenderexpansion.entities.mobs.void_wyrm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.mobs.porphyromancer.PorphyromancerModel;
import net.fireofpower.firesenderexpansion.entities.spells.GateOfEnder.GatePortal;
import net.fireofpower.firesenderexpansion.entities.spells.GateOfEnder.GatePortalModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class VoidWyrmRenderer extends AbstractSpellCastingMobRenderer {
    public VoidWyrmRenderer(EntityRendererProvider.Context context) {
        super(context, new VoidWyrmModel());
    }

//    @Override
//    public @Nullable RenderType getRenderType(VoidWyrm animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
//        return RenderHelper.CustomerRenderType.magic(texture);
//    }
}
