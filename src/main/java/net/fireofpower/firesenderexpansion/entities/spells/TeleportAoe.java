package net.fireofpower.firesenderexpansion.entities.spells;

import io.redspace.ironsspellbooks.api.events.SpellTeleportEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.fireofpower.firesenderexpansion.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@EventBusSubscriber
public class TeleportAoe extends AoeEntity implements AntiMagicSusceptible {
    private static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.defineId(TeleportAoe.class, EntityDataSerializers.INT);
    private float tpRadius = 0.5f;
    private final List<LivingEntity> trackedTargets = new ArrayList<>();

    public TeleportAoe(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

    }

    public TeleportAoe(Level level) {
        this(EntityRegistry.TELEPORT_AREA.get(), level);
    }

    @Override
    public void tick() {

        //get everyone sorta nearby (it's a square instead of a circle with radius dimensions)
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(this.getX() - getRadius(), this.getY() - getRadius(), this.getZ() - getRadius(), this.getX() + getRadius(), this.getY() + getRadius(), this.getZ() + getRadius()));
        for(int i = 0; i < targets.size(); i++) {
            //update the trackedTargets list
            Vec3 distFromCircleCenter = new Vec3((float) (targets.get(i).position().x - this.position().x), 0, (float) (targets.get(i).position().z - this.position().z));
            //if we're not already tracking them, then start tracking them and apply the shader
            if(!trackedTargets.contains(targets.get(i)) && distFromCircleCenter.horizontalDistance() < getRadius() && !Objects.equals(targets.get(i),getOwner()) && !DamageSources.isFriendlyFireBetween(getOwner(),targets.get(i)) && !(targets.get(i) instanceof ServerPlayer serverPlayer && (serverPlayer.isSpectator() || serverPlayer.isCreative()))){
                trackedTargets.add(targets.get(i));
                level().playSound((Player) null, targets.get(i).position().x, targets.get(i).position().y, targets.get(i).position().z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 2.0F, 1.0F);
            }
        }

        for(int i = 0; i < trackedTargets.size(); i++){
            LivingEntity tracked = trackedTargets.get(i);
            Vec3 distFromCircleCenter = new Vec3((float) (tracked.position().x - this.position().x), 0, (float) (tracked.position().z - this.position().z));
            if(distFromCircleCenter.horizontalDistance() > getRadius()) {
                if (!tracked.getType().is(ModTags.DISPLACEMENT_CAGE_IMMUNE) || tracked instanceof ServerPlayer serverPlayer && !(serverPlayer.isCreative() || serverPlayer.isSpectator()) && !serverPlayer.getType().is(ModTags.DISPLACEMENT_CAGE_IMMUNE)) {
                    //do the teleporty stuff
                    Vec3 dest = position().add(position().subtract(tracked.position()).normalize().multiply(getRadius() * 0.99,0,getRadius() * 0.99)).subtract(0,position().subtract(tracked.position()).y,0);
                    BlockPos output = new BlockPos((int) Math.round(dest.x), (int) Math.round(dest.y), (int) Math.round(dest.z));
                    while(!level().isEmptyBlock(output)){
                        dest = dest.add(0,1,0);
                        output = output.offset(0,1,0);
                    }
                    if(!Utils.handleSpellTeleport(SpellRegistries.DISPLACEMENT_CAGE.get(), tracked, dest)) {
                        if (trackedTargets.contains(tracked)){
                            trackedTargets.remove(tracked);
                        }
                    }
                }
            }
        }

        if(tickCount > getDuration()){
            for(int i = 0; i < trackedTargets.size(); i++){
                trackedTargets.remove(i);
                i--;
            }
            discard();
        }
        super.tick();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void allowFreeTeleportation(EntityTeleportEvent event) {
        TeleportAoe aoe = new TeleportAoe(event.getEntity().level());
        List<TeleportAoe> nearby = event.getEntity().level().getEntitiesOfClass(TeleportAoe.class,new AABB(new Vec3(event.getEntity().position().subtract(aoe.getRadius(),aoe.getRadius(),aoe.getRadius()).toVector3f()), new Vec3(event.getEntity().position().add(aoe.getRadius(),aoe.getRadius(),aoe.getRadius()).toVector3f())));
        for(int i = 0; i < nearby.size(); i++){
            nearby.get(i).trackedTargets.remove(event.getEntity());
        }
    }

    @Override
    public void applyEffect(LivingEntity livingEntity) {

    }

    @Override
    public float getParticleCount() {
        return 0f;
    }

    @Override
    public Optional<ParticleOptions> getParticle() {
        return Optional.empty();
    }

    @Override
    protected float getParticleSpeedModifier() {
        return 0f;
    }

    @Override
    public void onAntiMagic(MagicData magicData) {
        for(int i = 0; i < trackedTargets.size(); i++){
            trackedTargets.remove(i);
            i--;
        }
        discard();
    }

    @Override
    public float getRadius() {
        return 6;
    }

    @Override
    public int getDuration() {
        return this.entityData.get(DURATION).intValue();
    }

    public void setDuration(int duration) {
        this.entityData.set(DURATION,duration);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DURATION,0);
    }
}
