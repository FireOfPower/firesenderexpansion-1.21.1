package net.fireofpower.firesenderexpansion;

import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.render.PocketDimensionEffects;
import io.redspace.ironsspellbooks.render.SpellBookCurioRenderer;
import net.fireofpower.firesenderexpansion.entities.mobs.PorphyromancerRenderer;
import net.fireofpower.firesenderexpansion.entities.spells.BinaryStars.BinaryStarRenderer;
import net.fireofpower.firesenderexpansion.entities.spells.BinaryStars.NovaStar.NovaStarEntity;
import net.fireofpower.firesenderexpansion.entities.spells.BinaryStars.NovaStar.NovaStarModel;
import net.fireofpower.firesenderexpansion.entities.spells.BinaryStars.ObsidianStar.ObsidianStarModel;
import net.fireofpower.firesenderexpansion.entities.spells.GateOfEnder.GatePortalRenderer;
import net.fireofpower.firesenderexpansion.entities.spells.HollowCrystal.HollowCrystalRenderer;
import net.fireofpower.firesenderexpansion.entities.spells.InfiniteVoid.InfiniteVoidRenderer;
import net.fireofpower.firesenderexpansion.entities.spells.ObsidianRod.ObsidianRodRenderer;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.claymore.UnstableSummonedClaymoreModel;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.rapier.UnstableSummonedRapierModel;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.sword.UnstableSummonedSwordModel;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.UnstableSummonedSwordRenderer;
import net.fireofpower.firesenderexpansion.items.armor.FEEArmorMaterials;
import net.fireofpower.firesenderexpansion.registries.*;
import net.fireofpower.firesenderexpansion.setup.ModSetup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import java.util.function.Supplier;

@Mod(FiresEnderExpansion.MODID)
public class FiresEnderExpansion
{
    public static final String MODID = "firesenderexpansion";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public FiresEnderExpansion(IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ModSetup::init);
        CREATIVE_MODE_TABS.register(modEventBus);

        SpellRegistries.register(modEventBus);
        EffectRegistry.register(modEventBus);
        EntityRegistry.register(modEventBus);
        ItemRegistry.register(modEventBus);
        FEEArmorMaterials.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static final Supplier<CreativeModeTab> FEE_TAB = CREATIVE_MODE_TABS.register("example", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MODID + ".creative_tab"))
            .icon(() -> new ItemStack(ItemRegistry.STABILIZED_CORE_OF_ENDER.get()))
            .displayItems((params, output) -> {
                output.accept(ItemRegistry.VOID_STAFF_HOLDER.get());
                output.accept(ItemRegistry.VOID_STAFF.get());
                output.accept(ItemRegistry.ENDCHIRIDION.get());
                output.accept(ItemRegistry.PORPHYROMANCER_SPAWN_EGG.get());
                output.accept(ItemRegistry.CORE_OF_ENDER.get());
                output.accept(ItemRegistry.STABILIZED_CORE_OF_ENDER.get());
                output.accept(ItemRegistry.INFUSED_OBSIDIAN_FRAGMENTS.get());
                output.accept(ItemRegistry.END_LORD_HELMET.get());
                output.accept(ItemRegistry.END_LORD_CHESTPLATE.get());
                output.accept(ItemRegistry.END_LORD_LEGGINGS.get());
                output.accept(ItemRegistry.END_LORD_BOOTS.get());
                output.accept(ItemRegistry.ENDER_TREASURY_KEY.get());
                output.accept(ItemRegistry.ANCHORING_RING.get());
            })
            .build()
    );

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            event.enqueueWork(() -> {
                ItemRegistry.getItems().stream().filter(item -> item.get() instanceof SpellBook).forEach((item) -> CuriosRendererRegistry.register(item.get(), SpellBookCurioRenderer::new));
            });
        }

        @SubscribeEvent
        public static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
            event.register(FiresEnderExpansion.id("pocket_dimension"), new PocketDimensionEffects());
        }

        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EntityRegistry.HOLLOW_CRYSTAL.get(), HollowCrystalRenderer::new);
            event.registerEntityRenderer(EntityRegistry.OBSIDIAN_ROD.get(), ObsidianRodRenderer::new);
            event.registerEntityRenderer(EntityRegistry.INFINITE_VOID.get(), InfiniteVoidRenderer::new);
            event.registerEntityRenderer(EntityRegistry.PORPHYROMANCER.get(), PorphyromancerRenderer::new);
            event.registerEntityRenderer(EntityRegistry.GATE_PORTAL.get(), GatePortalRenderer::new);
            event.registerEntityRenderer(EntityRegistry.TELEPORT_AREA.get(), NoopRenderer::new);
            event.registerEntityRenderer(EntityRegistry.UNSTABLE_SUMMONED_SWORD.get(), (e) -> new UnstableSummonedSwordRenderer(e, UnstableSummonedSwordModel::new));
            event.registerEntityRenderer(EntityRegistry.UNSTABLE_SUMMONED_RAPIER.get(), (e) -> new UnstableSummonedSwordRenderer(e, UnstableSummonedRapierModel::new));
            event.registerEntityRenderer(EntityRegistry.UNSTABLE_SUMMONED_CLAYMORE.get(), (e) -> new UnstableSummonedSwordRenderer(e, UnstableSummonedClaymoreModel::new));
            event.registerEntityRenderer(EntityRegistry.NOVA_STAR.get(), (e) -> new BinaryStarRenderer(e, NovaStarModel::new));
            event.registerEntityRenderer(EntityRegistry.OBSIDIAN_STAR.get(), (e) -> new BinaryStarRenderer(e, ObsidianStarModel::new));
        }

        @SubscribeEvent
        public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        }

    }
    public static ResourceLocation id(@NotNull String path)
    {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, path);
    }
}
