package net.fireofpower.firesenderexpansion.entities;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.EchoingStrikeEntity;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TeleportRend extends EchoingStrikeEntity {

    private final Level level;
    private final double waitTime;

    public TeleportRend(Level level, LivingEntity owner, float damage, float radius, double waitTime) {
        super(level, owner, damage, radius);
        this.level = level;
        this.waitTime = waitTime;
    }

    @Override
    public void tick() {
        if (this.tickCount == waitTime) {
            this.playSound((SoundEvent) SoundRegistry.ECHOING_STRIKE.get(), 1.0F, (float) Utils.random.nextIntBetweenInclusive(8, 12) * 0.1F);
            if (!this.level.isClientSide) {
                Vec3 center = this.getBoundingBox().getCenter();
                MagicManager.spawnParticles(this.level, ParticleHelper.UNSTABLE_ENDER, center.x, center.y, center.z, 25, (double)0.0F, (double)0.0F, (double)0.0F, 0.18, false);
                MagicManager.spawnParticles(this.level, new BlastwaveParticleOptions(((AbstractSpell) SpellRegistry.ECHOING_STRIKES_SPELL.get()).getSchoolType().getTargetingColor(), this.getRadius() * 0.9F), center.x, center.y, center.z, 1, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F, true);
                float explosionRadius = this.getRadius();
                float explosionRadiusSqr = explosionRadius * explosionRadius;
                List<Entity> entities = this.level.getEntities(this, this.getBoundingBox().inflate((double)explosionRadius));
                Vec3 losCenter = Utils.moveToRelativeGroundLevel(this.level, center, 2);
                losCenter = Utils.raycastForBlock(this.level, losCenter, losCenter.add((double)0.0F, (double)3.0F, (double)0.0F), ClipContext.Fluid.NONE).getLocation().add(losCenter).scale((double)0.5F);

                for(Entity entity : entities) {
                    double distanceSqr = entity.distanceToSqr(center);
                    if (distanceSqr < (double)explosionRadiusSqr && this.canHitEntity(entity) && Utils.hasLineOfSight(this.level, losCenter, entity.getBoundingBox().getCenter(), true)) {
                        double p = Mth.clamp((double)1.0F - distanceSqr / (double)explosionRadiusSqr + (double)0.4F, (double)0.0F, (double)1.0F);
                        float damage = (float)((double)this.damage * p);
                        DamageSources.applyDamage(entity, damage, ((AbstractSpell)SpellRegistry.ECHOING_STRIKES_SPELL.get()).getDamageSource(this, this.getOwner()));
                    }
                }
            }
        } else if (this.tickCount > waitTime) {
            this.discard();
        }

        if (this.level.isClientSide && this.tickCount < waitTime / 2) {
            Vec3 position = this.getBoundingBox().getCenter();

            for(int i = 0; i < 3; ++i) {
                Vec3 vec3 = Utils.getRandomVec3((double)1.0F);
                vec3 = vec3.multiply(vec3).multiply((double)Mth.sign(vec3.x), (double)Mth.sign(vec3.y), (double)Mth.sign(vec3.z)).scale((double)this.getRadius()).add(position);
                Vec3 motion = position.subtract(vec3).scale((double)0.125F);
                this.level.addParticle(ParticleHelper.UNSTABLE_ENDER, vec3.x, vec3.y - (double)0.5F, vec3.z, motion.x, motion.y, motion.z);
            }
        }

    }
}
