package net.fireofpower.firesenderexpansion.entities.spells.InfiniteVoid;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.fireofpower.firesenderexpansion.capabilities.magic.VoidDimensionManager;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.INBTSerializable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;


public class InfiniteVoid extends AbstractDomainEntity implements GeoEntity, AntiMagicSusceptible, INBTSerializable<CompoundTag> {
    private int duration = 15 + 2; //in seconds, 15 for actual time + 2 for spawn anim
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    public InfiniteVoid(Level level, Entity shooter) {
        this((EntityType) EntityRegistry.INFINITE_VOID.get(), level);
        this.setNoGravity(true);
        this.canUsePortal(false);
        this.setSpawnAnimTime(40);
        this.setOwner(shooter);
        this.setOpen(false);
    }

    public InfiniteVoid(EntityType<InfiniteVoid> infiniteVoidEntityType, Level level) {
        super(infiniteVoidEntityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        if(tickCount > getDuration() * 20 + getTimeSpentClashing()){
            destroyDomain();
        }
    }

    @Override
    public void handleTransportation() {
        super.handleTransportation();
        setClashable(false);
        Entity entity = getOwner();
        List<Entity> targets = entity.level().getEntities(entity, new AABB(entity.getX() - getRadius(), entity.getY() - getRadius(), entity.getZ() - getRadius(), entity.getX() + getRadius(), entity.getY() + getRadius(), entity.getZ() + getRadius()));
        if(targets.contains(getOwner())){
            targets.remove(getOwner());
        }
        for (int i = 0; i < targets.size(); i++) {
            if (targets.get(i) instanceof LivingEntity target && !target.getType().equals(EntityRegistry.INFINITE_VOID.get())) {
                target.addEffect(new MobEffectInstance(EffectRegistry.ANCHORED_EFFECT, (duration) * 20, 0, false, false, true));
                target.addEffect(new MobEffectInstance(MobEffectRegistry.ANTIGRAVITY, (duration) * 20, 0, false, false, true));
                target.addEffect(new MobEffectInstance(EffectRegistry.INFINITE_VOID_EFFECT, (duration) * 20, 0, false, false, true));
            }
        }
        if(entity instanceof LivingEntity living){
            living.addEffect(new MobEffectInstance(EffectRegistry.ASCENDED_CASTER_EFFECT, (duration) * 20, 0, false, false, true));
            living.addEffect(new MobEffectInstance(EffectRegistry.ANCHORED_EFFECT, (duration) * 20, 0, false, false, true));
            living.addEffect(new MobEffectInstance(MobEffectRegistry.ANTIGRAVITY, (duration) * 20, 0, false, false, true));
            living.addEffect(new MobEffectInstance(EffectRegistry.INFINITE_VOID_EFFECT, (duration) * 20, 0, false, false, true));
        }
    }

    @Override
    public void handleDomainClash(ArrayList<AbstractDomainEntity> opposingDomains) {
        int totalRefinement = getRefinement();
        for(int i = 0; i < opposingDomains.size(); i++){
            totalRefinement += opposingDomains.get(i).getRefinement();
        }
        if(getOwner() instanceof LivingEntity living) {
            double ownerHealthPercentage = living.getHealth() / living.getMaxHealth();
            if (!opposingDomains.isEmpty() && ownerHealthPercentage < (double) (totalRefinement - getRefinement()) / totalRefinement){
                //System.out.println("Health Threshold reached, breaking " +getOwner() + "'s domain because their health percentage: " + ownerHealthPercentage + " was less than " + (double) (totalRefinement - getRefinement()) / totalRefinement + " total refinement was " + totalRefinement + " with domains: " + opposingDomains.get(0).getRefinement() + " and " + this.getRefinement() + " REMINDER: THIS ONLY WORKS FOR 1v1 CLASHES");
                destroyDomain();
            }
        }else{
            destroyDomain();
        }
    }

    @Override
    public void targetSureHit() {
        if(level() instanceof ServerLevel serverLevel && tickCount % 60 == 0) {
            ServerLevel voidLevel = serverLevel.getServer().getLevel(VoidDimensionManager.VOID_DIMENSION);
            if(voidLevel != null) {
                voidLevel.getAllEntities().forEach(e -> {
                    if (canTarget(e)) {
                        handleSureHit(e);
                    }
                });
            }
        }
    }

    @Override
    public void handleSureHit(Entity e) {
                if(e instanceof LivingEntity livingEntity && !livingEntity.hasEffect(EffectRegistry.ASCENDED_CASTER_EFFECT)){
                    float yHeadRot = e.getYHeadRot();
                    yHeadRot += 90 * (int)(Math.random() * 5);
                    Level voidLevel = getServer().getLevel(VoidDimensionManager.VOID_DIMENSION);
                    for (int i = -5; i <= 5; i++) {
                        Vec3 particlePos = e.position();
                        particlePos = particlePos.add(0, livingEntity.getBbHeight() / 2, 0);
                        particlePos = particlePos.add(new Vec3(Math.cos(yHeadRot) * 0.3, 0.3, -Math.sin(yHeadRot) * 0.3).scale(i));
                        if (i % 2 == 0) {
                            MagicManager.spawnParticles(voidLevel, ParticleTypes.SQUID_INK, particlePos.x, particlePos.y - 0.5, particlePos.z, 1, 0, 0, 0, 0, false);
                        } else {
                            MagicManager.spawnParticles(voidLevel, ParticleTypes.SQUID_INK, particlePos.x, particlePos.y + 0.5, particlePos.z, 1, 0, 0, 0, 0, false);
                        }
                        MagicManager.spawnParticles(voidLevel, ParticleRegistry.UNSTABLE_ENDER_PARTICLE.get(), particlePos.x, particlePos.y, particlePos.z, 1, 0, 0, 0, 0, false);
                    }
                    voidLevel.playSound(null, livingEntity.blockPosition(), SoundRegistry.DEVOUR_BITE.get(), SoundSource.PLAYERS, 5, 10);
                    DamageSources.applyDamage(livingEntity, 0.1f, SpellDamageSource.source(getOwner(), SpellRegistries.INFINITE_VOID.get()));
                    livingEntity.addEffect(new MobEffectInstance(EffectRegistry.VOIDTORN_EFFECT,100,0));
                }
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    private final AnimationController<InfiniteVoid> animationController = new AnimationController<>(this, "controller", 0, this::predicate);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(animationController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private PlayState predicate(AnimationState<InfiniteVoid> event){
        long time = level().getGameTime() - getSpawnTime();
        if(time < 40) {
            event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("misc.open_grow"));
        }else if(time < 80 && !isClashing()) {
            event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("misc.open_shrink"));
        }else if(isClashing()) {
            event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("misc.idle_large"));
        } else if (time < getDuration() * 20L - 20){
            event.getController().setAnimation(DefaultAnimations.IDLE);
        }else{
            event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("misc.close"));
        }

        return PlayState.CONTINUE;
    }
}


