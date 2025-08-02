package net.fireofpower.firesenderexpansion.effects;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.world.effect.MobEffectCategory;

public class AnchoredEffect extends MagicMobEffect {
    public AnchoredEffect() {
        super(MobEffectCategory.HARMFUL, Utils.rgbToInt(150,20,30));
    }
}
