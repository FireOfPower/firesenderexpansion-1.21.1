package net.fireofpower.firesenderexpansion.entities.HollowCrystal;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.entity.spells.black_hole.BlackHole;
import io.redspace.ironsspellbooks.entity.spells.black_hole.BlackHoleRenderer;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.particle.ShockwaveParticleOptions;
import io.redspace.ironsspellbooks.particle.ZapParticle;
import io.redspace.ironsspellbooks.particle.ZapParticleOption;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.fireofpower.firesenderexpansion.registries.PotionEffectRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.Color;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.Timer;

public class HollowCrystal extends AbstractMagicProjectile implements GeoEntity {
    private int soundCounter = 19;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    public HollowCrystal(Level level, LivingEntity shooter) {
        this((EntityType) EntityRegistry.HOLLOW_CRYSTAL.get(), level);
        this.setOwner(shooter);
        this.setNoGravity(true);
    }

    public HollowCrystal(EntityType<HollowCrystal> hollowCrystalEntityType, Level level) {
        super(hollowCrystalEntityType,level);
    }

    @Override
    public void trailParticles() {
        Vec3 pos = this.getBoundingBox().getCenter().add(this.getDeltaMovement());
        Vec3 random = Utils.getRandomVec3((double)1.0F).add(pos);
        pos = pos.add(this.getDeltaMovement());
        //this.level().addParticle(new ShockwaveParticleOptions(new Vector3f(255,0,255),5F,true), pos.x, pos.y, pos.z, (double)0.0F, (double)0.0F, (double)0.0F);
        soundCounter++;
        if(soundCounter == 20){
            this.level().playLocalSound(this,SoundRegistry.BLACK_HOLE_LOOP.get(),SoundSource.PLAYERS,3f,1f);
        }
    }

    @Override
    public void impactParticles(double v, double v1, double v2) {
        MagicManager.spawnParticles(this.level(), ParticleHelper.COMET_FOG, v, v1, v2, 12, 0.08, 0.08, 0.08, 0.3, false);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        var target = pResult.getEntity();
        DamageSources.applyDamage(target, damage,
                SpellRegistries.HOLLOW_CRYSTAL.get().getDamageSource(this, getOwner()));
        System.out.println("Did " + damage + " damage");

    }

    @Override
    public float getSpeed() {
        return 3.0F;
    }

    @Override
    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.of(SoundRegistry.EARTHQUAKE_LOOP);
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

    private PlayState predicate(AnimationState<HollowCrystal> event){
        event.getController().setAnimation(DefaultAnimations.IDLE);
        return PlayState.CONTINUE;
    }
}
