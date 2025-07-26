package net.fireofpower.firesenderexpansion.effects;


import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.PocketDimensionManager;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.spells.eldritch.PocketDimensionSpell;
import net.fireofpower.firesenderexpansion.capabilities.magic.VoidDimensionManager;
import net.fireofpower.firesenderexpansion.registries.PotionEffectRegistry;
import net.fireofpower.firesenderexpansion.util.ModTags;
import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.world.chunk.ForcedChunkManager;

import java.awt.*;
import java.util.*;

import static net.fireofpower.firesenderexpansion.util.Utils.getChunkPos;

public class InfiniteVoidPotionEffect extends MagicMobEffect implements AntiMagicSusceptible {
    private Vec3 savedPosition;
    private Level savedDimension;
    private final int duration = 18;
    private final int range = 20;

    public InfiniteVoidPotionEffect() {
        super(MobEffectCategory.BENEFICIAL, 5984177);
    }

    @Override
    public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectAdded(pLivingEntity, pAmplifier);
        if(pLivingEntity.hasEffect(PotionEffectRegistry.ASCENDED_CASTER_POTION_EFFECT)) {
            savedPosition = pLivingEntity.position();
            savedDimension = pLivingEntity.level();
        }
        if(!pLivingEntity.getType().is(ModTags.INFINITE_VOID_IMMUNE)) {
            //puts savedPosition at 0,0 and keeps relative distance
            Vec3 newPos = pLivingEntity.position().subtract(savedPosition).add(0,1000,0);
            pLivingEntity.changeDimension(new DimensionTransition(Objects.requireNonNull(pLivingEntity.getServer()).getLevel(VoidDimensionManager.VOID_DIMENSION),newPos,Vec3.ZERO,pLivingEntity.getXRot(),pLivingEntity.getYRot(),DimensionTransition.DO_NOTHING));
        }
    }

    @Override
    public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectRemoved(pLivingEntity, pAmplifier);
        if(savedPosition == null || savedDimension == null){
            savedPosition = new Vec3(0, 100,0);
            savedDimension = pLivingEntity.level().getServer().getLevel(Level.OVERWORLD);
            System.out.println("Manifest Domain: Void found an issue while saving previous location, returning affected entities to 0,100,0 in the overworld.");
            return;
        }
        if(!pLivingEntity.getType().is(ModTags.INFINITE_VOID_IMMUNE)) {
            if(pLivingEntity.getHealth() != 0) {
                ServerChunkCache cache = pLivingEntity.getServer().getLevel(VoidDimensionManager.VOID_DIMENSION).getChunkSource();
                cache.addRegionTicket(TicketType.POST_TELEPORT, Utils.getChunkPos(pLivingEntity.getOnPos()), 3, 239, true);
                ChunkPos pos = Utils.getChunkPos(pLivingEntity.getOnPos());
                pLivingEntity.changeDimension(new DimensionTransition((ServerLevel) savedDimension, savedPosition, Vec3.ZERO, pLivingEntity.getXRot(), pLivingEntity.getYRot(), DimensionTransition.DO_NOTHING));
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        cache.removeRegionTicket(TicketType.POST_TELEPORT,pos,3,239,true);
                    }
                },200);
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
