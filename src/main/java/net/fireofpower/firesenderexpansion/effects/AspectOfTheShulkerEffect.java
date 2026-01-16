package net.fireofpower.firesenderexpansion.effects;

import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.fireofpower.firesenderexpansion.entities.spells.MagicShulkerBullet;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.fireofpower.firesenderexpansion.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber
public class AspectOfTheShulkerEffect extends MagicMobEffect {
    private static Map<UUID,Integer> internalCooldowns = new HashMap<>();

    public AspectOfTheShulkerEffect() {
        super(MobEffectCategory.BENEFICIAL, Utils.rgbToInt(231,234,211));
    }

    @SubscribeEvent
    public static void handleAbility(LivingIncomingDamageEvent event) {
        if(event.getSource().getEntity() != null) {
            if (!internalCooldowns.containsKey(event.getSource().getEntity().getUUID())) {
                internalCooldowns.put(event.getSource().getEntity().getUUID(), 0);
            }
            if (event.getSource().getEntity() instanceof LivingEntity living && living.hasEffect(EffectRegistry.ASPECT_OF_THE_SHULKER_EFFECT)) {
                if (event.getSource() instanceof SpellDamageSource && internalCooldowns.get(event.getSource().getEntity().getUUID()) == 0) {
                    LivingEntity victimPlayer = event.getEntity();
                    Level world = living.level();
                    MagicShulkerBullet bullet = new MagicShulkerBullet(living.level(), living, victimPlayer, Direction.Axis.X);
                    bullet.setPos(living.getBoundingBox().getCenter().add((double) 0.0F, (double) (bullet.getBbHeight() * 3F), (double) 0.0F));
                    world.addFreshEntity(bullet);
                    internalCooldowns.replace(living.getUUID(), 5);
                }
            }
        }
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!internalCooldowns.containsKey(entity.getUUID())) {
            internalCooldowns.put(entity.getUUID(), 0);
        }
        if(internalCooldowns.get(entity.getUUID()) > 0){
            internalCooldowns.replace(entity.getUUID(),internalCooldowns.get(entity.getUUID()) - 1);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplifier) {
        return true;
    }
}
