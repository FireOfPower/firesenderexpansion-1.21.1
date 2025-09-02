package net.fireofpower.firesenderexpansion.entities.spells.BinaryStars;

import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import net.fireofpower.firesenderexpansion.entities.spells.GateOfEnder.GatePortal;
import net.fireofpower.firesenderexpansion.entities.spells.HollowCrystal.HollowCrystal;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.UnstableWeaponEntity;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;

public class BinaryStarEntity extends AbstractMagicProjectile implements GeoEntity {

    public BinaryStarEntity(Level level, LivingEntity shooter) {
        this((EntityType) EntityRegistry.NOVA_STAR.get(), level);
        this.setOwner(shooter);
        this.setNoGravity(true);
    }

    public BinaryStarEntity(EntityType<? extends BinaryStarEntity> BinaryStarEntityEntityType, Level level) {
        super(BinaryStarEntityEntityType,level);
    }


    @Override
    public void trailParticles() {
    }

    @Override
    public void impactParticles(double v, double v1, double v2) {

    }

    @Override
    public float getSpeed() {
        return 3;
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
}
