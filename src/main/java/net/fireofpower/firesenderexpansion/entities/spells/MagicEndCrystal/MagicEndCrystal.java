package net.fireofpower.firesenderexpansion.entities.spells.MagicEndCrystal;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.SummonedPolarBear;
import io.redspace.ironsspellbooks.entity.mobs.dead_king_boss.DeadKingBoss;
import io.redspace.ironsspellbooks.spells.ender.CounterspellSpell;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class MagicEndCrystal extends Mob implements IMagicSummon {
    private Entity owner;
    private static final EntityDataAccessor<Optional<UUID>> DATA_BEAM_TARGET = SynchedEntityData.defineId(MagicEndCrystal.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> DATA_SHOW_BOTTOM = SynchedEntityData.defineId(MagicEndCrystal.class, EntityDataSerializers.BOOLEAN);
    public int time;
    public final static int MAX_EFFECTIVE_DISTANCE = 32;

    public MagicEndCrystal(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
        this.time = this.random.nextInt(100000);
    }

    public MagicEndCrystal(Level level, double x, double y, double z) {
        this(EntityRegistry.MAGIC_END_CRYSTAL.get(), level);
        this.setPos(x,y,z);
        setShowBottom(false);
        if(level.isClientSide()){
            setOwner(owner);
        }
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder prepareAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 0.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.0)
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.FOLLOW_RANGE, 0.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 100.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0);
    }

    protected Entity.MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    protected void tickDeath() {
        if (!this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(RemovalReason.KILLED);
        }
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_BEAM_TARGET, Optional.empty());
        builder.define(DATA_SHOW_BOTTOM, false);
    }

    @Override
    public boolean isAlliedTo(Entity pEntity) {
        return super.isAlliedTo(pEntity) || this.isAlliedHelper(pEntity);
    }

    @Override
    public void onUnSummon() {
        if (!level().isClientSide) {
            MagicManager.spawnParticles(level(), ParticleTypes.POOF, getX(), getY(), getZ(), 25, .4, .8, .4, .03, false);
            setRemoved(RemovalReason.DISCARDED);
        }
    }

    @Override
    public void onRemovedFromLevel() {
        this.onRemovedHelper(this);
        super.onRemovedFromLevel();
    }

    public void setBeamTarget(Optional<UUID> beamTarget) {
        this.getEntityData().set(DATA_BEAM_TARGET, beamTarget);
    }

    @Nullable
    public Optional<UUID> getBeamTarget() {
        return (this.getEntityData().get(DATA_BEAM_TARGET));
    }

    public void setShowBottom(boolean showBottom) {
        this.getEntityData().set(DATA_SHOW_BOTTOM, showBottom);
    }

    public boolean showsBottom() {
        return (Boolean)this.getEntityData().get(DATA_SHOW_BOTTOM);
    }

    public boolean shouldRenderAtSqrDistance(double distance) {
        return super.shouldRenderAtSqrDistance(distance) || this.getBeamTarget().isPresent();
    }

    @Override
    protected @org.jetbrains.annotations.Nullable SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.GLASS_BREAK;
    }

    @Override
    protected @org.jetbrains.annotations.Nullable SoundEvent getDeathSound() {
        return SoundEvents.BEACON_DEACTIVATE;
    }

    @Override
    public void tick() {
        ++time;
        if(tickCount == 1 && !level().isClientSide()){
            for(int i = 0; i < 20; i++){
                MagicManager.spawnParticles(level(),ParticleTypes.END_ROD,position().x,position().y + i / 10.0,position().z,5,Math.random() - 0.5,0,Math.random() - 0.5,0.1,false);
            }
        }
        super.tick();
        this.checkInsideBlocks();
        this.handlePortal();
        if(getOwner() != SummonManager.getOwner(this)){
            setOwner(SummonManager.getOwner(this));
        }
        if(SummonManager.getOwner(this) instanceof LivingEntity l && l.isDeadOrDying()){
            this.remove(RemovalReason.KILLED);
        }
        if(!level().isClientSide) {
            if (getOwner() != null && getOwner().level().equals(level()) && distanceTo(getOwner()) <= MAX_EFFECTIVE_DISTANCE) {
                setBeamTarget(Optional.of(getOwner().getUUID()));
                if(tickCount % 20 == 0 && getOwner() instanceof LivingEntity living){
                    living.heal(1);
                }
            } else {
                setBeamTarget(Optional.empty());
            }
        }
    }

    @Override
    public void onDeathHelper() {
        IMagicSummon.super.onDeathHelper();
    }

    public Entity getOwner() {
        return owner;
    }

    public void setOwner(Entity owner) {
        this.owner = owner;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        //it's immune to damage sources that can just exist on the ground
        if (source == level().damageSources().lava() ||
                source == level().damageSources().onFire() ||
                source == level().damageSources().cactus() ||
                source == level().damageSources().campfire() ||
                source == level().damageSources().drown()) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
        IMagicSummon.super.onAntiMagic(playerMagicData);
        this.discard();
    }
}
