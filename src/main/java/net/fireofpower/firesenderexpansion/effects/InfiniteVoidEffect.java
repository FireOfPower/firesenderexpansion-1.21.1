package net.fireofpower.firesenderexpansion.effects;


import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import net.fireofpower.firesenderexpansion.capabilities.magic.VoidDimensionManager;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.fireofpower.firesenderexpansion.util.ModTags;
import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.*;

@EventBusSubscriber
public class InfiniteVoidEffect extends MagicMobEffect implements AntiMagicSusceptible {

    public InfiniteVoidEffect() {
        super(MobEffectCategory.NEUTRAL, Utils.rgbToInt(0,0,0));
    }

    @Override
    public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectAdded(pLivingEntity, pAmplifier);
        if(!pLivingEntity.getType().is(ModTags.INFINITE_VOID_IMMUNE)) {
            recordPosition(pLivingEntity);
            Vec3 newPos = pLivingEntity.position().add(0,500,0);
            if(pLivingEntity.getServer().getLevel(VoidDimensionManager.VOID_DIMENSION) != null){
                pLivingEntity.changeDimension(new DimensionTransition(Objects.requireNonNull(pLivingEntity.getServer()).getLevel(VoidDimensionManager.VOID_DIMENSION),newPos,pLivingEntity.getLookAngle(),pLivingEntity.getXRot(),pLivingEntity.getYRot(),DimensionTransition.DO_NOTHING));
            }
        }
    }

    @Override
    public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectRemoved(pLivingEntity, pAmplifier);
        if(!recordedPositions.containsKey(pLivingEntity.getUUID())){
            System.out.println("Manifest Domain: Void found an issue while saving previous location, returning affected entities to 0,100,0 in the overworld.");
            pLivingEntity.changeDimension(new DimensionTransition(Objects.requireNonNull(pLivingEntity.getServer()).getLevel(Level.OVERWORLD),new Vec3(0, 100,0),pLivingEntity.getLookAngle(),pLivingEntity.getXRot(),pLivingEntity.getYRot(),DimensionTransition.DO_NOTHING));
        }else if(!(pLivingEntity.getType().is(ModTags.INFINITE_VOID_IMMUNE) || pLivingEntity.isDeadOrDying())) {
            ServerChunkCache cache = pLivingEntity.getServer().getLevel(VoidDimensionManager.VOID_DIMENSION).getChunkSource();
            cache.addRegionTicket(TicketType.POST_TELEPORT, Utils.getChunkPos(pLivingEntity.getOnPos()), 10, 239, true);
            pLivingEntity.changeDimension(new DimensionTransition(pLivingEntity.getServer().getLevel(recordedPositions.get(pLivingEntity.getUUID()).dimension), recordedPositions.get(pLivingEntity.getUUID()).position, pLivingEntity.getLookAngle(), pLivingEntity.getXRot(), pLivingEntity.getYRot(), DimensionTransition.DO_NOTHING));
        }
    }

    @Override
    public void onAntiMagic(MagicData magicData) {

    }

    @SubscribeEvent
    public static void onLivingDropsEvent(LivingDropsEvent event){
        Collection<ItemEntity> items = event.getDrops();
        if(event.getEntity().hasEffect(EffectRegistry.INFINITE_VOID_EFFECT)){
            if(event.getEntity().getEffect(EffectRegistry.INFINITE_VOID_EFFECT).getEffect().value() instanceof InfiniteVoidEffect voidPotionEffect){
                if(items != null){
                    items.forEach(e -> {
                        if(e != null){
                            ServerChunkCache cache = e.getServer().getLevel(recordedPositions.get(event.getEntity().getUUID()).dimension).getChunkSource();
                            cache.addRegionTicket(TicketType.POST_TELEPORT, Utils.getChunkPos(new BlockPos((int)recordedPositions.get(event.getEntity().getUUID()).position.x,(int)recordedPositions.get(event.getEntity().getUUID()).position.y,(int)recordedPositions.get(event.getEntity().getUUID()).position.z)), 9, 238, true);
                            e.changeDimension(new DimensionTransition(e.getServer().getLevel(recordedPositions.get(event.getEntity().getUUID()).dimension), recordedPositions.get(event.getEntity().getUUID()).position, Vec3.ZERO, 0, 0, DimensionTransition.DO_NOTHING));
                        }
                    });
                }
            }
        }
    }

    //The below code was provided to me by Gametech (creator of T.O.'s Magic and Extras). Thank you Gametech!
    private static final Map<UUID, InfiniteVoidEffect.RecordedPosition> recordedPositions = new HashMap<>();

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
            recordedPositions.put(entity.getUUID(), new InfiniteVoidEffect.RecordedPosition(position, dimension, lookDirection));
        }
    }
}
