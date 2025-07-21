package net.fireofpower.firesenderexpansion.effects;


import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import net.fireofpower.firesenderexpansion.registries.PotionEffectRegistry;
import net.fireofpower.firesenderexpansion.util.ModTags;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class InfiniteVoidPotionEffect extends MagicMobEffect implements AntiMagicSusceptible {
    private Vec3 savedPosition;
    private final int duration = 18;
    private final int range = 20;

    public InfiniteVoidPotionEffect() {
        super(MobEffectCategory.BENEFICIAL, 5984177);
    }

    @Override
    public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectAdded(pLivingEntity, pAmplifier);
        if(pLivingEntity.hasEffect(PotionEffectRegistry.ASCENDED_CASTER_POTION_EFFECT)) {
            savedPosition = pLivingEntity.position();
        }
        if(!pLivingEntity.getType().is(ModTags.INFINITE_VOID_IMMUNE)) {
            pLivingEntity.teleportTo(pLivingEntity.getX(), pLivingEntity.getY() + 10000, pLivingEntity.getZ());
        }
    }

    @Override
    public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectRemoved(pLivingEntity, pAmplifier);
        if(savedPosition == null){
            savedPosition = new Vec3(0, 100,0);
            System.out.println("Manifest Domain: Void found an issue");
        }
        if(!pLivingEntity.getType().is(ModTags.INFINITE_VOID_IMMUNE)) {
            pLivingEntity.teleportTo(savedPosition.x, savedPosition.y, savedPosition.z);
        }
    }

    @Override
    public void onAntiMagic(MagicData magicData) {

    }
}
