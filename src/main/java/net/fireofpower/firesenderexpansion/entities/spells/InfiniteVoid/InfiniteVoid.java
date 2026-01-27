package net.fireofpower.firesenderexpansion.entities.spells.InfiniteVoid;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.fireofpower.firesenderexpansion.capabilities.magic.VoidDimensionManager;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.fireofpower.firesenderexpansion.util.ModTags;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;


public class InfiniteVoid extends AbstractDomainEntity implements GeoEntity {
    private int duration = 15; //in seconds


    public InfiniteVoid(Level level, Entity shooter, int radius, int refinement, int duration) {
        this((EntityType) EntityRegistry.INFINITE_VOID.get(), level);
        //the parts that won't change per-cast
        this.setSpawnAnimTime(40);
        this.setOpen(false);
        setDuration(15); //no this isnt necessary but its good practice imo

        //the parts that will
        this.setOwner(shooter);
        this.setRadius(radius);
        this.setRefinement(refinement);
        this.setDuration(duration);
    }

    public InfiniteVoid(EntityType<InfiniteVoid> infiniteVoidEntityType, Level level) {
        super(infiniteVoidEntityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        //again tickCount is unreliable with the cross-dimensional travel
        long time = level().getGameTime() - getSpawnTime();
        if(time > (getDuration() + 2 /* plus two for the spawn animation */) * 20L /* to because it's stored in seconds, but needs to be in ticks */ + getTimeSpentClashing() /* it'd be annoying if you lost all your duration while clashing :( */){
            destroyDomain();
        }
    }

    @Override
    public void handleTransportation() {
        super.handleTransportation();
        //can't clash when in the small form since that'd wreak havoc with the effect management
        setClashable(false);
        Entity entity = getOwner();
        //target everyone within the hitbox who is within a sphere of radius
        List<Entity> targets = entity.level().getEntities(entity, new AABB(entity.getX() - getRadius(), entity.getY() - getRadius(), entity.getZ() - getRadius(), entity.getX() + getRadius(), entity.getY() + getRadius(), entity.getZ() + getRadius()));
        targets.removeIf(e -> e.distanceTo(this) > getRadius());
        if(targets.contains(getOwner())){
            //the owner is handled separately
            targets.remove(getOwner());
        }
        for (int i = 0; i < targets.size(); i++) {
            if (targets.get(i) instanceof LivingEntity target && !target.getType().equals(EntityRegistry.INFINITE_VOID.get()) && !target.getType().is(ModTags.INFINITE_VOID_IMMUNE)) {
                //teleportation bad here since ppl can easily run away forever
                target.addEffect(new MobEffectInstance(EffectRegistry.ANCHORED_EFFECT, (duration) * 20, 0, false, false, true));
                //lets people float
                target.addEffect(new MobEffectInstance(MobEffectRegistry.ANTIGRAVITY, (duration) * 20, 0, false, false, true));
                //the transportation is handled by the InfiniteVoidEffect
                target.addEffect(new MobEffectInstance(EffectRegistry.INFINITE_VOID_EFFECT, (duration) * 20, 0, false, false, true));
            }
        }
        if(entity instanceof LivingEntity living){
            //all of the above plus the "beneficial" caster effect that gives big buffs and prevents the surehit
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
        //basically what this does is the higher someone's refinement is, the lower they can get before they lose the clash
        //If two equally refined people clash, this threshold is 50% health
        //The more people that are in the clash, the easier it is for a domain to break
        if(getOwner() instanceof LivingEntity living) {
            double ownerHealthPercentage = living.getHealth() / living.getMaxHealth();
            if (!opposingDomains.isEmpty() && ownerHealthPercentage < (double) (totalRefinement - getRefinement()) / totalRefinement){
                destroyDomain();
            }
        }else{
            //if the clasher is not alive then just dont even try to clash
            destroyDomain();
        }
    }

    @Override
    public void targetSureHit() {
        //only attack every 3 seconds
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
        //only attack living entities, and not any that have the caster effect
                if(e instanceof LivingEntity livingEntity && !livingEntity.hasEffect(EffectRegistry.ASCENDED_CASTER_EFFECT)){
                    //I tried to do something cool
                    float yHeadRot = e.getYHeadRot();
                    yHeadRot += 90 * (int)(Math.random() * 5);
                    Level voidLevel = getServer().getLevel(VoidDimensionManager.VOID_DIMENSION);
                    //slash particle effect
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
                    //sound
                    voidLevel.playSound(null, livingEntity.blockPosition(), SoundRegistry.DEVOUR_BITE.get(), SoundSource.PLAYERS, 5, 10);
                    //damage
                    DamageSources.applyDamage(livingEntity, 5f, SpellDamageSource.source(getOwner(), SpellRegistries.INFINITE_VOID.get()));
                    //apply effect
                    livingEntity.addEffect(new MobEffectInstance(EffectRegistry.VOIDTORN_EFFECT,100,0));
                }
    }

    //getters/setters

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    //geckolibbing it

    private final AnimationController<InfiniteVoid> animationController = new AnimationController<>(this, "controller", 0, this::predicate);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

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
        } else if (time < (getDuration() + 2) * 20L + getTimeSpentClashing() - 20){
            event.getController().setAnimation(DefaultAnimations.IDLE);
        }else{
            event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("misc.close"));
        }
        return PlayState.CONTINUE;
    }
}


