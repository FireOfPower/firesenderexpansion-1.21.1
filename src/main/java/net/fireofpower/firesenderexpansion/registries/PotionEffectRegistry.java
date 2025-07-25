package net.fireofpower.firesenderexpansion.registries;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.effects.*;
import net.fireofpower.firesenderexpansion.spells.PartialTeleportSpell;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PotionEffectRegistry {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, FiresEnderExpansion.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> ASPECT_OF_THE_SHULKER_POTION_EFFECT = MOB_EFFECTS.register("aspect_of_the_shulker_potion_effect", () -> new AspectOfTheShulkerPotionEffect());
    public static final DeferredHolder<MobEffect, MobEffect> HOLLOW_CRYSTAL_POTION_EFFECT = MOB_EFFECTS.register("hollow_crystal_potion_effect", () -> new HollowCrystalPotionEffect());
    public static final DeferredHolder<MobEffect, MobEffect> ANCHORED_POTION_EFFECT = MOB_EFFECTS.register("anchored_potion_effect", () -> new AnchoredPotionEffect());
    public static final DeferredHolder<MobEffect, MobEffect> INFINITE_VOID_POTION_EFFECT = MOB_EFFECTS.register("infinite_void_potion_effect", () -> new InfiniteVoidPotionEffect());
    public static final DeferredHolder<MobEffect, MobEffect> ASCENDED_CASTER_POTION_EFFECT = MOB_EFFECTS.register("ascended_caster_potion_effect", () -> new AscendedCasterPotionEffect());
    public static final DeferredHolder<MobEffect, MobEffect> VOID_BURN_POTION_EFFECT = MOB_EFFECTS.register("void_burn_potion_effect", () -> new VoidBurnPotionEffect());

    public static void register(IEventBus eventBus)
    {
        MOB_EFFECTS.register(eventBus);
    }
}
