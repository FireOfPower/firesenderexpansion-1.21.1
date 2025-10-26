package net.fireofpower.firesenderexpansion.effects;

import io.redspace.ironsspellbooks.api.events.SpellTeleportEvent;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber
public class StridingEffect extends MagicMobEffect {

    public StridingEffect() {
        super(MobEffectCategory.HARMFUL, Utils.rgbToInt(255,0,136));
    }

    @Override
    public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectAdded(pLivingEntity, pAmplifier);
        recordPosition(pLivingEntity);
    }

    @SubscribeEvent
    public static void onEffectCleared(MobEffectEvent.Remove event){
        LivingEntity pLivingEntity = event.getEntity();
        if(recordedPositions.containsKey(pLivingEntity.getUUID())) {
            RecordedPosition data = recordedPositions.get(pLivingEntity.getUUID());
            if(pLivingEntity.level().dimension().equals(data.dimension)){
                NeoForge.EVENT_BUS.post(new SpellTeleportEvent(SpellRegistries.SCINTILLATING_STRIDE.get(), pLivingEntity, pLivingEntity.position().x, pLivingEntity.position().y, pLivingEntity.position().z));
                io.redspace.ironsspellbooks.api.util.Utils.handleSpellTeleport(SpellRegistries.SCINTILLATING_STRIDE.get(), pLivingEntity, data.position);
            }else{
                if(pLivingEntity instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("msg.firesenderexpansion.fail_step")
                            .withStyle(s -> s.withColor(TextColor.fromRgb(0xF35F5F)))));
                }
            }
        }
    }

    //The below code was provided to me by Gametech (creator of T.O.'s Magic and Extras). Thank you Gametech!
    private static final Map<UUID, RecordedPosition> recordedPositions = new HashMap<>();

    public static class RecordedPosition {
        final Vec3 position;
        final ResourceKey<Level> dimension;
        final Vec3 lookDirection;

        RecordedPosition(Vec3 position, ResourceKey<Level> dimension, Vec3 lookDirection) {
            this.position = position;
            this.dimension = dimension;
            this.lookDirection = lookDirection;
        }
    }

    public static void recordPosition(LivingEntity entity) {
        if (!entity.level().isClientSide) {
            Vec3 position = entity.position();
            ResourceKey<Level> dimension = entity.level().dimension();
            Vec3 lookDirection = entity.getLookAngle();
            recordedPositions.put(entity.getUUID(), new RecordedPosition(position, dimension, lookDirection));
        }
    }
}
