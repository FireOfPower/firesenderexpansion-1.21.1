package net.fireofpower.firesenderexpansion.entities.spells.GateOfEnder;

import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.UnstableWeaponEntity;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.claymore.UnstableSummonedClaymoreEntity;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.rapier.UnstableSummonedRapierEntity;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.sword.UnstableSummonedSwordEntity;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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

public class GatePortal extends AbstractMagicProjectile implements GeoEntity {
    public float speed = 0;
    public GatePortal(Level level, LivingEntity shooter) {
        this((EntityType) EntityRegistry.GATE_PORTAL.get(), level);
        this.setOwner(shooter);
        this.setNoGravity(true);
    }

    public GatePortal(EntityType<GatePortal> gatePortalEntityType, Level level) {
        super(gatePortalEntityType,level);
    }

    @Override
    protected void rotateWithMotion() {

    }

    public void shootSword(){
        Vec3 origin = this.position();
        double choice = Math.random() * 3;
        UnstableWeaponEntity weapon;
        if(choice < 1){
            weapon = new UnstableSummonedSwordEntity(this.level(), (LivingEntity) this.getOwner());
        }else if (choice < 2){
            weapon = new UnstableSummonedRapierEntity(this.level(), (LivingEntity) this.getOwner());
        }else{
            weapon = new UnstableSummonedClaymoreEntity(this.level(), (LivingEntity) this.getOwner());
        }
        this.setPos(origin.subtract(0.0,this.getBbHeight()/2,0.0));
        weapon.setPos(origin.add(0,weapon.getBbHeight()/2,0));
        weapon.setDamage(this.getDamage());
        weapon.setSpeed(0.01f);
        weapon.shoot(this.getLookAngle());
        this.level().playSound((Player)null, origin.x, origin.y, origin.z, SoundRegistry.ECHOING_STRIKE, SoundSource.PLAYERS, 0.1F, 1.0F);
        this.level().addFreshEntity(weapon);

        //particles
        if(this.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleRegistry.UNSTABLE_ENDER_PARTICLE.get(), this.getX(), this.getY(), this.getZ(), 5, 0, 0, 0, 0.1);
        }
    }

    @Override
    public void travel() {
    }

    @Override
    public void tick() {
        if(tickCount == 5){
            shootSword();
        }
        if(tickCount > 20){
            discard();
        }
        super.tick();
    }

    @Override
    public void trailParticles() {

    }

    @Override
    public void impactParticles(double v, double v1, double v2) {

    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.empty();
    }

    private final AnimationController<GatePortal> animationController = new AnimationController<>(this, "controller", 0, this::predicate);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(animationController);
    }

    private PlayState predicate(AnimationState<GatePortal> event){
        event.getController().setAnimation(DefaultAnimations.IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
