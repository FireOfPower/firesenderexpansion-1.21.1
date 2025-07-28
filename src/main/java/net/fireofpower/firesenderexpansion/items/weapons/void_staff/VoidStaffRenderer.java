package net.fireofpower.firesenderexpansion.items.weapons.void_staff;

import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class VoidStaffRenderer extends GeoItemRenderer<VoidStaffItem> {
    public VoidStaffRenderer() {
        super(new VoidStaffModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
