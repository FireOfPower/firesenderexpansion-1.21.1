package net.fireofpower.firesenderexpansion.registries;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.TeleportRend;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, FiresEnderExpansion.MODID);

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
     io.redspace.ironsspellbooks.registries.EntityRegistry

    public static final DeferredHolder<EntityType<?>, EntityType<TeleportRend>> TELEPORT_REND =
            ENTITIES.register("teleport_rend", () -> EntityType.Builder.of(TeleportRend::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(64)
                    .build("fires_ender_expansion:teleport_rend"));

}