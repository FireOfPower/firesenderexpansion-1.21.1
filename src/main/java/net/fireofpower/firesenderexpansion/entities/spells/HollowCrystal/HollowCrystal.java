package net.fireofpower.firesenderexpansion.entities.spells.HollowCrystal;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.stream.Collectors;

public class HollowCrystal extends AbstractMagicProjectile implements GeoEntity, AntiMagicSusceptible {
    private int timeAlive = -1;
    private RawAnimation animationToPlay = null;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private List<Entity> victims;
    private static final EntityDataAccessor<Boolean> DATA_IS_PLAYING_BREAK_ANIM = SynchedEntityData.defineId(HollowCrystal.class, EntityDataSerializers.BOOLEAN);



    public HollowCrystal(Level level, LivingEntity shooter) {
        this((EntityType) EntityRegistry.HOLLOW_CRYSTAL.get(), level);
        this.setOwner(shooter);
        this.setNoGravity(true);
        victims = new ArrayList<>();
    }

    public HollowCrystal(EntityType<HollowCrystal> hollowCrystalEntityType, Level level) {
        super(hollowCrystalEntityType,level);
    }

    @Override
    public void trailParticles() {
    }

    @Override
    public void tick() {
        if(!level().isClientSide()) {
            this.level().getEntitiesOfClass(ServerPlayer.class,this.getBoundingBox().inflate(3)).stream().forEach(e -> {
                this.level().playSeededSound(null,this.getX(),this.getY(),this.getZ(),SoundRegistry.BLACK_HOLE_LOOP.get(),SoundSource.PLAYERS,3f,1f,1239831800);
            });
            this.level().getEntitiesOfClass(Projectile.class,
                            this.getBoundingBox()
                                    .inflate(1))
                    .stream()
                    .filter(proj -> proj.distanceTo(this) < 5)
                    .forEach(e -> {
                        if(Utils.shouldBreakHollowCrystal(e)){
                            e.discard();
                            triggerBreakAnimation();
                            this.setDeltaMovement(0,0,0);
                            this.timeAlive = 60;
                        }
                    });
        }
        if(this.timeAlive == 0 || tickCount > 600){
            //final move
            this.level().getEntitiesOfClass(LivingEntity.class,
                            this.getBoundingBox()
                                    .inflate(15))
                    .stream()
                    .forEach(e -> {
                        if(canHitEntity(e)){
                            damageEntity(e);
                        }
                    });
            discardThis();
        }
        if(timeAlive > 0) {
            timeAlive--;
            for(int i = 0; i < 10; i++) {
                this.level().addParticle(ParticleTypes.END_ROD, particleRangeX(0), particleRangeY(0), particleRangeZ(0), Math.random() -0.5, Math.random() -0.5, Math.random() - 0.5);
            }
        }else{
            this.level().addParticle(ParticleHelper.PORTAL_FRAME, particleRangeX(5), particleRangeY(5), particleRangeZ(5), 0, 0, 0);
        }

        if (!this.level().isClientSide) {
            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitresult.getType() == HitResult.Type.BLOCK) {
                onHitBlock((BlockHitResult) hitresult);
                impactParticles(this.position().x,this.position().y,this.position().z);
                this.level().playSound((Player)null, this.position().x, this.position().y, this.position().z, SoundRegistry.EARTHQUAKE_IMPACT, SoundSource.PLAYERS, 2.0F, 1.0F);
            }
            if(victims !=null) {
                for (Entity entity : this.level().getEntities(this, this.getBoundingBox()).stream().filter(target -> canHitEntity(target) && !victims.contains(target)).collect(Collectors.toSet())) {
                    damageEntity(entity);
                }
            }
        }
        super.tick();
    }

    private void damageEntity(Entity entity) {
        if (!victims.contains(entity)) {
            DamageSources.applyDamage(entity, damage, SpellRegistries.HOLLOW_CRYSTAL.get().getDamageSource(this, getOwner()));
            victims.add(entity);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {

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
    protected void onHit(HitResult hitresult) {
        HitResult.Type hitresult$type = hitresult.getType();
        if (hitresult$type == HitResult.Type.ENTITY) {
            EntityHitResult entityhitresult = (EntityHitResult) hitresult;
            Entity entity = entityhitresult.getEntity();
            if (entity.getType().is(EntityTypeTags.REDIRECTABLE_PROJECTILE) && entity instanceof Projectile) {
                Projectile projectile = (Projectile) entity;
                projectile.deflect(ProjectileDeflection.AIM_DEFLECT, this.getOwner(), this.getOwner(), true);
            }

            this.onHitEntity(entityhitresult);
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, hitresult.getLocation(), GameEvent.Context.of(this, (BlockState) null));
        }
        else if (hitresult$type == HitResult.Type.BLOCK) {
            BlockHitResult blockhitresult = (BlockHitResult)hitresult;
            this.onHitBlock(blockhitresult);
            BlockPos blockpos = blockhitresult.getBlockPos();
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockpos, GameEvent.Context.of(this, this.level().getBlockState(blockpos)));
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private PlayState predicate(AnimationState<HollowCrystal> event) {
        if (!isPlayingBreakAnimation())
        {
            if (this.animationToPlay == null)
            {
                event.getController().setAnimation(DefaultAnimations.IDLE);
                return PlayState.CONTINUE;
            }
        }
        else
        {
            event.getController().setAnimation(DefaultAnimations.DIE);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    public boolean isPlayingBreakAnimation()
    {
        return entityData.get(DATA_IS_PLAYING_BREAK_ANIM);
    }

    public void triggerBreakAnimation()
    {
        entityData.set(DATA_IS_PLAYING_BREAK_ANIM, true);
    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_IS_PLAYING_BREAK_ANIM, false);
    }
}
