package net.fireofpower.firesenderexpansion.entities.spells.BinaryStars.ObsidianStar;

import net.fireofpower.firesenderexpansion.entities.spells.BinaryStars.BinaryStarEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ObsidianStarEntity extends BinaryStarEntity {
    public ObsidianStarEntity(Level level, LivingEntity shooter) {
        super(level, shooter);
    }

    public ObsidianStarEntity(EntityType<ObsidianStarEntity> obsidianStarEntityEntityType, Level level) {
        super(obsidianStarEntityEntityType,level);
    }
}
