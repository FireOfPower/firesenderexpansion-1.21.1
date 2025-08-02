package net.fireofpower.firesenderexpansion.items.weapons.void_staff_holder;

import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class VoidStaffHolderRenderer extends GeoItemRenderer<VoidStaffHolder> {
    public VoidStaffHolderRenderer() {
        super(new VoidStaffHolderModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
