package net.fireofpower.firesenderexpansion;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue HOLLOW_CRYSTAL_FLASH;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.comment("##############################################################################################");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##   ATTENTION: These are client configs. For gameplay settings, go to the SERVER CONFIGS   ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##############################################################################################");
        BUILDER.comment("");

        BUILDER.push("VFX");
        BUILDER.push("Shaders");
        BUILDER.comment("Should Hollow Crystal have a flash when it releases? May be harmful to photosensitive viewers. Default is false.");
        HOLLOW_CRYSTAL_FLASH = BUILDER.define("hollow_crystal_flash", false);
        BUILDER.pop();
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
