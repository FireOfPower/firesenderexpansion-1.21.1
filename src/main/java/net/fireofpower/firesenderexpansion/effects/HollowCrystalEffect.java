package net.fireofpower.firesenderexpansion.effects;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class HollowCrystalEffect extends MagicMobEffect {
    public HollowCrystalEffect(){
        super(MobEffectCategory.BENEFICIAL, net.fireofpower.firesenderexpansion.util.Utils.rgbToInt(255,224,255));
    }
}
