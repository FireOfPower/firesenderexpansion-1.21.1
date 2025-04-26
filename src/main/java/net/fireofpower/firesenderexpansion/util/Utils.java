package net.fireofpower.firesenderexpansion.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;

public class Utils {
    public static boolean shouldBreakHollowCrystal(Projectile target){
        return target.getType().is(ModTags.BREAKS_HOLLOW_CRYSTAL);
    }
}
