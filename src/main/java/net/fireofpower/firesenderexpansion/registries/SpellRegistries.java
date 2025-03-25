package net.fireofpower.firesenderexpansion.registries;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.spells.eldritch.AbyssalShroudSpell;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.spells.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SpellRegistries {
    public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, FiresEnderExpansion.MODID);

    public static void register(IEventBus eventBus) {
        SPELLS.register(eventBus);
    }

    public static Supplier<AbstractSpell> registerSpell(AbstractSpell spell) {
        return SPELLS.register(spell.getSpellName(), () -> spell);
    }

    public static final Supplier<AbstractSpell> PARTIAL_TELEPORT = registerSpell(new PartialTeleportSpell());
    public static final Supplier<AbstractSpell> ASPECT_OF_THE_SHULKER = registerSpell(new AspectOfTheShulkerSpell());
    public static final Supplier<AbstractSpell> HOLLOW_CRYSTAL = registerSpell(new HollowCrystalSpell());
    public static final Supplier<AbstractSpell> DIMENSIONAL_TRAVELLER = registerSpell(new DimensionalTravellerSpell());
    public static final Supplier<AbstractSpell> OBSIDIAN_ROD = registerSpell(new ObsidianRodSpell());
}
