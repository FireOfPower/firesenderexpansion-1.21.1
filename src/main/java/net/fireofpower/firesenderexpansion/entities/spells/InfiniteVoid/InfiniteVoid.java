package net.fireofpower.firesenderexpansion.entities.spells.InfiniteVoid;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;


public class InfiniteVoid extends AbstractMagicProjectile implements GeoEntity, AntiMagicSusceptible {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    public InfiniteVoid(Level level, LivingEntity shooter) {
        this((EntityType) EntityRegistry.INFINITE_VOID.get(), level);
        this.setOwner(shooter);
        this.setNoGravity(true);
    }

    public InfiniteVoid(EntityType<InfiniteVoid> infiniteVoidEntityType, Level level) {
        super(infiniteVoidEntityType,level);
    }

    @Override
    public void trailParticles() {
        Vec3 pos = this.getBoundingBox().getCenter().add(this.getDeltaMovement());
        Vec3 random = Utils.getRandomVec3((double)1.0F).add(pos);
        pos = pos.add(this.getDeltaMovement());
    }

//    protected void onHitBlock(BlockHitResult blockHitResult) {
//        super.onHitBlock(blockHitResult);
//        this.discard();
//    }
//    @Override
//    protected void onHitEntity(EntityHitResult pResult) {
//        var target = pResult.getEntity();
//        if (target instanceof LivingEntity livingTarget)
//        {
//            livingTarget.addEffect(new MobEffectInstance(PotionEffectRegistry.ANCHORED_POTION_EFFECT, 100, 0));
//        }
//        discard();
//    }


    @Override
    public void impactParticles(double v, double v1, double v2) {
        MagicManager.spawnParticles(this.level(), ParticleHelper.UNSTABLE_ENDER, v, v1, v2, 12, 0.08, 0.08, 0.08, 0.3, false);
    }

    @Override
    public float getSpeed() {
        return 0F;
    }

    @Override
    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.empty();
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
        event.getController().setAnimation(DefaultAnimations.IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {

    }

    @Override
    public void tick() {
        if(tickCount > 40){
            discard();
        }
        super.tick();
    }
}


