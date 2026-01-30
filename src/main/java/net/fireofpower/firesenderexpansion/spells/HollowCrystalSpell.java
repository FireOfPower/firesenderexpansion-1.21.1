package net.fireofpower.firesenderexpansion.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.fire.FireballSpell;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.spells.HollowCrystal.HollowCrystal;
//import net.fireofpower.firesenderexpansion.network.SyncFinalCastPacket;
import net.fireofpower.firesenderexpansion.network.AddShaderEffectPacket;
import net.fireofpower.firesenderexpansion.network.RemoveShaderEffectPacket;
import net.fireofpower.firesenderexpansion.network.SyncFinalCastPacket;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.fireofpower.firesenderexpansion.registries.ItemRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;


public class HollowCrystalSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "hollow_crystal");
    private final int ticksOfEffect = 400;

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.firesenderexpansion.hollow_crystal_damage", Utils.stringTruncation(getRecastCount(spellLevel,caster) * 15 * getSpellPower(spellLevel,caster)/50,1)),
                Component.translatable("ui.firesenderexpansion.charge_count", getRecastCount(spellLevel,caster))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(30)
            .build();

    public HollowCrystalSpell()
    {
        this.manaCostPerLevel = 25;
        this.baseSpellPower = 20;
        this.spellPowerPerLevel = 5;
        this.castTime = 40;
        this.baseManaCost = 55;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(getSpellId())) {
            playerMagicData.getPlayerRecasts().addRecast(new RecastInstance(getSpellId(), spellLevel, getRecastCount(spellLevel, entity), ticksOfEffect, castSource, null), playerMagicData);
        }
        if(entity.hasEffect(EffectRegistry.HOLLOW_CRYSTAL_EFFECT)){
            if(!entity.isCrouching()) {
                entity.addEffect(new MobEffectInstance(EffectRegistry.HOLLOW_CRYSTAL_EFFECT, ticksOfEffect, entity.getEffect(EffectRegistry.HOLLOW_CRYSTAL_EFFECT).getAmplifier() + 1, false, false, true));
                if(playerMagicData.getPlayerRecasts().getRemainingRecastsForSpell(getSpellId()) != 1) {
                    spawnParticles(entity);
                }
            }else{
                if(entity instanceof ServerPlayer serverPlayer  && net.fireofpower.firesenderexpansion.util.Utils.hasCurio(serverPlayer, ItemRegistry.CRYSTAL_HEART.get())) {
                    handleFiring(serverPlayer, spellLevel);
                }
            }
        }else{
            if(spellLevel > 1) {
                entity.addEffect(new MobEffectInstance(EffectRegistry.HOLLOW_CRYSTAL_EFFECT, ticksOfEffect, 1, false, false, true));
                spawnParticles(entity);
            }else{
                if(entity instanceof ServerPlayer serverPlayer){
                    entity.addEffect(new MobEffectInstance(EffectRegistry.HOLLOW_CRYSTAL_EFFECT, ticksOfEffect, 1, false, false, true));
                    handleFiring(serverPlayer,spellLevel);
                }
            }
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
        return spellLevel;
    }

    @Override
    public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
        super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
        if(recastResult.isSuccess() && !serverPlayer.level().isClientSide()) {
            handleFiring(serverPlayer, recastInstance.getSpellLevel());
        }
    }

    private void handleFiring(ServerPlayer serverPlayer, int spellLevel){
        PlayerRecasts recasts = MagicData.getPlayerMagicData(serverPlayer).getPlayerRecasts();
        if(recasts.hasRecastForSpell(SpellRegistries.HOLLOW_CRYSTAL.get().getSpellId())){
            recasts.removeRecast(SpellRegistries.HOLLOW_CRYSTAL.get().getSpellId());
            recasts.syncAllToPlayer();
        }
        if (serverPlayer.hasEffect(EffectRegistry.HOLLOW_CRYSTAL_EFFECT)) {
            //animation
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, new SyncFinalCastPacket(serverPlayer.getUUID(), SpellRegistries.HOLLOW_CRYSTAL.toString(), false));

            //actual casting it
            serverPlayer.addEffect(new MobEffectInstance(EffectRegistry.LOCKED_CAMERA_EFFECT,20,serverPlayer.getEffect(EffectRegistry.HOLLOW_CRYSTAL_EFFECT).getAmplifier(),true, false));
            HollowCrystal hollowCrystal = new HollowCrystal(serverPlayer.level(), serverPlayer);
            hollowCrystal.setPos(serverPlayer.position().add(0, serverPlayer.getEyeHeight() + hollowCrystal.getBoundingBox().getYsize() * .25f - 3, 0).add(serverPlayer.getForward().multiply(10, 10, 10)));
            hollowCrystal.setDamage(getDamage(serverPlayer, spellLevel));
            hollowCrystal.setDeltaMovement(0,0,0);
            hollowCrystal.setDelay(20);
            CameraShakeManager.addCameraShake(new CameraShakeData(serverPlayer.level(),20, serverPlayer.position(), 20));
            serverPlayer.removeEffect(EffectRegistry.HOLLOW_CRYSTAL_EFFECT);
            serverPlayer.level().addFreshEntity(hollowCrystal);
        }
    }

    @Override
    public void onClientPreCast(Level level, int spellLevel, LivingEntity entity, InteractionHand hand, @Nullable MagicData playerMagicData) {
        super.onClientPreCast(level, spellLevel, entity, hand, playerMagicData);
        // attempt to align body with arms so the sword animation plays more smoothly
        entity.setYBodyRot(entity.getYRot());
    }

    public float getDamage(LivingEntity entity, int spellLevel){
        float damagePerCharge = (float) 15;
        if(entity.getEffect(EffectRegistry.HOLLOW_CRYSTAL_EFFECT) != null) {
            return entity.getEffect(EffectRegistry.HOLLOW_CRYSTAL_EFFECT).getAmplifier() * damagePerCharge * getSpellPower(spellLevel,entity)/50;
        }else{
            return damagePerCharge * getSpellPower(spellLevel,entity)/50;
        }
    }

    private void spawnParticles(LivingEntity entity)
    {
        ServerLevel level = (ServerLevel) entity.level();
        for(int i = 0; i < 20; i++){
            level.sendParticles(ParticleRegistry.UNSTABLE_ENDER_PARTICLE.get(), entity.getX(), entity.getY() + 1, entity.getZ(), 20, 0, 0, 0, 1.0);
        }
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

    @Override
    public int getCastTime(int spellLevel) {
        return 30;
    }
}
