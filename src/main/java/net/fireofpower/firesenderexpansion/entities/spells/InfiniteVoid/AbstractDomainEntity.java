package net.fireofpower.firesenderexpansion.entities.spells.InfiniteVoid;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;

public abstract class AbstractDomainEntity extends Entity implements AntiMagicSusceptible, INBTSerializable<CompoundTag> {
    private static final EntityDataAccessor<Integer> RADIUS = SynchedEntityData.defineId(AbstractDomainEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> REFINEMENT = SynchedEntityData.defineId(AbstractDomainEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> OPEN  = SynchedEntityData.defineId(AbstractDomainEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TRANSPORTED = SynchedEntityData.defineId(AbstractDomainEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CLASHABLE  = SynchedEntityData.defineId(AbstractDomainEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Long> SPAWN_TIME = SynchedEntityData.defineId(AbstractDomainEntity.class, EntityDataSerializers.LONG);
    private static final Map<AbstractDomainEntity,ArrayList<AbstractDomainEntity>> clashingWithMap = new HashMap<>();
    private static final Map<AbstractDomainEntity, Entity> ownerMap = new HashMap<>();
    private int spawnAnimTime = Integer.MAX_VALUE; //please update this in your own code with setSpawnAnimTime()

    public AbstractDomainEntity(EntityType<? extends Entity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNoGravity(true);
    }

    public void onActivation(){
        if(!level().isClientSide) {
            setSpawnTime(level().getGameTime());
        }
        clashingWithMap.put(this,new ArrayList<>());
        level().getEntitiesOfClass(AbstractDomainEntity.class, new AABB(position().subtract(getRadius() / 2.0, getRadius() / 2.0, getRadius() / 2.0), this.position().add(getRadius() / 2.0, getRadius() / 2.0, getRadius() / 2.0))).stream()
                .forEach(e -> {
                            if(e.distanceTo(this) < getRadius() && !Objects.equals(e,this)){
                                //clash checks!
                                if(!e.getClashable()){
                                    //do nothing
                                }else if(e.getOwner() != null && getOwner() != null && e.getOwner().equals(getOwner())){
                                    System.out.println("SAME OWNER - NO CLASH");
                                }else if((double) e.getRefinement() / getRefinement() >= 1.5){
                                    System.out.println("REFINEMENT DIFFERENCE TOO GREAT - NO CLASH");
                                    destroyDomain();
                                }else if ((double) getRefinement() / e.getRefinement() >= 1.5){
                                    System.out.println("REFINEMENT DIFFERENCE TOO GREAT - NO CLASH");
                                    e.destroyDomain();
                                }else{
                                    System.out.println("DOMAIN CLASH DETECTED - Our Domain has Refinement of " + getRefinement());
                                    if(getClashingWith() != null && e.getClashingWith() != null) {
                                        if (!getClashingWith().contains(e)) {
                                            clashingWithMap.get(this).add(e);
                                        }
                                        if (!e.getClashingWith().contains(this)) {
                                            e.getClashingWith().add(this);
                                        }
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
        return !isOpen() && !getTransported() && !isClashing() && tickCount > this.getSpawnAnimTime();
    }

    public void handleDomainClash(ArrayList<AbstractDomainEntity> opposingDomains){
    }

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
            cache.addRegionTicket(TicketType.FORCED, Utils.getChunkPos(new BlockPos((int) this.position().x, (int) this.position().y, (int) this.position().z)), 20, Utils.getChunkPos(new BlockPos((int) this.position().x, (int) this.position().y, (int) this.position().z)), true);
        }
        if(getOwner() instanceof LivingEntity living && living.isDeadOrDying()){
            destroyDomain();
        }
        if(!getClashingWith().isEmpty()) {
            for (AbstractDomainEntity e : clashingWithMap.get(this)) {
                if (e != null) {
                    handleDomainClash(clashingWithMap.get(this));
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

    public void setOwner(Entity owner){
        ownerMap.put(this,owner);
    }

    public Entity getOwner(){
        return ownerMap.get(this);
    }

    public void setSpawnAnimTime(int spawnAnimTime) {
        this.spawnAnimTime = spawnAnimTime;
    }

    public int getSpawnAnimTime() {
        return spawnAnimTime;
    }

    public Long getSpawnTime() {
        return this.entityData.get(SPAWN_TIME);
    }

    public void setSpawnTime(long spawnTime){
        this.entityData.set(SPAWN_TIME,spawnTime);
    }

    public void setClashable(boolean clashable){
        this.entityData.set(CLASHABLE,clashable);
    }

    public boolean getClashable(){
        return this.entityData.get(CLASHABLE);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setRadius(tag.getInt("Radius"));
        this.setRefinement(tag.getInt("Refinement"));
        this.setOpen(tag.getBoolean("Open"));
        this.setTransported(tag.getBoolean("Transported"));
        this.setSpawnTime(tag.getLong("Spawn Time"));
        this.setClashable(tag.getBoolean("Clashable"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Radius",this.getRadius());
        tag.putInt("Refinement",this.getRefinement());
        tag.putBoolean("Open",this.isOpen());
        tag.putBoolean("Transported",this.getTransported());
        tag.putLong("Spawn Time",this.getSpawnTime());
        tag.putBoolean("Clashable",this.getClashable());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(RADIUS, 0);
        builder.define(REFINEMENT,0);
        builder.define(OPEN,false);
        builder.define(TRANSPORTED,false);
        builder.define(SPAWN_TIME,Long.MIN_VALUE);
        builder.define(CLASHABLE,true);
    }

    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putLong("Spawn Time",getSpawnTime());
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        setSpawnTime(nbt.getLong("Spawn Time"));
    }
}