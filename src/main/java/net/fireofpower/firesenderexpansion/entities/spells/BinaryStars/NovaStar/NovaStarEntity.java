package net.fireofpower.firesenderexpansion.entities.spells.BinaryStars.NovaStar;

import net.fireofpower.firesenderexpansion.entities.spells.BinaryStars.BinaryStarEntity;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class NovaStarEntity extends BinaryStarEntity {
    public NovaStarEntity(Level level, LivingEntity shooter) {
        this((EntityType) EntityRegistry.NOVA_STAR.get(), level);
        this.setOwner(shooter);
        this.setNoGravity(true);
    }

    public NovaStarEntity(EntityType<NovaStarEntity> novaStarEntityEntityType, Level level) {
        super(novaStarEntityEntityType,level);
    }
}
