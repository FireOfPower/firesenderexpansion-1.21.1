package net.fireofpower.firesenderexpansion.setup;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.mobs.porphyromancer.Porphyromancer;
import net.fireofpower.firesenderexpansion.entities.mobs.void_wyrm.VoidWyrm;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = FiresEnderExpansion.MODID)
public class CommonSetup {
    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(EntityRegistry.PORPHYROMANCER.get(), Porphyromancer.prepareAttributes().build());
        event.put(EntityRegistry.VOID_WYRM.get(), VoidWyrm.prepareAttributes().build());
    }
}
