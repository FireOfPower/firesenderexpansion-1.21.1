package net.fireofpower.firesenderexpansion.entities.spells;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.Level;

public class MagicShulkerBullet extends ShulkerBullet {
    public MagicShulkerBullet(Level level, LivingEntity shooter, Entity finalTarget, Direction.Axis axis) {
        super(level, shooter, finalTarget, axis);
    }

    public MagicShulkerBullet(EntityType<MagicShulkerBullet> magicShulkerBulletEntityType, Level level) {
        super(magicShulkerBulletEntityType, level);

    }
    //I was initially going to use this to make the shulker bullets faster but that's kinda busted
}
