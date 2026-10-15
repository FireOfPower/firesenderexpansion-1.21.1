package net.fireofpower.firesenderexpansion.entities.spells.ChainGrapple;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.ender_chain.EnderChain;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.rapier.UnstableSummonedRapierEntity;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

//A version of the EnderChain that connects two entities rather than an entity and a position
public class EnderGrappleChain extends EnderChain implements AntiMagicSusceptible, IEntityWithComplexSpawn {
    public static final int VISUAL_WARMUP_TIME = 3;

    private float maxLength = 12;
    private float pullStrength = 0.35f;

    public EnderGrappleChain(EntityType<?> type, Level level) {
        super(type, level);
    }

    public EnderGrappleChain(Level level, Entity owner, Entity victim) {
        this(EntityRegistry.ENDER_GRAPPLE_CHAIN.get(), level);
        setOwner(owner);
        setVictim(victim);
        setPos(owner.position());
    }

    public float getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(float maxLength) {
        this.maxLength = maxLength;
    }

    public void setPullStrength(float pullStrength) {
        this.pullStrength = pullStrength;
    }

    public float getPullStrength() {
        return pullStrength;
    }

    @Override
    public void tick() {
        if(getOwner() == null || getOwner().isRemoved()){
            trueBreakChain();
            return;
        }
        if(getVictim() instanceof EnderChainHookEntity entity && entity.getChain() == null){
            entity.setChain(this);
        }
        //logic for restraint strength and pull strength
        if(getVictim() instanceof EnderChainHookEntity hook && !hook.isInGround()){
            //hook is traveling through air
            setRestraintStrength(0);
            setPullStrength(0);
        } else if (getVictim() instanceof EnderChainHookEntity hook && hook.isInGround()) {
            //lodged in block
            setRestraintStrength(0);
            setPullStrength(0.05f);
            if(getOwner().distanceTo(getVictim()) < 3){
                trueBreakChain();
            }
        }else if (!(getVictim() instanceof EnderChainHookEntity) && getVictim() != null){
            //stuck to entity
            setRestraintStrength(0.05f);
            setPullStrength(0);
            if(getOwner().distanceTo(getVictim()) < 3){
                trueBreakChain();
            }
        }

        super.tick();
        if(getVictim() != null && !(getVictim() instanceof EnderChainHookEntity) && getVictim().distanceToSqr(this) >= 12 * 12){
            applyRestraint(getVictim());
        }

        if(getOwner() instanceof ServerPlayer serverPlayer && serverPlayer.isCrouching()){
            trueBreakChain();
        }

        setPos(getOwner().position().add(0,getOwner().getBbHeight()*0.5,0));

        if (invulnerableTime > 0) {
            invulnerableTime--;
        }
        if (warmup < VISUAL_WARMUP_TIME) {
            warmup++;
        }
        if (!level().isClientSide) {
            Entity victim = getVictim();
            if (victim == null || victim.isRemoved() || victim.distanceToSqr(this) > maxLength * maxLength || getHealth() <= 0) {
                trueBreakChain();
                return;
            }
            applyPull(victim);
        }

        repositionParts();
    }

    public Vec3 getChainAnchor(Entity owner, float partialTick){
        return owner.getPosition(partialTick).add(0,owner.getBbHeight()*0.5,0);
    }

    private void repositionParts() {
        Entity victim = getVictim();
        Entity owner = getOwner();
        Vec3 start = owner != null ? owner.position().add(0,owner.getBbHeight() * 0.5, 0) : position();
        Vec3 end = victim != null ? victim.position().add(0, victim.getBbHeight() * 0.5, 0) : start.add(0, 1, 0);
        int totalLinks = getParts().length + 1;

        for (int i = 0; i < getParts().length; i++) {
            float t = (float) (i + 1) / totalLinks;
            Vec3 pos = start.lerp(end, t);
            getParts()[i].xo = getParts()[i].getX();
            getParts()[i].yo = getParts()[i].getY();
            getParts()[i].zo = getParts()[i].getZ();
            getParts()[i].xOld = getParts()[i].getX();
            getParts()[i].yOld = getParts()[i].getY();
            getParts()[i].zOld = getParts()[i].getZ();
            getParts()[i].setPos(pos);
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (invulnerableTime > 0) {
            return false;
        }
        if (level().isClientSide || isInvulnerableTo(source)) {
            return false;
        }
        if (DamageSources.isFriendlyFireBetween(source.getEntity(), getOwner())) {
            return false;
        }
        setHealth(getHealth() - amount);
        if (getHealth() <= 0) {
            trueBreakChain();
        } else {
            this.playSound(SoundEvents.CHAIN_STEP);
        }
        invulnerableTime = 10;
        return true;
    }

    public void breakChain() {
        //can't use this method since the superclass is going to call it if we exceed a certain distance
    }

    public void trueBreakChain(){
        if (!level().isClientSide) {
            playSound(SoundEvents.CHAIN_BREAK);
            if (getParts().length > 0) {
                int count = 15;
                Vec3 pos = position();
                Vec3 end = getParts()[getParts().length - 1].position();
                for (int i = 0; i < count; i++) {
                    Vec3 vec3 = pos.lerp(end, i / (float) count);
                    MagicManager.spawnParticles(level(), ParticleHelper.ENDER_SPARKS, vec3.x, vec3.y, vec3.z, 1, 0.05, 0.05, 0.05, 0.1, false);
                }
            }
            if(getVictim() instanceof EnderChainHookEntity entity){
                entity.discard();
            }
            this.discard();
        }
    }

    private void applyRestraint(Entity victim) {
        Vec3 displacement = victim.position().subtract(position());
        if (displacement.lengthSqr() < 1.0E-8) {
            return;
        }
        Vec3 deltaV = displacement.scale(-0.05); //restraint strength
        deltaV.multiply(deltaV);
        victim.setDeltaMovement(victim.getDeltaMovement().add(deltaV));
        victim.hurtMarked = true;
        if (victim.getDeltaMovement().y >= 0) {
            victim.resetFallDistance();
        }
    }

    private void applyPull(Entity victim) {
        Entity owner = getOwner();
        if(pullStrength > 0) {
            Vec3 displacement = owner.position().subtract(victim.position());
            if (displacement.lengthSqr() < 1.0E-8) {
                return;
            }
            Vec3 deltaV = displacement.scale(-pullStrength);
            deltaV.multiply(deltaV);
            owner.setDeltaMovement(owner.getDeltaMovement().add(deltaV));
            owner.hurtMarked = true;
            if (owner.getDeltaMovement().y >= 0) {
                owner.resetFallDistance();
            }
        }
    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
        trueBreakChain();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Max Length", maxLength);
        tag.putFloat("PullStrength", pullStrength);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.maxLength = tag.getFloat("Max Length");
        this.pullStrength = tag.getFloat("PullStrength");
    }
}