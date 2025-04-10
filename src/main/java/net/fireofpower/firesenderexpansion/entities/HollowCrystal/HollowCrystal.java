package net.fireofpower.firesenderexpansion.entities.HollowCrystal;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.entity.spells.black_hole.BlackHole;
import io.redspace.ironsspellbooks.entity.spells.black_hole.BlackHoleRenderer;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.particle.ShockwaveParticleOptions;
import io.redspace.ironsspellbooks.particle.ZapParticle;
import io.redspace.ironsspellbooks.particle.ZapParticleOption;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.esoteric_edge.EsotericEdge;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEEntityRegistry;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.fireofpower.firesenderexpansion.registries.PotionEffectRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.Color;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class HollowCrystal extends AbstractMagicProjectile implements GeoEntity, AntiMagicSusceptible {
    private int soundCounter = 19;
    private boolean allowIdleAnim = true;
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
//        Vec3 pos = this.getBoundingBox().getCenter().add(this.getDeltaMovement());
//        Vec3 random = Utils.getRandomVec3((double)1.0F).add(pos);
//        pos = pos.add(this.getDeltaMovement());
        //this.level().addParticle(new ShockwaveParticleOptions(new Vector3f(255,0,255),5F,true), pos.x, pos.y, pos.z, (double)0.0F, (double)0.0F, (double)0.0F);
        //ok i'm using it for sounds sue me
        soundCounter++;
        if(soundCounter == 20){
            this.level().playLocalSound(this,SoundRegistry.BLACK_HOLE_LOOP.get(),SoundSource.PLAYERS,3f,1f);
        }
        CameraShakeManager.addCameraShake(new CameraShakeData(20, this.position(), 20));
        //ok this is just actual code I gave up (credit to Snack for the airblast code)
        this.level().getEntitiesOfClass(Projectile.class,
                        this.getBoundingBox()
                                .inflate(1))
                .stream()
                .filter(proj -> proj.distanceTo(this) < 5 /*&&
                        !Objects.equals(proj.getOwner(), this.getOwner())*/)
                .forEach(e -> {
                    if(e instanceof EsotericEdge){
                        e.discard();
                        allowIdleAnim = false;
                        //setting deltaMovement here didn't work
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                discardThis();
                                allowIdleAnim = true;
                            }
                        },3000);
                    }
                });
    }

    @Override
    public void tick() {
        if(!allowIdleAnim) {
            for(int i = 0; i < 10; i++) {
                this.level().addParticle(ParticleTypes.END_ROD, particleRangeX(10), particleRangeY(10), particleRangeZ(10), 0, 0, 0);
            }
        }else{
            this.level().addParticle(ParticleHelper.PORTAL_FRAME, particleRangeX(5), particleRangeY(5), particleRangeZ(5), 0, 0, 0);
        }
        super.tick();
    }

    public double particleRangeX(float distance){
        double range = Math.random() * distance;
        return this.getX() + range - distance/2;
    }
    public double particleRangeY(float distance){
        double range = Math.random() * distance;
        return this.getY() + 2 + range - distance/2;
    }
    public double particleRangeZ(float distance){
        double range = Math.random() * distance;
        return this.getZ() + range - distance/2;
    }

    @Override
    public void impactParticles(double v, double v1, double v2) {
        MagicManager.spawnParticles(this.level(), ParticleHelper.COMET_FOG, v, v1, v2, 12, 0.08, 0.08, 0.08, 0.3, false);
    }

    @Override
    public float getSpeed() {
        return 3.0F;
    }

    public void discardThis(){
        this.discard();
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
        if (allowIdleAnim) {
            event.getController().setAnimation(DefaultAnimations.IDLE);
        }else{
            event.getController().setAnimation(DefaultAnimations.DIE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {

    }
}
