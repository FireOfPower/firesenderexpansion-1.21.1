package net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.sword;

import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.UnstableWeaponEntity;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;

import java.util.Optional;

public class UnstableSummonedSwordEntity extends UnstableWeaponEntity implements GeoEntity {


    public UnstableSummonedSwordEntity(Level level, LivingEntity shooter) {
        this((EntityType) EntityRegistry.UNSTABLE_SUMMONED_SWORD.get(), level);
        this.setOwner(shooter);
        this.setNoGravity(true);
    }

    public UnstableSummonedSwordEntity(EntityType<net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.sword.UnstableSummonedSwordEntity> UnstableWeaponEntityType, Level level) {
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