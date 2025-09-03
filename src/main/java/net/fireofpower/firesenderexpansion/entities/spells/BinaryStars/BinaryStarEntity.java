package net.fireofpower.firesenderexpansion.entities.spells.BinaryStars;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.fireofpower.firesenderexpansion.entities.spells.GateOfEnder.GatePortal;
import net.fireofpower.firesenderexpansion.entities.spells.HollowCrystal.HollowCrystal;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.UnstableWeaponEntity;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class BinaryStarEntity extends AbstractMagicProjectile implements GeoEntity {

    public BinaryStarEntity(Level level, LivingEntity shooter) {
        this((EntityType) EntityRegistry.OBSIDIAN_STAR.get(), level);
        this.setOwner(shooter);
        this.setNoGravity(true);
    }

    public BinaryStarEntity(EntityType<? extends BinaryStarEntity> BinaryStarEntityEntityType, Level level) {
        super(BinaryStarEntityEntityType,level);
    }


    @Override
    public void trailParticles() {
        this.level().addParticle(ParticleHelper.PORTAL_FRAME, this.position().x, this.position().y, this.position().z, 0, 0, 0);
    }

    @Override
    public void impactParticles(double v, double v1, double v2) {

    }

    @Override
    public float getSpeed() {
        return 2F;
    }

    @Override
    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.empty();
    }

    //animation stuff

    private final AnimationController<BinaryStarEntity> animationController = new AnimationController<>(this, "controller", 0, this::predicate);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(animationController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private PlayState predicate(AnimationState<BinaryStarEntity> event){
        event.getController().setAnimation(DefaultAnimations.IDLE);
        return PlayState.CONTINUE;
    }

    //homing behaviour
    @Nullable
    Entity cachedHomingTarget;
    @Nullable
    UUID homingTargetUUID;

    boolean readyToBreak;
    @Nullable
    Entity cachedTarget;

    private int duration;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Nullable
    public Entity getHomingTarget() {
        if (this.cachedHomingTarget != null && !this.cachedHomingTarget.isRemoved()) {
            return this.cachedHomingTarget;
        } else if (this.homingTargetUUID != null && this.level() instanceof ServerLevel) {
            this.cachedHomingTarget = ((ServerLevel) this.level()).getEntity(this.homingTargetUUID);
            return this.cachedHomingTarget;
        } else {
            return null;
        }
    }

    public void setHomingTarget(LivingEntity entity) {
        this.homingTargetUUID = entity.getUUID();
        this.cachedHomingTarget = entity;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        if(canHitEntity(pResult.getEntity())){
            DamageSources.applyDamage(pResult.getEntity(), damage, SpellRegistries.BINARY_STARS.get().getDamageSource(this, getOwner()));
        }
    }

    @Override
    public void tick() {
        super.tick();
        var homingTarget = getHomingTarget();
        if(homingTarget != null && this.cachedTarget == null){
            this.cachedTarget = homingTarget;
        }
        if (homingTarget != null) {
            if (!doHomingTowards(homingTarget)) {
                this.homingTargetUUID = null;
                this.cachedHomingTarget = null;
            }
        }


        if(cachedTarget != null && distanceTo(cachedTarget) > 64 && !this.readyToBreak){
            if(this.level() instanceof ServerLevel serverLevel){
                serverLevel.sendParticles(ParticleRegistry.UNSTABLE_ENDER_PARTICLE.get(),this.position().x,this.position().y,this.position().z,20,0,0,0,0.1);
            }
            setPos(this.cachedTarget.position().add(0,3 + this.cachedTarget.getBbHeight(),0));
            setDeltaMovement(0,-1 * getDeltaMovement().length(),0);
            this.readyToBreak = true;
            if(this.level() instanceof ServerLevel serverLevel){
                serverLevel.sendParticles(ParticleRegistry.UNSTABLE_ENDER_PARTICLE.get(),this.position().x,this.position().y,this.position().z,20,0,0,0,0.1);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        Vec3 blockPos = result.getBlockPos().getCenter();
        if(this.readyToBreak && this.position().y <= this.cachedTarget.position().y){
            MagicManager.spawnParticles(this.level(), new BlastwaveParticleOptions(SchoolRegistry.ENDER.get().getTargetingColor(), 2),
                    blockPos.x, blockPos.y + 2, blockPos.z, 1, 0, 0, 0, 0, true);
            this.level().getEntitiesOfClass(LivingEntity.class,
                            this.getBoundingBox()
                                    .inflate(5))
                    .stream()
                    .forEach(e -> {
                        if(canHitEntity(e)){
                            DamageSources.applyDamage(e, damage, SpellRegistries.BINARY_STARS.get().getDamageSource(this, getOwner()));
                        }
                    });
            this.discard();
        }
        super.onHitBlock(result);
    }

    /**
     * @return if homing should continue
     */
    private boolean doHomingTowards(Entity entity) {
        if (entity.isRemoved()) {
            return false;
        }
        var motion = this.getDeltaMovement();
        var speed = this.getDeltaMovement().length();
        var delta = entity.getBoundingBox().getCenter().subtract(this.position()).add(entity.getDeltaMovement());
        float f = .08f;
        var newMotion = new Vec3(Mth.lerp(f, motion.x, delta.x), Mth.lerp(f, motion.y, delta.y), Mth.lerp(f, motion.z, delta.z)).normalize().scale(speed);
        this.setDeltaMovement(newMotion);
        // after a decent bit into our flight, if we are past our target, lose tracking
        return this.tickCount <= 5 || !(newMotion.dot(delta) < 0);
    }
}
