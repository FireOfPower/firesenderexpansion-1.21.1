package net.fireofpower.firesenderexpansion.entities.ObsidianRod;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.fireofpower.firesenderexpansion.entities.HollowCrystal.HollowCrystal;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;


public class ObsidianRod extends AbstractMagicProjectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    public ObsidianRod(Level level, LivingEntity shooter) {
        this((EntityType) EntityRegistry.OBSIDIAN_ROD.get(), level);
        this.setOwner(shooter);
    }

    public ObsidianRod(EntityType<ObsidianRod> obsidianRodEntityType, Level level) {
        super(obsidianRodEntityType,level);
    }

    @Override
    public void trailParticles() {
        Vec3 pos = this.getBoundingBox().getCenter().add(this.getDeltaMovement());
        Vec3 random = Utils.getRandomVec3((double)1.0F).add(pos);
        pos = pos.add(this.getDeltaMovement());
        //this.level().addParticle(new ZapParticleOption(random), pos.x, pos.y, pos.z, (double)0.0F, (double)0.0F, (double)0.0F);
    }

    public void shoot(Vec3 rotation, float inaccuracy) {
        double speed = rotation.length();
        Vec3 offset = Utils.getRandomVec3((double)1.0F).normalize().scale((double)inaccuracy);
        Vec3 motion = rotation.normalize()/*.add(offset)*/.normalize().scale(speed);
        super.shoot(motion);
    }

    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        this.discard();
    }

    @Override
    public void impactParticles(double v, double v1, double v2) {
        MagicManager.spawnParticles(this.level(), ParticleHelper.PORTAL_FRAME, v, v1, v2, 12, 0.08, 0.08, 0.08, 0.3, false);
    }

    @Override
    public float getSpeed() {
        return 3.0F;
    }

    @Override
    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.empty();
    }


    private final AnimationController<ObsidianRod> animationController = new AnimationController<>(this, "controller", 0, this::predicate);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(animationController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private PlayState predicate(AnimationState<ObsidianRod> event){
        event.getController().setAnimation(DefaultAnimations.IDLE);
        return PlayState.CONTINUE;
    }
}

