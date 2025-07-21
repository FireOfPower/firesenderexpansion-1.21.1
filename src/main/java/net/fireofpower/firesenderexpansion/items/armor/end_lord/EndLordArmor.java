package net.fireofpower.firesenderexpansion.items.armor.end_lord;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.items.armor.FEEGeckolibArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class EndLordArmor extends DefaultedEntityGeoModel<EndLordArmorItem> {
    public EndLordArmor(){
        super(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, ""));
    }

    @Override
    public ResourceLocation getModelResource(EndLordArmorItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "geo/armor/end_lord_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EndLordArmorItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "textures/armor/end_lord_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EndLordArmorItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "animations/armor/end_lord_armor.animation.json");
    }
}
