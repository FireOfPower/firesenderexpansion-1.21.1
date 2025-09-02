package net.fireofpower.firesenderexpansion.registries;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.mobs.Porphyromancer;
import net.fireofpower.firesenderexpansion.entities.spells.GateOfEnder.GatePortal;
import net.fireofpower.firesenderexpansion.entities.spells.HollowCrystal.HollowCrystal;
import net.fireofpower.firesenderexpansion.entities.spells.InfiniteVoid.InfiniteVoid;
import net.fireofpower.firesenderexpansion.entities.spells.MagicShulkerBullet;
import net.fireofpower.firesenderexpansion.entities.spells.ObsidianRod.ObsidianRod;
import net.fireofpower.firesenderexpansion.entities.spells.TeleportAoe;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.claymore.UnstableSummonedClaymoreEntity;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.rapier.UnstableSummonedRapierEntity;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.sword.UnstableSummonedSwordEntity;
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

    public static final DeferredHolder<EntityType<?>, EntityType<MagicShulkerBullet>> MAGIC_SHULKER_BULLET =
            ENTITIES.register("magic_shulker_bullet", () -> EntityType.Builder.<MagicShulkerBullet>of(MagicShulkerBullet::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(64)
                    .build((ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "magic_shulker_bullet")).toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<HollowCrystal>> HOLLOW_CRYSTAL =
            ENTITIES.register("hollow_crystal", () -> EntityType.Builder.<HollowCrystal>of(HollowCrystal::new, MobCategory.MISC)
                    .sized(5f, 5f)
                    .clientTrackingRange(64)
                    .build((ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "hollow_crystal")).toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<ObsidianRod>> OBSIDIAN_ROD =
            ENTITIES.register("obsidian_rod", () -> EntityType.Builder.<ObsidianRod>of(ObsidianRod::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(64)
                    .build((ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "obsidian_rod")).toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<InfiniteVoid>> INFINITE_VOID =
            ENTITIES.register("infinite_void", () -> EntityType.Builder.<InfiniteVoid>of(InfiniteVoid::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(64)
                    .build((ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "infinite_void")).toString()));



    public static final DeferredHolder<EntityType<?>, EntityType<Porphyromancer>> PORPHYROMANCER =
            ENTITIES.register("porphyromancer", () -> EntityType.Builder.of(Porphyromancer::new, MobCategory.MONSTER)
                    .sized(.6f, 1.8f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "porphyromancer").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<UnstableSummonedSwordEntity>> UNSTABLE_SUMMONED_SWORD =
            ENTITIES.register("unstable_summoned_sword", (e) -> EntityType.Builder.<UnstableSummonedSwordEntity>of(UnstableSummonedSwordEntity::new, MobCategory.MISC)
                    .sized(.6f, .6f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "unstable_summoned_sword").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<UnstableSummonedRapierEntity>> UNSTABLE_SUMMONED_RAPIER =
            ENTITIES.register("unstable_summoned_rapier", (e) -> EntityType.Builder.<UnstableSummonedRapierEntity>of(UnstableSummonedRapierEntity::new, MobCategory.MISC)
                    .sized(.6f, .6f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "unstable_summoned_rapier").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<UnstableSummonedClaymoreEntity>> UNSTABLE_SUMMONED_CLAYMORE =
            ENTITIES.register("unstable_summoned_claymore", (e) -> EntityType.Builder.<UnstableSummonedClaymoreEntity>of(UnstableSummonedClaymoreEntity::new, MobCategory.MISC)
                    .sized(.6f, .6f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "unstable_summoned_claymore").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<GatePortal>> GATE_PORTAL =
            ENTITIES.register("gate_portal", (e) -> EntityType.Builder.<GatePortal>of(GatePortal::new, MobCategory.MISC)
                    .sized(.6f, .6f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "gate_portal").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<TeleportAoe>> TELEPORT_AREA =
            ENTITIES.register("teleport_area", () -> EntityType.Builder.<TeleportAoe>of(TeleportAoe::new, MobCategory.MISC)
                    .sized(16f, .8f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "teleport_area").toString()));
}