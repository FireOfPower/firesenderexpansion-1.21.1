package net.fireofpower.firesenderexpansion;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    // a list of strings that are treated as resource locations for items
    public static final ModConfigSpec.BooleanValue DISCLAIMER_MSG;
    public static final ModConfigSpec.BooleanValue ALLOW_INFUSED_OBSIDIAN_FRAGMENTS;
    public static final ModConfigSpec.BooleanValue ALLOW_FILLING_VOID_STAFF_WITH_BLACK_HOLE;
    public static final ModConfigSpec.BooleanValue ALLOW_CRAFTING_CRYSTAL_HEART;
    public static final ModConfigSpec.BooleanValue ALLOW_SWORD_HAIL;
    public static final ModConfigSpec.BooleanValue HOLLOW_CRYSTAL_BREAK_PROJECTILES;

    static{
        {
            BUILDER.push("General");
            BUILDER.comment("If you want to modify stats relating to this mod's spells, check the ISS server config.");
            DISCLAIMER_MSG = BUILDER.worldRestart().define("understand", true);

            BUILDER.comment("Should Gate of Ender have its sneak-cast variant? Default is false");
            ALLOW_SWORD_HAIL = BUILDER.worldRestart().define("allow_sword_hail",false);

            BUILDER.comment("Should Hollow Crystal break any projectiles it touches? Default is true");
            HOLLOW_CRYSTAL_BREAK_PROJECTILES = BUILDER.worldRestart().define("hollow_crystal_break_projectiles",true);

            BUILDER.pop();
        }

        {
            BUILDER.push("Crafting");
            BUILDER.comment("Should Infused Obsidian Fragments be obtainable through the in-world method? Default is true");
            ALLOW_INFUSED_OBSIDIAN_FRAGMENTS = BUILDER.worldRestart().define("fragments_obtainable", true);

            BUILDER.comment("Should the Ornate Empty Staff be able to be filled with a black hole? Default is true");
            ALLOW_FILLING_VOID_STAFF_WITH_BLACK_HOLE = BUILDER.worldRestart().define("tbate_black_hole_obtainable",true);

            BUILDER.comment("Should the Crystal heart be craftable with the in-world method? Default is true");
            ALLOW_CRAFTING_CRYSTAL_HEART = BUILDER.worldRestart().define("crystal_heart_obtainable",true);
            BUILDER.pop();
        }

        SPEC = BUILDER.build();
    }
}
