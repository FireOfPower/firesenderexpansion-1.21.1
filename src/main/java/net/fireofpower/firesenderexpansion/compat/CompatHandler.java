package net.fireofpower.firesenderexpansion.compat;

import net.fireofpower.firesenderexpansion.compat.DiscerningTheEldritch.DTEHandler;
import net.neoforged.fml.ModList;

import java.util.Map;

public class CompatHandler {
    private static final Map<String, Runnable> MOD_MAP = Map.of(
//            "tetra", () -> TetraProxy.PROXY = new TetraActualImpl(),
//            "apotheosis", ApotheosisHandler::init
            "discerning_the_eldritch", DTEHandler::init
    );

    public static void init() {
        MOD_MAP.forEach((modid, supplier) -> {
            if (ModList.get().isLoaded(modid)) {
                supplier.run();
            }
        });
    }
}
