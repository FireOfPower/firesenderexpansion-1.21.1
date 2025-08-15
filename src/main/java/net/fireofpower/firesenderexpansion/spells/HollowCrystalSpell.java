package net.fireofpower.firesenderexpansion.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.spells.HollowCrystal.HollowCrystal;
//import net.fireofpower.firesenderexpansion.network.SyncFinalCastPacket;
import net.fireofpower.firesenderexpansion.network.PlayShaderEffectPacket;
import net.fireofpower.firesenderexpansion.network.RemoveShaderEffectPacket;
import net.fireofpower.firesenderexpansion.network.SyncFinalCastPacket;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

@AutoSpellConfig
public class HollowCrystalSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "hollow_crystal");
    private final int ticksOfEffect = 200;

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.firesenderexpansion.hollow_crystal_damage", Utils.stringTruncation(spellLevel * /*FEEModConfig.damagePerCrystalCharge*/15 * getSpellPower(1 /* the spell power doesn't change per level */,caster)/50,1)),
                Component.translatable("ui.firesenderexpansion.charge_count", getRecastCount(spellLevel,caster))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(60)
            .build();

    public HollowCrystalSpell()
    {
        this.manaCostPerLevel = 25;
        this.baseSpellPower = 50;
        this.spellPowerPerLevel = 0;
        this.castTime = 40;
        this.baseManaCost = 55;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        //CameraShakeManager.addCameraShake(new CameraShakeData(5, entity.position(), 20));
        if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(getSpellId())) {
            playerMagicData.getPlayerRecasts().addRecast(new RecastInstance(getSpellId(), spellLevel, getRecastCount(spellLevel, entity), ticksOfEffect, castSource, null), playerMagicData);
        }
        if(entity.hasEffect(EffectRegistry.HOLLOW_CRYSTAL_EFFECT)){
            entity.addEffect(new MobEffectInstance(EffectRegistry.HOLLOW_CRYSTAL_EFFECT, ticksOfEffect, entity.getEffect(EffectRegistry.HOLLOW_CRYSTAL_EFFECT).getAmplifier() + 1, false, false, true));
        }else{
            entity.addEffect(new MobEffectInstance(EffectRegistry.HOLLOW_CRYSTAL_EFFECT, (int) getSpellPower(spellLevel, entity) * 20, 1, false, false, true));
        }
        for(int i = 0; i < 20; i++){
            spawnParticles(entity);
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
        if(spellLevel > 1) {
            return spellLevel;
        }else{
            //the recast method breaks otherwise
            return 2;
        }
    }

    @Override
    public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
        super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
        if(recastResult.isSuccess()) {
            if (serverPlayer.hasEffect(EffectRegistry.HOLLOW_CRYSTAL_EFFECT)) {
                //animation
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, new SyncFinalCastPacket(serverPlayer.getUUID(), SpellRegistries.HOLLOW_CRYSTAL.toString(), false));

                //flash
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, new PlayShaderEffectPacket());

                Vec3 prevLookDir = serverPlayer.getLookAngle();
                //actual casting it
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        PacketDistributor.sendToPlayer(serverPlayer, new RemoveShaderEffectPacket());
                        HollowCrystal hollowCrystal = new HollowCrystal(serverPlayer.level(), serverPlayer);
                        hollowCrystal.setPos(serverPlayer.position().add(0, serverPlayer.getEyeHeight() + hollowCrystal.getBoundingBox().getYsize() * .25f - 3, 0).add(serverPlayer.getForward().multiply(3, 3, 3)));
                        hollowCrystal.setDamage(getDamage(serverPlayer));
                        hollowCrystal.setDeltaMovement(hollowCrystal.getDeltaMovement().multiply(0.5,0.5,0.5));
                        hollowCrystal.shoot(prevLookDir);
                        serverPlayer.removeEffect(EffectRegistry.HOLLOW_CRYSTAL_EFFECT);
                        serverPlayer.level().addFreshEntity(hollowCrystal);
                        serverPlayer.level().playLocalSound(serverPlayer, SoundRegistry.SONIC_BOOM.get(), SoundSource.PLAYERS, 3f, 1f);
                    }
                }, 1000);
            }
        }
    }

    public float getDamage(LivingEntity entity){
        float damagePerCharge = (float) 15;
        if(entity.getEffect(EffectRegistry.HOLLOW_CRYSTAL_EFFECT) != null) {
            return entity.getEffect(EffectRegistry.HOLLOW_CRYSTAL_EFFECT).getAmplifier() * damagePerCharge * getSpellPower(1 /* the spell power doesn't change per level */,entity)/50;
        }else{
            return damagePerCharge;
        }
    }

    private void spawnParticles(LivingEntity entity)
    {
        ServerLevel level = (ServerLevel) entity.level();
        level.sendParticles(ParticleRegistry.UNSTABLE_ENDER_PARTICLE.get(), entity.getX(), entity.getY() + 1, entity.getZ(), 20, 0, 0, 0, 1.0);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return FEESpellAnimations.ANIMATION_HOLLOW_CRYSTAL_CHARGE;
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.END_PORTAL_FRAME_FILL);
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return AnimationHolder.pass();
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }
}
