package net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
//import net.fireofpower.firesenderexpansion.entities.spells.UnstableWeaponEntity.UnstableWeaponEntity;
//import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.rapier.UnstableSummonedSwordEntity;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import net.fireofpower.firesenderexpansion.entities.spells.GateOfEnder.GatePortal;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.claymore.UnstableSummonedClaymoreEntity;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;

public abstract class UnstableWeaponEntity extends AbstractMagicProjectile implements GeoEntity {

    private float speed = 2;

    public UnstableWeaponEntity(Level level, LivingEntity shooter) {
        this((EntityType) EntityRegistry.UNSTABLE_SUMMONED_SWORD.get(), level);
        this.setOwner(shooter);
        this.setNoGravity(true);
    }

    public UnstableWeaponEntity(EntityType<? extends UnstableWeaponEntity> UnstableWeaponEntityEntityType, Level level) {
        super(UnstableWeaponEntityEntityType,level);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        return;
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final AnimationController<UnstableWeaponEntity> meleeController = new AnimationController<>(this, "animations", 0, this::predicate);
    RawAnimation animationToPlay = null;

    @Override
    public double getBoneResetTime() {
        return 3;
    }

    private PlayState predicate(AnimationState<UnstableWeaponEntity> animationEvent) {
        var controller = animationEvent.getController();

        if (this.animationToPlay != null) {
            controller.forceAnimationReset();
            controller.setAnimation(animationToPlay);
            animationToPlay = null;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
    }

    public static AttributeSupplier.Builder prepareAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_KNOCKBACK, 1.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.FLYING_SPEED, 1)
                .add(Attributes.ENTITY_INTERACTION_RANGE, 4)
                .add(Attributes.MOVEMENT_SPEED, .2);

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(meleeController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.empty();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if(!this.level().isClientSide()){
        MagicManager.spawnParticles(this.level(), new BlastwaveParticleOptions(SchoolRegistry.ENDER.get().getTargetingColor(), 1),
                this.position().x, this.position().y, this.position().z, 1, 0, 0, 0, 0, true);
        }
        super.onHitBlock(result);
        this.discard();
    }

    @Override
    public float getExplosionRadius() {
        return 1.5f;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (!this.level().isClientSide) {
            impactParticles(xOld, yOld, zOld);
            getImpactSound().ifPresent(this::doImpactSound);
            float explosionRadius = getExplosionRadius();
            var entities = level().getEntities(this, this.getBoundingBox().inflate(explosionRadius));
            for (Entity entity : entities) {
                double distance = entity.distanceToSqr(hitResult.getLocation());
                if (distance < explosionRadius * explosionRadius && canHitEntity(entity)) {
                    DamageSources.applyDamage(entity, damage, SpellRegistries.GATE_OF_ENDER.get().getDamageSource(this, getOwner()));
                }
            }
        }
        //remove(RemovalReason.DISCARDED);
        discard();
        super.onHit(hitResult);
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public float getSpeed() {
        return speed;
    }
}