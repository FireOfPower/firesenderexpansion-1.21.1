package net.fireofpower.firesenderexpansion.effects;


import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.fireofpower.firesenderexpansion.registries.PotionEffectRegistry;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class InfiniteVoidPotionEffect extends MagicMobEffect {
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
        pLivingEntity.teleportTo(pLivingEntity.getX(), pLivingEntity.getY() + 10000, pLivingEntity.getZ());
    }

    @Override
    public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectRemoved(pLivingEntity, pAmplifier);
        if(savedPosition == null){
            savedPosition = new Vec3(0, 100,0);
            System.out.println("Manifest Domain: Void found an issue");
        }
        pLivingEntity.teleportTo(savedPosition.x,savedPosition.y,savedPosition.z);
    }
}
