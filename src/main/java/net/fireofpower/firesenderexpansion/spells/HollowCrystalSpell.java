package net.fireofpower.firesenderexpansion.spells;

import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.HollowCrystal.HollowCrystal;
import net.fireofpower.firesenderexpansion.registries.PotionEffectRegistry;
import net.minecraft.client.player.AbstractClientPlayer;
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
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class HollowCrystalSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "hollow_crystal");
    private final int ticksOfEffect = 200;

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.firesenderexpansion.hollow_crystal_damage", Utils.stringTruncation(spellLevel * 10 * getSpellPower(1 /* the spell power doesn't change per level */,caster)/50,1)),
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
        this.castTime = 20;
        this.baseManaCost = 55;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        CameraShakeManager.addCameraShake(new CameraShakeData(5, entity.position(), 20));
        if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(getSpellId())) {
            playerMagicData.getPlayerRecasts().addRecast(new RecastInstance(getSpellId(), spellLevel, getRecastCount(spellLevel, entity), ticksOfEffect, castSource, null), playerMagicData);
        }
        if(entity.hasEffect(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT)){
            entity.addEffect(new MobEffectInstance(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT, ticksOfEffect, entity.getEffect(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT).getAmplifier() + 1, false, false, true));
        }else{
            entity.addEffect(new MobEffectInstance(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT, (int) getSpellPower(spellLevel, entity) * 20, 1, false, false, true));
        }
        for(int i = 0; i < 20; i++){
            spawnParticles(entity);
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

        if(serverPlayer.hasEffect(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT)){
            //System.out.println("Launch a Hollow Crystal with " + serverPlayer.getEffect(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT).getAmplifier() + " Power");
            HollowCrystal hollowCrystal = new HollowCrystal(serverPlayer.level(), serverPlayer);
            hollowCrystal.setPos(serverPlayer.position().add(0, serverPlayer.getEyeHeight() + hollowCrystal.getBoundingBox().getYsize() * .25f - 3, 0).add(serverPlayer.getForward().multiply(3,3,3)));
            hollowCrystal.shoot(serverPlayer.getLookAngle());
            hollowCrystal.setDeltaMovement(hollowCrystal.getDeltaMovement().multiply(0.5,0.5,0.5));
            hollowCrystal.setDamage(getDamage(serverPlayer));
            serverPlayer.removeEffect(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT);
            serverPlayer.level().addFreshEntity(hollowCrystal);
            serverPlayer.level().playLocalSound(serverPlayer,SoundRegistry.SONIC_BOOM.get(),SoundSource.PLAYERS,3f,1f);
        }
    }

    public float getDamage(LivingEntity entity){
        float damagePerCharge = 10;
        if(entity.getEffect(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT) != null) {
            return entity.getEffect(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT).getAmplifier() * damagePerCharge * getSpellPower(1 /* the spell power doesn't change per level */,entity)/50;
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
        return FEESpellAnimations.ANIMATION_HOLLOW_CRYSTAL_CAST;
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
