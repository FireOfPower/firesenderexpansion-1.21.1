package net.fireofpower.firesenderexpansion.capabilities.magic;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber
public class VoidDimensionManager {
    public static final ResourceKey<Level> VOID_DIMENSION = ResourceKey.create(Registries.DIMENSION, FiresEnderExpansion.id("void_dimension"));
    public static final VoidDimensionManager INSTANCE = new VoidDimensionManager();


    public void tick(Level level){
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!serverLevel.dimension().equals(VoidDimensionManager.VOID_DIMENSION)) {
            return;
        }
        if (serverLevel.getGameTime() % 100 == 0) {
            serverLevel.getAllEntities().forEach(entity -> {
                if(entity instanceof LivingEntity livingEntity && shouldKickOut(livingEntity)){
                    FiresEnderExpansion.LOGGER.debug("Manifest Domain: Void found an issue, sending player to 0,200,0 in the Overworld");
                    livingEntity.changeDimension(new DimensionTransition(entity.level().getServer().getLevel(Level.OVERWORLD), Vec3.ZERO.add(0,200,0), Vec3.ZERO, 0, 0, DimensionTransition.DO_NOTHING));
                }
                if(entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(MobEffectRegistry.ABYSSAL_SHROUD) && !livingEntity.hasEffect(EffectRegistry.ASCENDED_CASTER_EFFECT)){
                    livingEntity.removeEffect(MobEffectRegistry.ABYSSAL_SHROUD);
                    // display a message to the player
                    if(livingEntity instanceof ServerPlayer serverPlayer) {
                        serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("msg.firesenderexpansion.bypass_shroud")
                                .withStyle(s -> s.withColor(TextColor.fromRgb(0xF35F5F)))));
                        serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                                SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.5f, 1f);
                    }
                }
            });
        }
    }

    public boolean shouldKickOut(LivingEntity entity){
        if(entity instanceof ServerPlayer player) {
            if (!player.isCreative() && !player.isSpectator()) {
                return false;
            }
        }
        if(entity.hasEffect(EffectRegistry.INFINITE_VOID_EFFECT)){
            return false;
        }
        return true;
    }

    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Pre event) {
        if (event.getLevel().isClientSide) {
            return;
        }
        VoidDimensionManager.INSTANCE.tick(event.getLevel());
    }
}
