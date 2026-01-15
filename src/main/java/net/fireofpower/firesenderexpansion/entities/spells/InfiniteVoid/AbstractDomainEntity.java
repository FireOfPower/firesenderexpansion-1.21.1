package net.fireofpower.firesenderexpansion.entities.spells.InfiniteVoid;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import net.fireofpower.firesenderexpansion.effects.InfiniteVoidEffect;
import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.*;

public abstract class AbstractDomainEntity extends Entity implements AntiMagicSusceptible{
    private static final EntityDataAccessor<Integer> RADIUS;
    private static final EntityDataAccessor<Integer> REFINEMENT;
    private static final EntityDataAccessor<Boolean> OPEN;
    private static final EntityDataAccessor<Boolean> TRANSPORTED;
    private static final EntityDataAccessor<Boolean> FINISHED_SPAWN_ANIM;
    private static final Map<AbstractDomainEntity,ArrayList<AbstractDomainEntity>> clashingWithMap = new HashMap<>();
    private static final Map<AbstractDomainEntity, Entity> ownerMap = new HashMap<>();


    static {
        OPEN = SynchedEntityData.defineId(AbstractDomainEntity.class, EntityDataSerializers.BOOLEAN);
        TRANSPORTED = SynchedEntityData.defineId(AbstractDomainEntity.class, EntityDataSerializers.BOOLEAN);
        FINISHED_SPAWN_ANIM = SynchedEntityData.defineId(AbstractDomainEntity.class, EntityDataSerializers.BOOLEAN);
        RADIUS = SynchedEntityData.defineId(AbstractDomainEntity.class, EntityDataSerializers.INT);
        REFINEMENT = SynchedEntityData.defineId(AbstractDomainEntity.class, EntityDataSerializers.INT);
    }

    public AbstractDomainEntity(EntityType<? extends Entity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNoGravity(true);
    }

    public void onActivation(){
        clashingWithMap.put(this,new ArrayList<>());
        level().getEntitiesOfClass(AbstractDomainEntity.class, new AABB(position().subtract(getRadius() / 2.0, getRadius() / 2.0, getRadius() / 2.0), this.position().add(getRadius() / 2.0, getRadius() / 2.0, getRadius() / 2.0))).stream()
                .forEach(e -> {
                            if(e.distanceTo(this) < getRadius() && !Objects.equals(e,this)){
                                if(e.getRefinement() > getRefinement()){
                                    destroyDomain();
                                }else if (e.getRefinement() < getRefinement()){
                                    e.destroyDomain();
                                }else{
                                    if(!getClashingWith().contains(e)) {
                                        clashingWithMap.get(this).add(e);
                                    }
                                    if(!e.getClashingWith().contains(this)) {
                                        e.getClashingWith().add(this);
                                    }
                                }
                            }
                        }
                );
    }

    //TODO: Is there a nice way to get this method to be used instead of discard() so that people can have breaking domain animations?
    public void destroyDomain(){
        discard();
    }

    private boolean canTransport(){
        return !isOpen() && !getTransported() && !isClashing() && getFinishedSpawnAnim();
    }

    public void handleDomainClash(AbstractDomainEntity opposingDomain){}

    public void targetSureHit(){
        level().getEntitiesOfClass(Entity.class, new AABB(position().subtract(getRadius() / 2.0, getRadius() / 2.0, getRadius() / 2.0), position().add(getRadius() / 2.0, getRadius() / 2.0, getRadius() / 2.0))).stream()
                .forEach(e -> {
                            if(e.distanceTo(this) < getRadius() && canTarget(e)){
                                handleSureHit(e);
                            }
                        }
                );
    }

    public void handleSureHit(Entity e){

    }

    public boolean isClashing(){
        return !clashingWithMap.get(this).isEmpty();
    }

    public boolean canTarget(Entity e){
        boolean shareOwner = false;
        if(e instanceof TamableAnimal tame){
            shareOwner = Objects.equals(tame.getOwner(), ((TamableAnimal) e).getOwner());
        }
        if(e instanceof Projectile proj){
            shareOwner = Objects.equals(proj.getOwner(), e);
        }
        return !(Objects.equals(e, this) || Objects.equals(e,getOwner()) || shareOwner);
    }

    public ArrayList<AbstractDomainEntity> getClashingWith() {
        return clashingWithMap.get(this);
    }

    @Override
    public void tick() {
        if(tickCount == 1) {
            onActivation();
        }
        if(!level().isClientSide) {
            ServerChunkCache cache = getServer().getLevel(this.level().dimension()).getChunkSource();
            cache.addRegionTicket(TicketType.FORCED, Utils.getChunkPos(new BlockPos((int) this.position().x, (int) this.position().y, (int) this.position().z)), 9, Utils.getChunkPos(new BlockPos((int) this.position().x, (int) this.position().y, (int) this.position().z)), true);
        }
        if(getOwner() instanceof LivingEntity living && living.isDeadOrDying()){
            destroyDomain();
        }
        if(!getClashingWith().isEmpty()) {
            for (AbstractDomainEntity e : clashingWithMap.get(this)) {
                if (e != null) {
                    handleDomainClash(e);
                } else {
                    clashingWithMap.get(this).remove(e);
                }
            }
        }
        if(canTransport()){
            handleTransportation();
        }
        targetSureHit();
        super.tick();
    }

    public void handleTransportation() {
        setTransported(true);
    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
    }

    public int getRefinement()
    {
        return this.entityData.get(REFINEMENT);
    }

    public void setRefinement(int refinement)
    {
        this.entityData.set(REFINEMENT, refinement);
    }

    public int getRadius()
    {
        return this.entityData.get(RADIUS);
    }

    public void setRadius(int radius)
    {
        this.entityData.set(RADIUS, radius);
    }

    public boolean isOpen()
    {
        return this.entityData.get(OPEN);
    }

    public void setOpen(boolean open)
    {
        this.entityData.set(OPEN, open);
    }

    public boolean getTransported()
    {
        return this.entityData.get(TRANSPORTED);
    }

    public void setTransported(boolean transported)
    {
        this.entityData.set(AbstractDomainEntity.TRANSPORTED, transported);
    }

    public boolean getFinishedSpawnAnim()
    {
        return this.entityData.get(FINISHED_SPAWN_ANIM);
    }

    public void setFinishedSpawnAnim(boolean finishedSpawnAnim)
    {
        this.entityData.set(AbstractDomainEntity.FINISHED_SPAWN_ANIM, finishedSpawnAnim);
    }

    public void setOwner(Entity owner){
        ownerMap.put(this,owner);
    }

    public Entity getOwner(){
        return ownerMap.get(this);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setRadius(tag.getInt("Radius"));
        this.setRefinement(tag.getInt("Refinement"));
        this.setOpen(tag.getBoolean("Open"));
        this.setTransported(tag.getBoolean("Transported"));
        this.setFinishedSpawnAnim(tag.getBoolean("Finished Spawn Anim"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Radius",this.getRadius());
        tag.putInt("Refinement",this.getRefinement());
        tag.putBoolean("Open",this.isOpen());
        tag.putBoolean("Transported",this.getTransported());
        tag.putBoolean("Finished Spawn Anim",this.getFinishedSpawnAnim());
    }
}