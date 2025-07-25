package net.fireofpower.firesenderexpansion.effects;


import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.PocketDimensionManager;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.spells.eldritch.PocketDimensionSpell;
import net.fireofpower.firesenderexpansion.registries.PotionEffectRegistry;
import net.fireofpower.firesenderexpansion.util.ModTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InfiniteVoidPotionEffect extends MagicMobEffect implements AntiMagicSusceptible {
    private Vec3 savedPosition;
    private Level savedDimension;
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
            savedDimension = pLivingEntity.level();
        }
        if(!pLivingEntity.getType().is(ModTags.INFINITE_VOID_IMMUNE)) {
            //puts savedPosition at 0,0 and keeps relative distance
            Vec3 newPos = pLivingEntity.position().subtract(savedPosition).add(0,1000,0);
            pLivingEntity.changeDimension(new DimensionTransition(Objects.requireNonNull(pLivingEntity.getServer()).getLevel(PocketDimensionManager.POCKET_DIMENSION),newPos,Vec3.ZERO,pLivingEntity.getXRot(),pLivingEntity.getYRot(),DimensionTransition.DO_NOTHING));
        }
    }

    @Override
    public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectRemoved(pLivingEntity, pAmplifier);
        if(savedPosition == null || savedDimension == null){
            savedPosition = new Vec3(0, 100,0);
            System.out.println("Manifest Domain: Void found an issue");
        }
        if(!pLivingEntity.getType().is(ModTags.INFINITE_VOID_IMMUNE)) {
            pLivingEntity.changeDimension(new DimensionTransition((ServerLevel) savedDimension,savedPosition,Vec3.ZERO,pLivingEntity.getXRot(),pLivingEntity.getYRot(),DimensionTransition.DO_NOTHING));
        }
    }

    @Override
    public void onAntiMagic(MagicData magicData) {

    }
}
