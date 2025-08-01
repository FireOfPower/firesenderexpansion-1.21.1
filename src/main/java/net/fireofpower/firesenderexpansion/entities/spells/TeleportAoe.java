package net.fireofpower.firesenderexpansion.entities.spells;

import io.redspace.ironsspellbooks.api.events.SpellTeleportEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.fireofpower.firesenderexpansion.util.ModTags;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class TeleportAoe extends AoeEntity implements AntiMagicSusceptible {
    private float radius;
    private int duration;
    private float tpRadius = 0.5f;
    public TeleportAoe(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

    }

    public TeleportAoe(Level level) {
        this(EntityRegistry.TELEPORT_AREA.get(), level);
    }

    @Override
    public void applyEffect(LivingEntity target) {
//        if (getOwner() instanceof LivingEntity owner /*&& !target.equals(owner))*/) {
//            Vec3 distFromCircleCenter = new Vec3((float)(target.position().x - this.position().x), 0, (float)(target.position().z - this.position().z));
//            if(distFromCircleCenter.length() > getRadius() - tpRadius  && distFromCircleCenter.length() < getRadius() + tpRadius){
//                //NeoForge.EVENT_BUS.post(new SpellTeleportEvent(SpellRegistries.DISPLACEMENT_CAGE.get(),target,target.position().x,target.position().y,target.position().z));
//                Vec3 dest = target.position().subtract(new Vec3(distFromCircleCenter.x, 0, distFromCircleCenter.z).multiply(1.5,1,1.5));
//                Utils.handleSpellTeleport(SpellRegistries.DISPLACEMENT_CAGE.get(),target,dest);
//            }
//        }
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
        if(tickCount > duration){
            discard();
        }
        super.tick();
    }

    @Override
    protected boolean canHitEntity(Entity pTarget) {
        return !pTarget.isSpectator() && pTarget.isAlive() && pTarget.isPickable();
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
