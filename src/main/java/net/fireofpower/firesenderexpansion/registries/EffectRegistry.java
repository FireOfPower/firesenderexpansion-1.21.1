package net.fireofpower.firesenderexpansion.registries;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.effects.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EffectRegistry {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, FiresEnderExpansion.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> ASPECT_OF_THE_SHULKER_EFFECT = MOB_EFFECTS.register("aspect_of_the_shulker_effect", () -> new AspectOfTheShulkerEffect());
    public static final DeferredHolder<MobEffect, MobEffect> HOLLOW_CRYSTAL_EFFECT = MOB_EFFECTS.register("hollow_crystal_effect", () -> new HollowCrystalEffect());
    public static final DeferredHolder<MobEffect, MobEffect> ANCHORED_EFFECT = MOB_EFFECTS.register("anchored_effect", () -> new AnchoredEffect());
    public static final DeferredHolder<MobEffect, MobEffect> INFINITE_VOID_EFFECT = MOB_EFFECTS.register("infinite_void_effect", () -> new InfiniteVoidEffect());
    public static final DeferredHolder<MobEffect, MobEffect> ASCENDED_CASTER_EFFECT = MOB_EFFECTS.register("ascended_caster_effect", () -> new AscendedCasterEffect());

    public static void register(IEventBus eventBus)
    {
        MOB_EFFECTS.register(eventBus);
    }
}
