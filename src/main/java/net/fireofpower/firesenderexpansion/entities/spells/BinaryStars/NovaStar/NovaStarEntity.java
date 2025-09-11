package net.fireofpower.firesenderexpansion.entities.spells.BinaryStars.NovaStar;

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

public class NovaStarEntity extends BinaryStarEntity {
    public NovaStarEntity(Level level, LivingEntity shooter) {
        this((EntityType) EntityRegistry.NOVA_STAR.get(), level);
        this.setOwner(shooter);
        this.setNoGravity(true);
    }

    public NovaStarEntity(EntityType<NovaStarEntity> novaStarEntityEntityType, Level level) {
        super(novaStarEntityEntityType,level);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        if(canHitEntity(pResult.getEntity())){
            if(pResult.getEntity() instanceof LivingEntity livingEntity){
                livingEntity.addEffect(new MobEffectInstance(EffectRegistry.NOVA_BURN_EFFECT,getDuration()));
            }
            DamageSources.applyDamage(pResult.getEntity(), damage, SpellRegistries.BINARY_STARS.get().getDamageSource(this, getOwner()));
        }
    }
}
