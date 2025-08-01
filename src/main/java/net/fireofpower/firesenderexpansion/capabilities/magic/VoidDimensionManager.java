package net.fireofpower.firesenderexpansion.capabilities.magic;

import io.redspace.ironsspellbooks.capabilities.magic.PocketDimensionManager;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.effects.InfiniteVoidPotionEffect;
import net.fireofpower.firesenderexpansion.registries.PotionEffectRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.openjdk.nashorn.internal.runtime.logging.DebugLogger;

public class VoidDimensionManager {
    public static final ResourceKey<Level> VOID_DIMENSION = ResourceKey.create(Registries.DIMENSION, FiresEnderExpansion.id("void_dimension"));
    public static final PocketDimensionManager INSTANCE = new PocketDimensionManager();

    public void tick(Level level){
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!serverLevel.dimension().equals(VoidDimensionManager.VOID_DIMENSION)) {
            return;
        }
        if (serverLevel.getGameTime() % 100 == 0) {
            serverLevel.getAllEntities().forEach(entity -> {
                if(entity instanceof LivingEntity livingEntity && shouldKickOut(livingEntity)){
                    System.out.println("Manifest Domain: Void found an issue, sending player to 0,200,0 in the Overworld");
                    livingEntity.changeDimension(new DimensionTransition(entity.level().getServer().getLevel(Level.OVERWORLD), Vec3.ZERO.add(0,200,0), Vec3.ZERO, 0, 0, DimensionTransition.DO_NOTHING));
                }
            });
        }
    }

    public boolean shouldKickOut(LivingEntity entity){
        if(entity instanceof ServerPlayer player) {
            if (!player.isCreative() && !player.isSpectator()) {
                return false;
            }
        }
        if(entity.hasEffect(PotionEffectRegistry.INFINITE_VOID_POTION_EFFECT)){
            return false;
        }
        return true;
    }
}
