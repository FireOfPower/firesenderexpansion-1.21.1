package net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
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
    private boolean isHoming = false;
    private float homingStrength = 0;
    private Vec3 location;

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

    public void setHoming(boolean homing) {
        isHoming = homing;
    }

    @Override
    protected void handleCursorHoming() {
        var cursorHoming = isCursorHoming();
        if (!cursorHoming) {
            return;
        }
        float maxRange = 48;
        var owner = getOwner();
        if (owner == null || position().distanceToSqr(owner.position()) > maxRange * maxRange) {
            setCursorHoming(false);
            return;
        }
        Vec3 start = owner.getEyePosition();
        Vec3 end = start.add(owner.getForward().scale(maxRange));
        HitResult hitresult = Utils.raycastForEntity(level(), owner, start, end, true, 0.5f, entity -> Utils.canHitWithRaycast(entity) && !DamageSources.isFriendlyFireBetween(entity, owner));
        if(location == null) {
            location = hitresult instanceof EntityHitResult entityHit ? entityHit.getEntity().getBoundingBox().getCenter() : hitresult.getLocation();
            location = location.add(this.position().subtract(owner.position())).subtract(this.position().subtract(owner.position()).normalize().scale(3));
        }
        homeTowards(location, homingStrength);
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
//        if(!this.level().isClientSide()){
//        MagicManager.spawnParticles(this.level(), new BlastwaveParticleOptions(SchoolRegistry.ENDER.get().getTargetingColor(), 1),
//                this.position().x, this.position().y, this.position().z, 1, 0, 0, 0, 0, true);
//        }
        if(isHoming){
            this.discard();
        }
        super.onHitBlock(result);
    }

    @Override
    public float getExplosionRadius() {
        return 1.5f;
    }

    @Override
    public void tick() {
        if(tickCount > 10){
            if(speed < 2) {
                speed = 2;
            }
            if(getDeltaMovement().length() != speed){
                setDeltaMovement(getDeltaMovement().normalize().scale(2));
            }
            if(tickCount > 20 && location != null && this.getOwner() != null ) {
                if(this.getOwner().position().distanceTo(location) >= 10) {
                    homingStrength = Mth.clamp((float) (5f / this.getOwner().position().distanceTo(location)), 0, 20);
                }else{
                    if(getOwner() instanceof ServerPlayer serverPlayer){
                        serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("msg.firesenderexpansion.too_close")
                                .withStyle(s -> s.withColor(TextColor.fromRgb(0xF35F5F)))));
                    }
                }
            }
            var entities = level().getEntities(this, this.getBoundingBox());
            for (Entity entity : entities) {
                if(canHitEntity(entity)) {
                    DamageSources.applyDamage(entity, damage, SpellRegistries.GATE_OF_ENDER.get().getDamageSource(this, getOwner()));
                }
            }
        }
        if(tickCount > 2000){
            this.discard();
        }
        super.tick();
    }

    @Override
    public boolean isCursorHoming() {
        return isHoming;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (!this.level().isClientSide) {
            impactParticles(xOld, yOld, zOld);
            getImpactSound().ifPresent(this::doImpactSound);
        }
        //remove(RemovalReason.DISCARDED);
        //discard();
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