package net.fireofpower.firesenderexpansion;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = FiresEnderExpansion.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // a list of strings that are treated as resource locations for items
    private static final ModConfigSpec.BooleanValue DISCLAIMER_MSG = BUILDER
            .comment("If you want to modify stats relating to this mod's spells, check the ISS server config.")
            .define("understand", true);
    private static final ModConfigSpec.BooleanValue ALLOW_INFUSED_OBSIDIAN_FRAGMENTS = BUILDER
            .comment("Should Infused Obsidian Fragments be obtainable through the in-world method? Default is true")
            .define("fragments_optainable", true);
    private static final ModConfigSpec.BooleanValue ALLOW_FILLING_VOID_STAFF_WITH_BLACK_HOLE = BUILDER
            .comment("Should the Ornate Empty Staff be able to be filled with a black hole? Default is true")
            .define("tbate_black_hole_obtainable",true);
    private static final ModConfigSpec.BooleanValue ALLOW_CRAFTING_CRYSTAL_HEART = BUILDER
            .comment("Should the Crystal heart be craftable with the in-world method? Default is true")
            .define("crystal_heart_obtainable",true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean placeholder;
    public static boolean allowInfusedObsidianFragments;
    public static boolean allowFillingVoidStaffWithBlackHole;
    public static boolean allowCraftingCrystalHeart;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        placeholder = DISCLAIMER_MSG.get();
        allowInfusedObsidianFragments = ALLOW_INFUSED_OBSIDIAN_FRAGMENTS.get();
        allowFillingVoidStaffWithBlackHole = ALLOW_FILLING_VOID_STAFF_WITH_BLACK_HOLE.get();
        allowCraftingCrystalHeart = ALLOW_CRAFTING_CRYSTAL_HEART.get();
    }
}
