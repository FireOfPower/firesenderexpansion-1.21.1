package net.fireofpower.firesenderexpansion.entities.HollowCrystal;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.particle.ZapParticleOption;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;

public class HollowCrystal extends AbstractMagicProjectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    public HollowCrystal(Level level, LivingEntity shooter) {
        this((EntityType) EntityRegistry.HOLLOW_CRYSTAL.get(), level);
        this.setOwner(shooter);
    }

    public HollowCrystal(EntityType<HollowCrystal> hollowCrystalEntityType, Level level) {
        super(hollowCrystalEntityType,level);
    }

    @Override
    public void trailParticles() {
        Vec3 pos = this.getBoundingBox().getCenter().add(this.getDeltaMovement());
        Vec3 random = Utils.getRandomVec3((double)1.0F).add(pos);
        pos = pos.add(this.getDeltaMovement());
        this.level().addParticle(new ZapParticleOption(random), pos.x, pos.y, pos.z, (double)0.0F, (double)0.0F, (double)0.0F);
    }

    @Override
    public void impactParticles(double v, double v1, double v2) {
        MagicManager.spawnParticles(this.level(), ParticleHelper.COMET_FOG, v, v1, v2, 12, 0.08, 0.08, 0.08, 0.3, false);
    }

    @Override
    public float getSpeed() {
        return 3.0F;
    }

    @Override
    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.of(SoundRegistry.ABYSSAL_SHROUD);
    }


    private final AnimationController<HollowCrystal> animationController = new AnimationController<>(this, "controller", 0, this::predicate);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(animationController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private PlayState predicate(software.bernie.geckolib.animation.AnimationState<HollowCrystal> event){
        event.getController().setAnimation(RawAnimation.begin().then("animation.hollow_crystal.animation", Animation.LoopType.LOOP));

        return PlayState.CONTINUE;
    }
}
