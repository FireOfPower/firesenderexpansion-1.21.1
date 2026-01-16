package net.fireofpower.firesenderexpansion.capabilities.magic;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.PocketDimensionManager;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class VoidDimensionManager {
    public static final ResourceKey<Level> VOID_DIMENSION = ResourceKey.create(Registries.DIMENSION, FiresEnderExpansion.id("void_dimension"));
    public static final VoidDimensionManager INSTANCE = new VoidDimensionManager();


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
        if(level.getGameTime() % 20 == 0){
            serverLevel.getAllEntities().forEach(entity -> {
                if(entity instanceof LivingEntity livingEntity && !livingEntity.hasEffect(EffectRegistry.ASCENDED_CASTER_EFFECT)){
                    float yHeadRot = entity.getYHeadRot();
                    yHeadRot += 90 * (int)(Math.random() * 5);
                    for(int i = -5; i <= 5; i++) {
                        Vec3 particlePos = entity.position();
                        particlePos = particlePos.add(0,livingEntity.getBbHeight() / 2,0);
                        particlePos = particlePos.add(new Vec3(Math.cos(yHeadRot) * 0.3,0.3,-Math.sin(yHeadRot) * 0.3).scale(i));
                        if(i % 2 == 0) {
                            MagicManager.spawnParticles(level,ParticleTypes.SQUID_INK,particlePos.x,particlePos.y - 0.5,particlePos.z,1,0,0,0,0,false);
                        }else{
                            MagicManager.spawnParticles(level,ParticleTypes.SQUID_INK,particlePos.x,particlePos.y + 0.5,particlePos.z,1,0,0,0,0,false);
                        }
                        MagicManager.spawnParticles(level, ParticleRegistry.UNSTABLE_ENDER_PARTICLE.get(), particlePos.x,particlePos.y,particlePos.z,1,0,0,0,0,false);
                    }
                    serverLevel.playSound(null,livingEntity.blockPosition(), SoundRegistry.DEVOUR_BITE.get(), SoundSource.PLAYERS,5,10);
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
        if(entity.hasEffect(EffectRegistry.INFINITE_VOID_EFFECT)){
            return false;
        }
        return true;
    }

    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Pre event) {
        if (event.getLevel().isClientSide) {
            return;
        }
        VoidDimensionManager.INSTANCE.tick(event.getLevel());
    }
}
