package net.fireofpower.firesenderexpansion.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.blood.AcupunctureSpell;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.HollowCrystal.HollowCrystal;
import net.fireofpower.firesenderexpansion.registries.PotionEffectRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@AutoSpellConfig
public class HollowCrystalSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "hollow_crystal");
    private final int ticksOfEffect = 200;

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.firesenderexpansion.hollow_crystal_damage", Utils.stringTruncation(spellLevel * 20 * getSpellPower(1 /* the spell power doesn't change per level */,caster)/50,1)),
                Component.translatable("ui.firesenderexpansion.charge_count", getRecastCount(spellLevel,caster))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(40)
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
        if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(getSpellId())) {
            playerMagicData.getPlayerRecasts().addRecast(new RecastInstance(getSpellId(), spellLevel, getRecastCount(spellLevel, entity), ticksOfEffect, castSource, null), playerMagicData);
        }
        if(entity.hasEffect(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT)){
            entity.addEffect(new MobEffectInstance(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT, ticksOfEffect, entity.getEffect(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT).getAmplifier() + 1, false, false, true));
        }else{
            entity.addEffect(new MobEffectInstance(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT, (int) getSpellPower(spellLevel, entity) * 20, 1, false, false, true));
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
            hollowCrystal.setDeltaMovement(hollowCrystal.getDeltaMovement().multiply(0.25,0.25,0.25));
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

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.PREPARE_CROSS_ARMS;
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.END_PORTAL_FRAME_FILL);
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
