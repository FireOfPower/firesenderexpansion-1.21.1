package net.fireofpower.firesenderexpansion.entities.spells.InfiniteVoid;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.acetheeldritchking.aces_spell_utils.entity.spells.AbstractDomainEntity;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;


public class InfiniteVoid extends AbstractDomainEntity implements GeoEntity, AntiMagicSusceptible {
    private int duration; //in seconds
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final int deathAnimLength = 60; //in ticks, should be time UNTIL YOU WANT THEM TO TRANSPORT BACK
    private boolean shouldPlayCloseAnim = false;


    public InfiniteVoid(Level level, LivingEntity shooter) {
        this((EntityType) EntityRegistry.INFINITE_VOID.get(), level);
        this.setOwner(shooter);
        this.setNoGravity(true);
    }

    public InfiniteVoid(EntityType<InfiniteVoid> infiniteVoidEntityType, Level level) {
        super(infiniteVoidEntityType,level);
    }

    @Override
    public void tick() {
        super.tick();
        if(tickCount > getDuration() * 20 - deathAnimLength){
            shouldPlayCloseAnim = true;
        }
        if(tickCount > getDuration() * 20 /* duration is measured in seconds, not ticks */){
            destroyDomain();
        }
    }

    @Override
    public void handleTransportation() {
        super.handleTransportation();
        Entity entity = getOwner();
        if(entity instanceof LivingEntity living){
            living.addEffect(new MobEffectInstance(EffectRegistry.ASCENDED_CASTER_EFFECT, (duration - 4) * 20, 0, false, false, true));
            living.addEffect(new MobEffectInstance(EffectRegistry.ANCHORED_EFFECT, (duration - 4) * 20, 0, false, false, true));
            living.addEffect(new MobEffectInstance(MobEffectRegistry.ANTIGRAVITY, (duration - 4) * 20, 0, false, false, true));
            living.addEffect(new MobEffectInstance(EffectRegistry.INFINITE_VOID_EFFECT, (duration - 4) * 20, 0, false, false, true));
        }
        List<Entity> targets = entity.level().getEntities(entity, new AABB(entity.getX() - getRadius(), entity.getY() - getRadius(), entity.getZ() - getRadius(), entity.getX() + getRadius(), entity.getY() + getRadius(), entity.getZ() + getRadius()));
        for (int i = 0; i < targets.size(); i++) {
            if (targets.get(i) instanceof LivingEntity target) {
                target.addEffect(new MobEffectInstance(EffectRegistry.ANCHORED_EFFECT, (duration - 4) * 20, 0, false, false, true));
                target.addEffect(new MobEffectInstance(MobEffectRegistry.ANTIGRAVITY, (duration - 4) * 20, 0, false, false, true));
                target.addEffect(new MobEffectInstance(EffectRegistry.INFINITE_VOID_EFFECT, (duration - 4) * 20, 0, false, false, true));
            }
        }
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
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
        if(tickCount < 20){
            event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("misc.open"));
        } else if (!shouldPlayCloseAnim) {
            if(tickCount == 20){
                setFinishedSpawnAnim(true);
            }
            event.getController().setAnimation(DefaultAnimations.IDLE);
        }else{
            int beginDeathTime = tickCount;
            if(tickCount < beginDeathTime + 20) {
                event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("misc.close"));
            } else if (tickCount < beginDeathTime + 100) {
                event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("misc.open"));
            } else if (tickCount < beginDeathTime + 120) {
                event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("misc.close"));
            }
        }

        return PlayState.CONTINUE;
    }
}


