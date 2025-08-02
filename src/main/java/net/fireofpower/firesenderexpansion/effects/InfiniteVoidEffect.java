package net.fireofpower.firesenderexpansion.effects;


import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import net.fireofpower.firesenderexpansion.capabilities.magic.VoidDimensionManager;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.fireofpower.firesenderexpansion.util.ModTags;
import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class InfiniteVoidEffect extends MagicMobEffect implements AntiMagicSusceptible {
    private Vec3 savedPosition;
    private Level savedDimension;

    public InfiniteVoidEffect() {
        super(MobEffectCategory.NEUTRAL, Utils.rgbToInt(0,0,0));
    }

    @Override
    public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectAdded(pLivingEntity, pAmplifier);
        if(pLivingEntity.hasEffect(EffectRegistry.ASCENDED_CASTER_EFFECT)) {
            savedPosition = pLivingEntity.position();
            savedDimension = pLivingEntity.level();
        }
        if(!pLivingEntity.getType().is(ModTags.INFINITE_VOID_IMMUNE)) {
            Vec3 newPos = pLivingEntity.position().subtract(savedPosition).add(0,500,0);
            pLivingEntity.changeDimension(new DimensionTransition(Objects.requireNonNull(pLivingEntity.getServer()).getLevel(VoidDimensionManager.VOID_DIMENSION),newPos,Vec3.ZERO,pLivingEntity.getXRot(),pLivingEntity.getYRot(),DimensionTransition.DO_NOTHING));
        }
    }

    @Override
    public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {

        pLivingEntity.setDeltaMovement(pLivingEntity.getDeltaMovement().x,0,pLivingEntity.getDeltaMovement().z);

        return super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectRemoved(pLivingEntity, pAmplifier);
        if(savedPosition == null || savedDimension == null){
            savedPosition = new Vec3(0, 1000,0);
            savedDimension = pLivingEntity.level().getServer().getLevel(Level.OVERWORLD);
            System.out.println("Manifest Domain: Void found an issue while saving previous location, returning affected entities to 0,100,0 in the overworld.");
            return;
        }
        if(!pLivingEntity.getType().is(ModTags.INFINITE_VOID_IMMUNE)) {
            if(pLivingEntity.getHealth() != 0) {
                ServerChunkCache cache = pLivingEntity.getServer().getLevel(VoidDimensionManager.VOID_DIMENSION).getChunkSource();
                cache.addRegionTicket(TicketType.POST_TELEPORT, Utils.getChunkPos(pLivingEntity.getOnPos()), 9, 239, true);
                ChunkPos pos = Utils.getChunkPos(pLivingEntity.getOnPos());
                pLivingEntity.changeDimension(new DimensionTransition((ServerLevel) savedDimension, savedPosition, Vec3.ZERO, pLivingEntity.getXRot(), pLivingEntity.getYRot(), DimensionTransition.DO_NOTHING));
                //Utils.clearRegionTicket(cache,TicketType.POST_TELEPORT,pos,9,239,true);
            }
        }
    }

    public Vec3 getSavedPosition() {
        return savedPosition;
    }

    public Level getSavedDimension() {
        return savedDimension;
    }

    @Override
    public void onAntiMagic(MagicData magicData) {

    }
}
