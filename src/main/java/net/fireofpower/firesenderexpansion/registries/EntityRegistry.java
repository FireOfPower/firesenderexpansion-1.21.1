package net.fireofpower.firesenderexpansion.registries;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.HollowCrystal.HollowCrystal;
import net.fireofpower.firesenderexpansion.entities.MagicShulkerBullet;
import net.fireofpower.firesenderexpansion.entities.ObsidianRod.ObsidianRod;
import net.fireofpower.firesenderexpansion.entities.ObsidianRod.ObsidianRodModel;
import net.fireofpower.firesenderexpansion.entities.TeleportRend;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, FiresEnderExpansion.MODID);

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }

    public static final DeferredHolder<EntityType<?>, EntityType<TeleportRend>> TELEPORT_REND =
            ENTITIES.register("teleport_rend", () -> EntityType.Builder.<TeleportRend>of(TeleportRend::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(64)
                    .build((ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "teleport_rend")).toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicShulkerBullet>> MAGIC_SHULKER_BULLET =
            ENTITIES.register("magic_shulker_bullet", () -> EntityType.Builder.<MagicShulkerBullet>of(MagicShulkerBullet::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(64)
                    .build((ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "magic_shulker_bullet")).toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<HollowCrystal>> HOLLOW_CRYSTAL =
            ENTITIES.register("hollow_crystal", () -> EntityType.Builder.<HollowCrystal>of(HollowCrystal::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(64)
                    .build((ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "hollow_crystal")).toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<ObsidianRod>> OBSIDIAN_ROD =
            ENTITIES.register("obsidian_rod", () -> EntityType.Builder.<ObsidianRod>of(ObsidianRod::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(64)
                    .build((ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "obsidian_rod")).toString()));

}