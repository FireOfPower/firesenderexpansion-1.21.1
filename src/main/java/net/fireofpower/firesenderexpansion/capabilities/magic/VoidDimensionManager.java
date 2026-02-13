package net.fireofpower.firesenderexpansion.capabilities.magic;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.PocketDimensionManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.spells.InfiniteVoid.InfiniteVoid;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

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
                    System.out.println("Manifest Domain: Void found an issue, sending player to 0,200,0 in the Overworld");
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
        ServerChunkCache cache = ((ServerLevel)(level)).getChunkSource();
        if(!cache.getLevel().isLoaded(new BlockPos(0,0,0))) {
            cache.addRegionTicket(TicketType.FORCED, Utils.getChunkPos(new BlockPos((int) 0, 0, 0)), 20, Utils.getChunkPos(new BlockPos(0, 0, 0)), true);
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

    @SubscribeEvent
    public static void knockbackPreventer(LivingKnockBackEvent event){
        if(event.getEntity().level().dimension().equals(VOID_DIMENSION) && event.getEntity().hasEffect(EffectRegistry.VOIDTORN_EFFECT)){
            event.setCanceled(true);
        }
    }
}
