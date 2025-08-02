package net.fireofpower.firesenderexpansion.items.weapons.void_staff_holder;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class VoidStaffHolderModel extends DefaultedItemGeoModel<VoidStaffHolder> {
    public VoidStaffHolderModel() {
        super(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, ""));
    }

    @Override
    public ResourceLocation getModelResource(VoidStaffHolder animatable) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "geo/item/void_staff_holder.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(VoidStaffHolder animatable) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "textures/item/void_staff_holder.png");
    }

    @Override
    public ResourceLocation getAnimationResource(VoidStaffHolder animatable) {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "animations/item/void_staff.animation.json");
    }
}
