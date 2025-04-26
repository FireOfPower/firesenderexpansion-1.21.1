package net.fireofpower.firesenderexpansion;

import io.redspace.ironsspellbooks.particle.UnstableEnderParticle;
import io.redspace.ironsspellbooks.registries.CreativeTabRegistry;
import net.fireofpower.firesenderexpansion.entities.HollowCrystal.HollowCrystalRenderer;
import net.fireofpower.firesenderexpansion.entities.InfiniteVoid.InfiniteVoidRenderer;
import net.fireofpower.firesenderexpansion.entities.ObsidianRod.ObsidianRodRenderer;
import net.fireofpower.firesenderexpansion.registries.*;
import net.fireofpower.firesenderexpansion.setup.ModSetup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
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
        PotionEffectRegistry.register(modEventBus);
        EntityRegistry.register(modEventBus);
        ItemRegistry.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }

        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EntityRegistry.HOLLOW_CRYSTAL.get(), HollowCrystalRenderer::new);
            event.registerEntityRenderer(EntityRegistry.OBSIDIAN_ROD.get(), ObsidianRodRenderer::new);
            event.registerEntityRenderer(EntityRegistry.INFINITE_VOID.get(), InfiniteVoidRenderer::new);
        }

        @SubscribeEvent
        public static void buildContents(BuildCreativeModeTabContentsEvent event) {
            // Is this the tab we want to add to?
            if (event.getTabKey() == CreativeModeTabs.COMBAT) {
                event.accept(ItemRegistry.VOID_STAFF.get());
            }
        }
    }
}
