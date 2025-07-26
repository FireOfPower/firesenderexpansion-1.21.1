package net.fireofpower.firesenderexpansion.events;

import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import io.redspace.ironsspellbooks.api.events.SpellTeleportEvent;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.PocketDimensionManager;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.fireofpower.firesenderexpansion.effects.InfiniteVoidPotionEffect;
import net.fireofpower.firesenderexpansion.registries.PotionEffectRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.Collection;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static io.redspace.ironsspellbooks.spells.ender.TeleportSpell.particleCloud;
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
            if (hasAnchoredEffect && (Objects.equals(event.getSpellId(), SpellRegistry.PORTAL_SPELL.get().getSpellId())))
            {
                event.setCanceled(true);
                // Effect Duration
                int time = player.getEffect(PotionEffectRegistry.ANCHORED_POTION_EFFECT).getDuration();
                // convert duration to time format  using the method convertTicksToTime
                String formattedTime = convertTicksToTime(time);

                if (player instanceof ServerPlayer serverPlayer)
                {
                    // display a message to the player
                    serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.literal(ChatFormatting.BOLD + "Unable to use teleportation")
                            .withStyle(s -> s.withColor(TextColor.fromRgb(0xF35F5F)))));
                    serverPlayer.level().playSound(null , player.getX() , player.getY() , player.getZ() ,
                            SoundEvents.FIRE_EXTINGUISH , SoundSource.PLAYERS , 0.5f , 1f);
                }
            }
            if(!hasAnchoredEffect && Utils.hasCurio(event.getEntity(), net.fireofpower.firesenderexpansion.registries.ItemRegistry.CORE_OF_ENDER.get())){
                LivingEntity livingEntity = event.getEntity();
                double d0 = livingEntity.getX();
                double d1 = livingEntity.getY();
                double d2 = livingEntity.getZ();
                double maxRadius = (double)12.0F;
                Level level = livingEntity.level();
                RandomSource random = livingEntity.getRandom();

                for(int i = 0; i < 16; ++i) {
                    double minRadius = maxRadius / (double)2.0F;
                    Vec3 vec = new Vec3((double)random.nextInt((int)minRadius, (int)maxRadius), (double)0.0F, (double)0.0F);
                    int degrees = random.nextInt(360);
                    vec = vec.yRot((float)degrees * ((float)Math.PI / 180F));
                    double x = d0 + vec.x;
                    double y = Mth.clamp(livingEntity.getY() + ((double)livingEntity.getRandom().nextInt((int)maxRadius) - maxRadius / (double)2.0F), (double)level.getMinBuildHeight(), (double)(level.getMinBuildHeight() + ((ServerLevel)level).getLogicalHeight() - 1));
                    double z = d2 + vec.z;
                    if (livingEntity.isPassenger()) {
                        livingEntity.stopRiding();
                    }

                    if (livingEntity.randomTeleport(x, y, z, true)) {
                        level.playSound((Player)null, d0, d1, d2, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
                        livingEntity.playSound(SoundEvents.ENDERMAN_TELEPORT, 2.0F, 1.0F);
                        break;
                    }

                    particleCloud(livingEntity.level(),livingEntity.position());

                    if (maxRadius > (double)2.0F) {
                        --maxRadius;
                    }
                }
            }
            if(entity.hasEffect(PotionEffectRegistry.INFINITE_VOID_POTION_EFFECT) && Objects.equals(event.getSpellId(), SpellRegistry.ROOT_SPELL.get().getSpellId())){
                event.setCanceled(true);
                // display a message to the player
                if(entity instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.literal(ChatFormatting.BOLD + "That cannot be casted right now")
                            .withStyle(s -> s.withColor(TextColor.fromRgb(0xF35F5F)))));
                    serverPlayer.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.5f, 1f);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDropsEvent(LivingDropsEvent event){
        Collection<ItemEntity> items = event.getDrops();
        if(event.getEntity().hasEffect(PotionEffectRegistry.INFINITE_VOID_POTION_EFFECT)){
            if(event.getEntity().getEffect(PotionEffectRegistry.INFINITE_VOID_POTION_EFFECT).getEffect().value() instanceof InfiniteVoidPotionEffect voidPotionEffect){
                if(items != null){
                    items.forEach(e -> {
                        if(e != null){
                            ServerChunkCache cache = e.getServer().getLevel(voidPotionEffect.getSavedDimension().dimension()).getChunkSource();
                            cache.addRegionTicket(TicketType.POST_TELEPORT, Utils.getChunkPos(new BlockPos((int)voidPotionEffect.getSavedPosition().x,(int)voidPotionEffect.getSavedPosition().y,(int)voidPotionEffect.getSavedPosition().z)), 3, 238, true);
                            e.changeDimension(new DimensionTransition((ServerLevel) voidPotionEffect.getSavedDimension(), voidPotionEffect.getSavedPosition(), Vec3.ZERO, 0, 0, DimensionTransition.DO_NOTHING));
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    cache.removeRegionTicket(TicketType.POST_TELEPORT, Utils.getChunkPos(new BlockPos((int)voidPotionEffect.getSavedPosition().x,(int)voidPotionEffect.getSavedPosition().y,(int)voidPotionEffect.getSavedPosition().z)), 3, 238, true);
                                }
                            },200);
                        }
                    });
                }
            }
        }
    }

    @SubscribeEvent
    public static void onTeleportSpellCast(SpellTeleportEvent event){
        if(event.getEntity() instanceof LivingEntity entity) {
            boolean hasAnchoredEffect = entity.hasEffect(PotionEffectRegistry.ANCHORED_POTION_EFFECT);
            if (entity instanceof ServerPlayer player && !player.level().isClientSide()) {
                if (hasAnchoredEffect) {
                    event.setCanceled(true);
                    // Effect Duration
                    int time = player.getEffect(PotionEffectRegistry.ANCHORED_POTION_EFFECT).getDuration();
                    // convert duration to time format  using the method convertTicksToTime
                    String formattedTime = convertTicksToTime(time);

                    if (player instanceof ServerPlayer serverPlayer) {
                        // display a message to the player
                        serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.literal(ChatFormatting.BOLD + "Unable to cast teleportation")
                                .withStyle(s -> s.withColor(TextColor.fromRgb(0xF35F5F)))));
                        serverPlayer.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.5f, 1f);
                    }
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
                    curio.withSpellbookAttributes(
                            new AttributeContainer(AttributeRegistry.ENDER_SPELL_POWER, 0.25f, AttributeModifier.Operation.ADD_VALUE),
                            new AttributeContainer(AttributeRegistry.MAX_MANA,200, AttributeModifier.Operation.ADD_VALUE));
                }
            }
        }
    }
}
