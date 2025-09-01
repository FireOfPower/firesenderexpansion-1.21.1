package net.fireofpower.firesenderexpansion.entities.spells.GateOfEnder;

import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.UnstableWeaponEntity;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.claymore.UnstableSummonedClaymoreEntity;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.rapier.UnstableSummonedRapierEntity;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.sword.UnstableSummonedSwordEntity;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
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
    private UnstableWeaponEntity weapon;
    public GatePortal(Level level, LivingEntity shooter) {
        this((EntityType) EntityRegistry.GATE_PORTAL.get(), level);
        this.setOwner(shooter);
        this.setNoGravity(true);
    }

    public GatePortal(EntityType<GatePortal> gatePortalEntityType, Level level) {
        super(gatePortalEntityType,level);
    }

    public void shootSword(){
        Entity owner = this.getOwner();
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
        this.weapon = weapon;
        this.setPos(origin.subtract(0.0,this.getBbHeight()/2,0.0));
        weapon.setPos(origin.add(0,weapon.getBbHeight()/2,0));
        weapon.setDamage(this.getDamage());
        weapon.setSpeed(0.01f);
        weapon.shoot(owner.getLookAngle());
        this.level().playSound((Player)null, origin.x, origin.y, origin.z, SoundRegistry.ECHOING_STRIKE, SoundSource.PLAYERS, 0.1F, 1.0F);
        this.level().addFreshEntity(weapon);
    }

    @Override
    public void travel() {
        Vec3 motion = this.getLookAngle();
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        //System.out.println("Set dir for " + this.toString() + " to " + Mth.wrapDegrees(xRot) + ", " + Mth.wrapDegrees(yRot) + ", Also, my look angle is " + this.getLookAngle());
        this.setXRot(xRot);
        this.setYRot(yRot);
    }

    public void shootSword(LivingEntity target, Vec3 spawnPos){
        Entity owner = this.getOwner();
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
        this.weapon = weapon;
        this.setPos(origin.subtract(0.0,this.getBbHeight()/2,0.0));
        weapon.setPos(origin.add(0,weapon.getBbHeight()/2,0));
        weapon.setDamage(this.getDamage());
        weapon.setSpeed(0.01f);
        Vec3 lookAngle = target.position().subtract(spawnPos).normalize().multiply(360,360,360);
        weapon.shoot(lookAngle);
        this.level().playSound((Player)null, origin.x, origin.y, origin.z, SoundRegistry.ECHOING_STRIKE, SoundSource.PLAYERS, 0.1F, 1.0F);
        this.level().addFreshEntity(weapon);
    }

    @Override
    public void tick() {
        if(tickCount > 10 && weapon != null && weapon.getSpeed() < 2){
            weapon.setSpeed(2f);
            weapon.setDeltaMovement(weapon.getDeltaMovement().normalize().scale(weapon.getSpeed()));
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
