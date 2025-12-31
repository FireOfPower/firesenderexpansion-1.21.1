package net.fireofpower.firesenderexpansion.entities.spells;

import io.redspace.ironsspellbooks.api.events.SpellTeleportEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import net.acetheeldritchking.aces_spell_utils.network.AddShaderEffectPacket;
import net.acetheeldritchking.aces_spell_utils.network.RemoveShaderEffectPacket;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.fireofpower.firesenderexpansion.util.ModTags;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TeleportAoe extends AoeEntity implements AntiMagicSusceptible {
    private float radius;
    private int duration;
    private float tpRadius = 0.5f;
    private List<ServerPlayer> trackedShaderTargets = new ArrayList<>();

    public TeleportAoe(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

    }

    public TeleportAoe(Level level) {
        this(EntityRegistry.TELEPORT_AREA.get(), level);
    }

    @Override
    public void tick() {
        this.level().getEntitiesOfClass(LivingEntity.class,
                        this.getBoundingBox()
                                .inflate(1))
                .stream()
                .filter(liv -> liv.distanceTo(this) > getRadius() - tpRadius && liv.distanceTo(this) < getRadius() + tpRadius &&
                        !Objects.equals(liv, this.getOwner()))
                .forEach(e -> {
                    if(!e.getType().is(ModTags.DISPLACEMENT_CAGE_IMMUNE)) {
                        //do the teleporty stuff
                        Vec3 distFromCircleCenter = new Vec3((float) (e.position().x - this.position().x), 0, (float) (e.position().z - this.position().z));
                        NeoForge.EVENT_BUS.post(new SpellTeleportEvent(SpellRegistries.DISPLACEMENT_CAGE.get(), e, e.position().x, e.position().y, e.position().z));
                        Vec3 dest = e.position().subtract(new Vec3(distFromCircleCenter.x, 0, distFromCircleCenter.z).multiply(1.75, 1, 1.75));
                        Utils.handleSpellTeleport(SpellRegistries.DISPLACEMENT_CAGE.get(), e, dest);
                    }
                });

        //get everyone sorta nearby (it's a square instead of a circle with radius dimensions)
        List<ServerPlayer> targets = this.level().getEntitiesOfClass(ServerPlayer.class, new AABB(this.getX() - getRadius(), this.getY() - getRadius(), this.getZ() - getRadius(), this.getX() + getRadius(), this.getY() + getRadius(), this.getZ() + getRadius()));
        for(int i = 0; i < targets.size(); i++) {
            //if we're not already tracking them, then start tracking them and apply the shader
            if(!trackedShaderTargets.contains(targets.get(i))){
                trackedShaderTargets.add(targets.get(i));
                if(distanceTo(targets.get(i)) < getRadius()){
                    PacketDistributor.sendToPlayer(trackedShaderTargets.get(i), new AddShaderEffectPacket(FiresEnderExpansion.MODID, "shaders/pink_shader.json"));
                }
            }
        }
        //stop tracking people that leave our range
        for(int i = 0; i < trackedShaderTargets.size(); i++){
            if(distanceTo(trackedShaderTargets.get(i)) > getRadius()){
                PacketDistributor.sendToPlayer(trackedShaderTargets.get(i), new RemoveShaderEffectPacket());
                trackedShaderTargets.remove(i);
                i--;
            }
        }
        if(tickCount > duration){
            //when we are done, remove the shader from everyone who's being tracked
            for(int i = 0; i < trackedShaderTargets.size(); i++){
                PacketDistributor.sendToPlayer(trackedShaderTargets.get(i), new RemoveShaderEffectPacket());
                trackedShaderTargets.remove(i);
                i--;
            }
            discard();
        }
        super.tick();
    }

    @Override
    protected boolean canHitEntity(Entity pTarget) {
        return !pTarget.isSpectator() && pTarget.isAlive() && pTarget.isPickable();
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
        discard();
    }

    @Override
    public float getRadius() {
        return radius;
    }

    @Override
    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
