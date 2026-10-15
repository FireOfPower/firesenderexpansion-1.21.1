package net.fireofpower.firesenderexpansion.entities.spells.ChainGrapple;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.spells.BinaryStars.BinaryStarEntity;
import net.fireofpower.firesenderexpansion.entities.spells.HollowCrystal.HollowCrystal;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EnderChainHookEntity extends AbstractMagicProjectile {

    @Nullable
    private UUID chainUUID;
    @Nullable
    private EnderGrappleChain cachedChain;

    private boolean inGround;
    private boolean markedForDiscard = false;

    public EnderChainHookEntity(Level level, LivingEntity owner){
        this((EntityType) EntityRegistry.ENDER_CHAIN_HOOK.get(), level);
        this.setOwner(owner);
        this.setNoGravity(true);
    }

    public EnderChainHookEntity(EntityType<? extends EnderChainHookEntity> EnderChainHookEntityEntityType, Level level) {
        super(EnderChainHookEntityEntityType,level);
    }

    public void setChain(@Nullable EnderGrappleChain chain) {
        if (chain != null) {
            this.chainUUID = chain.getUUID();
            this.cachedChain = chain;
        }
    }

    @Nullable
    public EnderGrappleChain getChain() {
        if (this.cachedChain != null && !this.cachedChain.isRemoved()) {
            return this.cachedChain;
        } else if (this.chainUUID != null && this.level() instanceof ServerLevel serverLevel) {
            if(serverLevel.getEntity(this.chainUUID) instanceof EnderGrappleChain foundChain) {
                this.cachedChain = foundChain;
            }
            return this.cachedChain;
        }
        return null;
    }

    @Override
    public void trailParticles() {

    }

    public boolean isInGround() {
        return inGround;
    }

    public void setInGround(boolean inGround) {
        this.inGround = inGround;
    }

    @Override
    public void impactParticles(double x, double y, double z) {

    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        setDeltaMovement(Vec3.ZERO);
        setInGround(true);
    }

    @Override
    public void tick() {
        if(markedForDiscard){
            this.discard();
        }
        super.tick();
        List<LivingEntity> nearbyEntities = level().getEntitiesOfClass(LivingEntity.class,this.getBoundingBox().inflate(1));
        if(getOwner() != null) {
            nearbyEntities.remove(getOwner());
        }
        nearbyEntities.remove(this);
        if(!nearbyEntities.isEmpty()) {
            LivingEntity hitEntity = nearbyEntities.stream().filter(e -> e instanceof LivingEntity).toList().getFirst();
            EnderGrappleChain chain = getChain();
            if(chain != null && hitEntity != chain.getOwner()) {
                chain.setVictim(hitEntity);
            }
            markedForDiscard = true;
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);
    }

    @Override
    public float getSpeed() {
        return 2f;
    }

    @Override
    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.of(SoundRegistry.CHARGE_CHAINS);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (chainUUID != null) {
            tag.putUUID("Chain", chainUUID);
        }
        tag.putBoolean("grounded", inGround);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("Chain")) {
            this.chainUUID = tag.getUUID("Chain");
            this.cachedChain = null;
        }
        this.inGround = tag.getBoolean("grounded");
    }
}
