package net.fireofpower.firesenderexpansion.effects;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.entity.spells.EchoingStrikeEntity;
import io.redspace.ironsspellbooks.entity.spells.magic_arrow.MagicArrowProjectile;
import io.redspace.ironsspellbooks.entity.spells.magic_missile.MagicMissileProjectile;
import net.fireofpower.firesenderexpansion.entities.MagicShulkerBullet;
import net.fireofpower.firesenderexpansion.registries.PotionEffectRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class AspectOfTheShulkerPotionEffect extends MagicMobEffect {
    private static int internalCooldown = 0;

    public AspectOfTheShulkerPotionEffect() {
        //TODO: Figure out a different color for this
        super(MobEffectCategory.BENEFICIAL, 5984177);
    }

    @SubscribeEvent
    public static void handleAbility(LivingIncomingDamageEvent event) {
        System.out.println("Internal Cooldown of Shulker aspect: " + internalCooldown);
        if (event.getSource().getEntity() instanceof ServerPlayer attackingPlayer && attackingPlayer.hasEffect(PotionEffectRegistry.ASPECT_OF_THE_SHULKER_POTION_EFFECT)) {
            if(event.getSource() instanceof SpellDamageSource && internalCooldown == 0){
                LivingEntity victimPlayer = event.getEntity();
                Level world = attackingPlayer.level();
                MagicShulkerBullet bullet = new MagicShulkerBullet(attackingPlayer.level(), attackingPlayer, victimPlayer, Direction.Axis.X);
                bullet.setPos(attackingPlayer.getBoundingBox().getCenter().add((double)0.0F, (double)(bullet.getBbHeight() * 3F), (double)0.0F));
                world.addFreshEntity(bullet);
                internalCooldown = 1;
            }
        }
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(internalCooldown > 0){
            internalCooldown -= 1;
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplifier) {
        return true; // replace this with whatever check you want
    }
}
