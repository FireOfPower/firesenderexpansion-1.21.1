package net.fireofpower.firesenderexpansion.items.weapons.void_staff;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class VoidStaffModel extends DefaultedItemGeoModel<VoidStaffItem> {
    public VoidStaffModel() {
        super(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, ""));
    }

    @Override
    public ResourceLocation getModelResource(VoidStaffItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "geo/item/void_staff.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(VoidStaffItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "textures/item/void_staff.png");
    }

    @Override
    public ResourceLocation getAnimationResource(VoidStaffItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "animations/item/void_staff.animation.json");
    }
}
