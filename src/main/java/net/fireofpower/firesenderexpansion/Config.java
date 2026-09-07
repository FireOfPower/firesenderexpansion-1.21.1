package net.fireofpower.firesenderexpansion;

import net.neoforged.neoforge.common.ModConfigSpec;

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
    public static final ModConfigSpec.BooleanValue INFINITE_VOID_KEEPINV;

    public static final ModConfigSpec.ConfigValue<Integer> SHULKER_ASPECT_INTERNAL_COOLDOWN;

    static{
        {
            BUILDER.push("General");
            BUILDER.comment("If you want to modify stats relating to this mod's spells, check the ISS server config.");
            DISCLAIMER_MSG = BUILDER.worldRestart().define("understand", true);

            BUILDER.comment("Should Gate of Ender have its sneak-cast variant? Default is false");
            ALLOW_SWORD_HAIL = BUILDER.worldRestart().define("allow_sword_hail",false);

            BUILDER.comment("Should Hollow Crystal break any projectiles it touches? Default is true");
            HOLLOW_CRYSTAL_BREAK_PROJECTILES = BUILDER.worldRestart().define("hollow_crystal_break_projectiles",true);

            BUILDER.comment("What is the internal cooldown (in ticks) for Aspect of the Shulker to trigger? Default is 20");
            SHULKER_ASPECT_INTERNAL_COOLDOWN = BUILDER.worldRestart().define("shulker_aspect_internal_cooldown",20);

            BUILDER.comment("Should Players killed in the Void Dimension have their items return to their respawn position? Default is false");
            INFINITE_VOID_KEEPINV = BUILDER.worldRestart().define("infinite_void_item_return",false);

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
