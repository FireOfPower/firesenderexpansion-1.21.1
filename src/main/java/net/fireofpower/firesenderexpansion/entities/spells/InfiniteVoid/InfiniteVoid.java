package net.fireofpower.firesenderexpansion.entities.spells.InfiniteVoid;

import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.util.INBTSerializable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;


public class InfiniteVoid extends AbstractDomainEntity implements GeoEntity, AntiMagicSusceptible, INBTSerializable<CompoundTag> {
    private int duration = 20; //in seconds
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    public InfiniteVoid(Level level, Entity shooter) {
        this((EntityType) EntityRegistry.INFINITE_VOID.get(), level);
        this.setNoGravity(true);
        this.canUsePortal(false);
        this.setSpawnAnimTime(40);
        this.setOwner(shooter);
        this.setOpen(false);
    }

    public InfiniteVoid(EntityType<InfiniteVoid> infiniteVoidEntityType, Level level) {
        super(infiniteVoidEntityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        //System.out.println("for entity " + this + " we have a tickCount of " + tickCount);
        if(tickCount > getDuration() * 20){
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
            if (targets.get(i) instanceof LivingEntity target && !target.getType().equals(EntityRegistry.INFINITE_VOID.get())) {
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
        long time = level().getGameTime() - getSpawnTime();
        if(time < 80){
            event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("misc.open"));
        } else if (time < getDuration() * 20L){
            event.getController().setAnimation(DefaultAnimations.IDLE);
        }else{
            event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("misc.close"));
        }

        return PlayState.CONTINUE;
    }
}


