package net.fireofpower.firesenderexpansion.entities.spells.BinaryStars.ObsidianStar;

import io.redspace.ironsspellbooks.damage.DamageSources;
import net.fireofpower.firesenderexpansion.entities.spells.BinaryStars.BinaryStarEntity;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class ObsidianStarEntity extends BinaryStarEntity {
    public ObsidianStarEntity(Level level, LivingEntity shooter) {
        this((EntityType) EntityRegistry.OBSIDIAN_STAR.get(), level);
        this.setOwner(shooter);
        this.setNoGravity(true);
    }

    public ObsidianStarEntity(EntityType<ObsidianStarEntity> obsidianStarEntityEntityType, Level level) {
        super(obsidianStarEntityEntityType,level);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        if(canHitEntity(pResult.getEntity())){
            if(pResult.getEntity() instanceof LivingEntity livingEntity){
                System.out.println("Hit living entity, attempting to apply effect for " + getDuration());
                livingEntity.addEffect(new MobEffectInstance(EffectRegistry.ECLIPSED_EFFECT,getDuration()));
            }
            DamageSources.applyDamage(pResult.getEntity(), damage, SpellRegistries.BINARY_STARS.get().getDamageSource(this, getOwner()));
        }
    }
}
