package net.fireofpower.firesenderexpansion.effects;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class HollowCrystalPotionEffect extends MagicMobEffect {
    public HollowCrystalPotionEffect(){
        super(MobEffectCategory.BENEFICIAL, 5984177);
    }

    @Override
    public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectAdded(pLivingEntity, pAmplifier);
        MagicData.getPlayerMagicData(pLivingEntity).getSyncedData().addEffects(SyncedSpellData.PLANAR_SIGHT);
    }

    @Override
    public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectRemoved(pLivingEntity, pAmplifier);
        MagicData.getPlayerMagicData(pLivingEntity).getSyncedData().removeEffects(SyncedSpellData.PLANAR_SIGHT);
    }
}
