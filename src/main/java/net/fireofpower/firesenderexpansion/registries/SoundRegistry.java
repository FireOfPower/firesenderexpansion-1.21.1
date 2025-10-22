package net.fireofpower.firesenderexpansion.registries;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SoundRegistry {
    private static final DeferredRegister<SoundEvent> SOUND_EVENT = DeferredRegister.create(Registries.SOUND_EVENT, FiresEnderExpansion.MODID);

    /***
     * Spell Sounds
     */
    // Binary Stars Spell Cast
    public static DeferredHolder<SoundEvent, SoundEvent> BINARY_STARS_SPELL_CAST = registerSoundEvent("binary_stars_cast");
    // Displacement Cage Spell Cast
    public static DeferredHolder<SoundEvent, SoundEvent> DISPLACEMENT_CAGE_SPELL_CAST = registerSoundEvent("displacement_cage_cast");
    // Hollow Crystal Spell Cast
    public static DeferredHolder<SoundEvent, SoundEvent> HOLLOW_CRYSTAL_SPELL_CAST = registerSoundEvent("hollow_crystal_cast");






    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name)
    {
        return SOUND_EVENT.register(name, () -> SoundEvent.createVariableRangeEvent
                (ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, name)));
    }

    public static void register(IEventBus eventBus)
    {
        SOUND_EVENT.register(eventBus);
    }
}
