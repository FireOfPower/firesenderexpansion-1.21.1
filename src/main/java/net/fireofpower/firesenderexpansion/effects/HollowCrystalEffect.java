package net.fireofpower.firesenderexpansion.effects;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.api.util.Utils;
//import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class HollowCrystalEffect extends MagicMobEffect {
    public HollowCrystalEffect(){
        super(MobEffectCategory.BENEFICIAL, net.fireofpower.firesenderexpansion.util.Utils.rgbToInt(255,224,255));
    }
}
