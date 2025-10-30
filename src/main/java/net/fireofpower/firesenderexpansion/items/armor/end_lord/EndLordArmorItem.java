package net.fireofpower.firesenderexpansion.items.armor.end_lord;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.fireofpower.firesenderexpansion.items.armor.FEEArmorMaterials;
import net.fireofpower.firesenderexpansion.items.armor.ImbueableFEEGeckolibArmorItem;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EndLordArmorItem extends ImbueableFEEGeckolibArmorItem {
    public EndLordArmorItem(Type type, Properties settings) {
        // Add in your armor tier + additional attributes for your item
        super(FEEArmorMaterials.END_LORD_MATERIAL, type, settings,
                new AttributeContainer(AttributeRegistry.MAX_MANA, 150.0, AttributeModifier.Operation.ADD_VALUE),
                new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, AttributeModifier.Operation.ADD_VALUE),
                new AttributeContainer(AttributeRegistry.ENDER_SPELL_POWER, 0.15, AttributeModifier.Operation.ADD_VALUE)
        );
    }

    // Just supply the armor model here; you don't have to worry about making a new renderer
    // ISS already has a custom renderer used for armor models
    @Override
    @OnlyIn(Dist.CLIENT)
    public GeoArmorRenderer<?> supplyRenderer() {
        return new EndLordArmorRenderer(new EndLordArmor());
    }
}
