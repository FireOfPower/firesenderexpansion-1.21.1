package net.fireofpower.firesenderexpansion.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.*;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.spells.BinaryStars.NovaStar.NovaStarEntity;
import net.fireofpower.firesenderexpansion.entities.spells.BinaryStars.ObsidianStar.ObsidianStarEntity;
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
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AutoSpellConfig
public class ScintillatingStrideSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "scintillating_stride");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMaxLevel(10)
            .setCooldownSeconds(10)
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .build();

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(spellLevel,caster), 1)),
                Component.translatable("ui.firesenderexpansion.force", Utils.stringTruncation(getForce(spellLevel,caster),1)),
                Component.translatable("ui.irons_spellbooks.radius", Utils.stringTruncation(getRadius(spellLevel,caster),1))
        );
    }

    public ScintillatingStrideSpell()
    {
        this.manaCostPerLevel = 2;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = 0;
        this.baseManaCost = 20;
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
        return CastType.INSTANT;
    }

    @Override
    public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
        return 2;
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.FIREWORK_ROCKET_LAUNCH);
    }

    private float getDamage(int spellLevel, LivingEntity caster) {
        return this.getSpellPower(spellLevel, caster) * 0.5F + 5;
    }

    private float getForce(int spellLevel, LivingEntity entity) {
        return getSpellPower(spellLevel, entity) * 0.5f;
    }

    private float getRadius(int spellLevel, LivingEntity entity){
        return 1 + getSpellPower(spellLevel,entity) / 2;
    }

    private void spawnParticles(LivingEntity entity)
    {
        ServerLevel level = (ServerLevel) entity.level();
        level.sendParticles(ParticleRegistry.UNSTABLE_ENDER_PARTICLE.get(), entity.getX(), entity.getY() + 1, entity.getZ(), 20, 0, 0, 0, 0.3);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(getSpellId())) {
            playerMagicData.getPlayerRecasts().addRecast(new RecastInstance(getSpellId(), spellLevel, getRecastCount(spellLevel, entity), 100, castSource, null), playerMagicData);
        }
        entity.addEffect(new MobEffectInstance(EffectRegistry.STRIDING_EFFECT, 150, 1, false, false, false));
        Vec3 angleVector = entity.getLookAngle().multiply(1,0.25,1).normalize();
        var vec = angleVector.scale(getForce(spellLevel, entity));
        if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.hurtMarked = true;
        }
        entity.setDeltaMovement(vec);
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
        super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
        //Pre-return
        for(int i = 0; i < 20; i++){
            spawnParticles(serverPlayer);
        }
        MagicManager.spawnParticles(serverPlayer.level(), new BlastwaveParticleOptions(SchoolRegistry.ENDER.get().getTargetingColor(), getRadius(recastInstance.getSpellLevel(), serverPlayer)),
                serverPlayer.position().x, serverPlayer.position().y, serverPlayer.position().z, 1, 0, 0, 0, 0, true);
        serverPlayer.level().playLocalSound(serverPlayer.position().x(), serverPlayer.position().y(), serverPlayer.position().z(), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 15f, 1f, true);

        serverPlayer.level().getEntitiesOfClass(LivingEntity.class,serverPlayer.getBoundingBox().inflate(getRadius(recastInstance.getSpellLevel(), serverPlayer))).stream().forEach(e -> {
            DamageSources.applyDamage(e, getDamage(recastInstance.getSpellLevel(), serverPlayer), getDamageSource(serverPlayer));
        });

        //Trigger return
        if(serverPlayer.hasEffect(EffectRegistry.STRIDING_EFFECT)){
            serverPlayer.removeEffect(EffectRegistry.STRIDING_EFFECT);
        }
    }
}
