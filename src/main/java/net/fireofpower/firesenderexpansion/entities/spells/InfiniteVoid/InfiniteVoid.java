package net.fireofpower.firesenderexpansion.entities.spells.InfiniteVoid;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.ender_chain.EnderChain;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.acetheeldritchking.aces_spell_utils.entity.spells.AbstractDomainEntity;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.capabilities.magic.VoidDimensionManager;
import net.fireofpower.firesenderexpansion.damage.VoidSureHitDamageSource;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.fireofpower.firesenderexpansion.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.ForceLoadCommand;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.world.chunk.ForcedChunkManager;
import net.neoforged.neoforge.event.level.ChunkEvent;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class InfiniteVoid extends net.acetheeldritchking.aces_spell_utils.entity.spells.AbstractDomainEntity implements GeoEntity {
    private int duration = 30; //in seconds
    private boolean overWritingForcedChunk;



    public InfiniteVoid(Level level, Entity shooter, int radius, int refinement, int duration) {
        this((EntityType) EntityRegistry.INFINITE_VOID.get(), level);
        //the parts that won't change per-cast
        this.setSpawnAnimTime(40);
        this.setOpen(false);
        setDuration(30); //no this isnt necessary but its good practice imo

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
    public void onActivation() {
        super.onActivation();
        this.level().getEntitiesOfClass(LivingEntity.class, new AABB(this.position().subtract((double)this.getRadius(), (double)this.getRadius(), (double)this.getRadius()), this.position().add((double)this.getRadius(), (double)this.getRadius(), (double)this.getRadius())))
                .stream().filter(e -> e.distanceTo(this) <= getRadius())
                .filter(e -> !e.equals(getOwner()) && !e.getType().is(ModTags.INFINITE_VOID_IMMUNE)).forEach((e) -> {
                    final int CHAIN_COUNT = 3;
                    Vec3 origin = e.getBoundingBox().getCenter();

                    float theta = Mth.TWO_PI / CHAIN_COUNT;
                    for (int i = 0; i < CHAIN_COUNT; i++) {
                        float angle = theta * i + Mth.TWO_PI / 4 - getYRot() * Mth.DEG_TO_RAD;
                        float radius = 8 * 0.5f + e.getBbWidth() * .4f;
                        Vec3 direction = new Vec3(Mth.cos(angle) * radius, 0, Mth.sin(angle) * radius);
                        Vec3 worldPos = Utils.moveToRelativeGroundLevel(e.level(), origin.add(direction), 2);
                        if (level().noCollision(AABB.ofSize(worldPos, 0.5, 0.5, 0.5))) {
                            Vec3 random = Utils.getRandomVec3(2);
                            random = random.subtract(direction.scale(direction.normalize().dot(random.normalize())));
                            worldPos = origin.add(direction).add(random);
                        }
                        spawnChain(e, worldPos);
                    }
                    e.addEffect(new MobEffectInstance(EffectRegistry.ANCHORED_EFFECT,getSpawnAnimTime(),0,false,false));
        });
    }

    @Override
    public void tick() {
        super.tick();
        //FiresEnderExpansion.LOGGER.debug("clashing: {} isClient: {}",isClashing(),level().isClientSide());
        //again tickCount is unreliable with the cross-dimensional travel
        if (!this.level().isClientSide() && tickCount == 1) {
            if(level() instanceof ServerLevel level) {
                ChunkPos pos = new ChunkPos(this.blockPosition());
                //level.getChunkSource().addRegionTicket(TicketType.FORCED, new ChunkPos(this.blockPosition()), 3, new ChunkPos(this.blockPosition()), true);
                if(level.getForcedChunks().contains(pos.toLong())){
                    overWritingForcedChunk = true;
                }
                level.setChunkForced(pos.x, pos.z, true);
            }
        }
        long time = level().getGameTime() - getSpawnTime();
        if(time > (getDuration() + 2 /* plus two for the spawn animation */) * 20L /* to because it's stored in seconds, but needs to be in ticks */ + getTimeSpentClashing() /* it'd be annoying if you lost all your duration while clashing :( */){
            //FiresEnderExpansion.LOGGER.debug("Duration Diff (Suspicion: getTimeSpentClashing is {})",getTimeSpentClashing());
            destroyDomain();
        }
    }

    @Override
    public void destroyDomain() {
        if(level() instanceof ServerLevel level){
            ChunkPos pos = new ChunkPos(this.blockPosition());

            level.setChunkForced(pos.x,pos.z,overWritingForcedChunk);
        }
        super.destroyDomain();
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
                //the transportation is handled by the InfiniteVoidEffect
                target.addEffect(new MobEffectInstance(EffectRegistry.INFINITE_VOID_EFFECT, (duration) * 20, 0, false, false, true));
            }
        }
        if(entity instanceof LivingEntity living){
            //all of the above plus the "beneficial" caster effect that gives big buffs and prevents the surehit
            living.addEffect(new MobEffectInstance(EffectRegistry.ASCENDED_CASTER_EFFECT, (duration) * 20, 0, false, false, true));
            living.addEffect(new MobEffectInstance(EffectRegistry.INFINITE_VOID_EFFECT, (duration) * 20, 0, false, false, true));
        }
    }

    @Override
    public void handleDomainClash(ArrayList<AbstractDomainEntity> opposingDomains) {
        if(!level().isClientSide) {
            int totalRefinement = getRefinement();
            for (int i = 0; i < opposingDomains.size(); i++) {
                totalRefinement += opposingDomains.get(i).getRefinement();
            }
            //basically what this does is the higher someone's refinement is, the lower they can get before they lose the clash
            //If two equally refined people clash, this threshold is 50% health
            //The more people that are in the clash, the easier it is for a domain to break
            if (getOwner() instanceof LivingEntity living) {
                double ownerHealthPercentage = living.getHealth() / living.getMaxHealth();
                if (!opposingDomains.isEmpty() && ownerHealthPercentage < (double) (totalRefinement - getRefinement()) / totalRefinement) {
                    //System.out.println("Health Diff");
                    //FiresEnderExpansion.LOGGER.debug("Health Diff");
                    destroyDomain();
                }
            } else {
                //if the clasher is not alive then just dont even try to clash
                //System.out.println("Nonliving Diff");
                //FiresEnderExpansion.LOGGER.debug("Nonliving Diff");
                destroyDomain();
            }
        }
    }

    @Override
    public void targetSureHit() {
        //FiresEnderExpansion.LOGGER.debug("Targeting Sure Hit");
        final int SUREHIT_BIG_DANGER_RADIUS = 30;
        //only attack every 5 seconds
        //attack more often if it's far away from the caster
        if(level() instanceof ServerLevel serverLevel && tickCount % 20 == 0) {
            ServerLevel voidLevel = serverLevel.getServer().getLevel(VoidDimensionManager.VOID_DIMENSION);
            if(voidLevel != null) {
                voidLevel.getAllEntities().forEach(e -> {
                    if (e instanceof LivingEntity living && canTarget(living)) {
                        if (tickCount % 100 == 0) {
                            //FiresEnderExpansion.LOGGER.debug("Sending sure hit tick to {}",living);
                            handleSureHit(living);
                        } else if (voidLevel.getEntitiesOfClass(LivingEntity.class, new AABB(e.position().subtract(SUREHIT_BIG_DANGER_RADIUS, SUREHIT_BIG_DANGER_RADIUS, SUREHIT_BIG_DANGER_RADIUS), e.position().add(SUREHIT_BIG_DANGER_RADIUS, SUREHIT_BIG_DANGER_RADIUS, SUREHIT_BIG_DANGER_RADIUS))).stream().noneMatch(player -> player.hasEffect(EffectRegistry.ASCENDED_CASTER_EFFECT))) {
                            handleSureHit(living);
                        }
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
                            //MagicManager.spawnParticles(voidLevel, ParticleTypes.SQUID_INK, particlePos.x, particlePos.y + 0.5, particlePos.z, 1, 0, 0, 0, 0, false);
                        }
                        //MagicManager.spawnParticles(voidLevel, ParticleRegistry.UNSTABLE_ENDER_PARTICLE.get(), particlePos.x, particlePos.y, particlePos.z, 1, 0, 0, 0, 0, false);
                    }
                    //sound
                    voidLevel.playSound(null, livingEntity.blockPosition(), SoundRegistry.DEVOUR_BITE.get(), SoundSource.PLAYERS, 5, 10);
                    //damage
                    if(getOwner() != null && livingEntity.hurt(new VoidSureHitDamageSource(getOwner()),5)) {
                        //apply effect
                        livingEntity.addEffect(new MobEffectInstance(EffectRegistry.VOIDTORN_EFFECT, 100, 0));
                    }
                }
    }

    private void spawnChain(LivingEntity victim, Vec3 anchor) {
        anchor = Utils.raycastForBlock(victim.level(), this.position(), anchor, ClipContext.Fluid.NONE).getLocation();
        EnderChain chain = new EnderChain(level(), getOwner(), victim, anchor);
        chain.setHealth(100);
        chain.setLifetime(getSpawnAnimTime());
        chain.setRestraintStrength(0.35f);
        level().addFreshEntity(chain);
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
        //FiresEnderExpansion.LOGGER.debug("predicate tick");
        if(time < 40) {
            //FiresEnderExpansion.LOGGER.debug("Open Anim, time:{} isClient:{}", time,level().isClientSide());
            event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("misc.open_grow"));
        }else if(time < 80 && !isClashing()) {
            //FiresEnderExpansion.LOGGER.debug("Not clashing, shrinking domain time:{} isClient:{}", time,level().isClientSide());
            event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("misc.open_shrink"));
        }else if(isClashing()) {
            //FiresEnderExpansion.LOGGER.debug("Large Anim, time:{} isClient:{}", time,level().isClientSide());
            event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("misc.idle_large"));
        } else if (time < (getDuration() + 2) * 20L + getTimeSpentClashing() - 20){
            //FiresEnderExpansion.LOGGER.debug("Idle Anim, time:{} isClient:{}", time,level().isClientSide());
            event.getController().setAnimation(DefaultAnimations.IDLE);
        }else{
            //FiresEnderExpansion.LOGGER.debug("Close Anim, time:{} isClient:{}", time,level().isClientSide());
            event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("misc.close"));
        }
        return PlayState.CONTINUE;
    }
}


