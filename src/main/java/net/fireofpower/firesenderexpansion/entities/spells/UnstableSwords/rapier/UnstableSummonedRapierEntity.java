package net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.rapier;

import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.UnstableWeaponEntity;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;

import java.util.Optional;

public class UnstableSummonedRapierEntity extends UnstableWeaponEntity implements GeoEntity {


    public UnstableSummonedRapierEntity(Level level, LivingEntity shooter) {
        this((EntityType) EntityRegistry.UNSTABLE_SUMMONED_RAPIER.get(), level);
        this.setOwner(shooter);
        this.setNoGravity(true);
    }

    public UnstableSummonedRapierEntity(EntityType<UnstableSummonedRapierEntity> UnstableWeaponEntityType, Level level) {
        super(UnstableWeaponEntityType,level);
    }

    @Override
    public void trailParticles() {

    }

    @Override
    public void impactParticles(double v, double v1, double v2) {

    }

    @Override
    public float getSpeed() {
        return super.getSpeed();
    }
}