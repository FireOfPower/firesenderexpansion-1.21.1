package net.fireofpower.firesenderexpansion.effects;


import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.spells.holy.HasteSpell;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.MagicShulkerBullet;
import net.fireofpower.firesenderexpansion.registries.PotionEffectRegistry;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
