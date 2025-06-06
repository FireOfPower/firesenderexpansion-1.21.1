package net.fireofpower.firesenderexpansion.setup;

import net.fireofpower.firesenderexpansion.compat.CompatHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ModSetup {
    public static void setup() {
        IEventBus bus = NeoForge.EVENT_BUS;

        //SPELLBOOKS
        //bus.addGenericListener(ItemStack.class, SpellBookDataEvents::onAttachCapabilities);
        //bus.addListener(SpellBookDataEvents::onRegisterCapabilities);

        //SCROLLS
        //bus.addListener(ScrollDataEvents::onRegisterCapabilities);
        //bus.addGenericListener(ItemStack.class, ScrollDataEvents::onAttachCapabilitiesItemStack);

    }

    public static void init(FMLCommonSetupEvent event) {

        CompatHandler.init();

    }
}
