package net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.claymore;

//import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.rapier.UnstableSummonedSwordEntity;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.UnstableWeaponEntity;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;

import java.util.Optional;

public class UnstableSummonedClaymoreEntity extends UnstableWeaponEntity implements GeoEntity {


    public UnstableSummonedClaymoreEntity(Level level, LivingEntity shooter) {
        this((EntityType) EntityRegistry.UNSTABLE_SUMMONED_CLAYMORE.get(), level);
        this.setOwner(shooter);
        this.setNoGravity(true);
    }

    public UnstableSummonedClaymoreEntity(EntityType<UnstableSummonedClaymoreEntity> UnstableWeaponEntityType, Level level) {
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

    @Override
    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.empty();
    }
}