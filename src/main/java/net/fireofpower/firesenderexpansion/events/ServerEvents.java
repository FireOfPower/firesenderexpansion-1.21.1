package net.fireofpower.firesenderexpansion.events;

import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import io.redspace.ironsspellbooks.api.item.UpgradeData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.registries.PotionEffectRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import java.util.List;
import java.util.Objects;

import static net.fireofpower.firesenderexpansion.FiresEnderExpansion.MODID;

@EventBusSubscriber
public class ServerEvents {
    @SubscribeEvent
    public static void onPlayerCastEvent(SpellPreCastEvent event)
    {
        var entity = event.getEntity();
        boolean hasAnchoredEffect = entity.hasEffect(PotionEffectRegistry.ANCHORED_POTION_EFFECT);
        if (entity instanceof ServerPlayer player && !player.level().isClientSide())
        {
            if (hasAnchoredEffect && (Objects.equals(event.getSpellId(), SpellRegistry.TELEPORT_SPELL.get().getSpellId()) || Objects.equals(event.getSpellId(), SpellRegistry.BLOOD_STEP_SPELL.get().getSpellId()) || Objects.equals(event.getSpellId(), SpellRegistry.FROST_STEP_SPELL.get().getSpellId()) || Objects.equals(event.getSpellId(), SpellRegistry.PORTAL_SPELL.get().getSpellId())))
            {
                event.setCanceled(true);
                // Effect Duration
                int time = player.getEffect(PotionEffectRegistry.ANCHORED_POTION_EFFECT).getDuration();
                // convert duration to time format  using the method convertTicksToTime
                String formattedTime = convertTicksToTime(time);

                if (player instanceof ServerPlayer serverPlayer)
                {
                    // display a message to the player
                    serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.literal(ChatFormatting.BOLD + "Unable to cast teleportation spells for : " + formattedTime)
                            .withStyle(s -> s.withColor(TextColor.fromRgb(0xF35F5F)))));
                    serverPlayer.level().playSound(null , player.getX() , player.getY() , player.getZ() ,
                            SoundEvents.FIRE_EXTINGUISH , SoundSource.PLAYERS , 0.5f , 1f);
                }
            }
        }
    }

    public static String convertTicksToTime(int ticks) {
        // Convert ticks to seconds
        int totalSeconds = ticks / 20;

        // Calculate minutes and seconds
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        // Format the result as mm:ss
        return String.format("%02d:%02d" , minutes , seconds);
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME)
    public class StartupEvents{
//        @SubscribeEvent
//        public static void modifyComponents(ModifyDefaultComponentsEvent event) {
//            // Sets the component on melon seeds
//            event.modify(ItemRegistry.DRAGONSKIN_SPELL_BOOK.get(), builder ->
//                    builder.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(, true))
//            );
//        }

        @SubscribeEvent
        public static void modifyAttributes(ItemAttributeModifierEvent event) {
            if(event.getItemStack().getItem().equals(ItemRegistry.DRAGONSKIN_SPELL_BOOK.get())){
                if(event.getItemStack().getItem() instanceof CurioBaseItem curio){
                    curio.withSpellbookAttributes(new AttributeContainer(AttributeRegistry.ENDER_SPELL_POWER, 0.25f, AttributeModifier.Operation.ADD_VALUE));
                }
            }
        }
    }
}
