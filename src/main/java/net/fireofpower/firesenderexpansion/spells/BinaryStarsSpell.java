package net.fireofpower.firesenderexpansion.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MultiTargetEntityCastData;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.spells.BinaryStars.NovaStar.NovaStarEntity;
import net.fireofpower.firesenderexpansion.entities.spells.BinaryStars.ObsidianStar.ObsidianStarEntity;
import net.fireofpower.firesenderexpansion.registries.SoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AutoSpellConfig
public class BinaryStarsSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "binary_stars");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMaxLevel(5)
            .setCooldownSeconds(60)
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .build();

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(spellLevel,caster), 1)),
                Component.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getDuration(spellLevel,caster), 1))
        );
    }

    public BinaryStarsSpell()
    {
        this.manaCostPerLevel = 25;
        this.baseSpellPower = 30;
        this.spellPowerPerLevel = 8;
        this.castTime = 0;
        this.baseManaCost = 55;
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
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 64, .15f);
    }

    @Override
    public ICastDataSerializable getEmptyCastData() {
        return new MultiTargetEntityCastData();
    }

    @Override
    public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
        return 2;
    }

    private int getDuration(int spellLevel, LivingEntity caster) {
        return (int) (this.getSpellPower(spellLevel, caster) / 2) + 100;
    }

    private float getDamage(int spellLevel, LivingEntity caster) {
        return this.getSpellPower(spellLevel, caster) * 0.07F + 5;
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.BINARY_STARS_SPELL_CAST.get());
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetEntityCastData) {
            var recasts = playerMagicData.getPlayerRecasts();
            if (!recasts.hasRecastForSpell(getSpellId())) {
                recasts.addRecast(new RecastInstance(getSpellId(), spellLevel, getRecastCount(spellLevel, entity), 80, castSource, new MultiTargetEntityCastData(targetEntityCastData.getTarget((ServerLevel) level))), playerMagicData);
            } else {
                var instance = recasts.getRecastInstance(this.getSpellId());
                if (instance != null && instance.getCastData() instanceof MultiTargetEntityCastData targetingData) {
                    targetingData.addTarget(targetEntityCastData.getTargetUUID());
                }
            }
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
        super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
        var level = serverPlayer.level();
        if (castDataSerializable instanceof MultiTargetEntityCastData targetingData) {
            List<UUID> targets = targetingData.getTargets();
            for(int i = 0; i < targets.size(); i++){
                double yOffset = 3;
                double cosPsi = Math.cos(Math.toRadians(serverPlayer.getYRot()));
                double sinPsi = Math.sin(Math.toRadians(serverPlayer.getYRot()));
                double cosTheta = Math.cos(Math.toRadians(serverPlayer.getXRot()));
                double sinTheta = Math.sin(Math.toRadians(serverPlayer.getXRot()));
                if(targets.size() == 1 || i == 0) {
                    //Fire Nova Star
                    var target = (LivingEntity) ((ServerLevel) serverPlayer.level()).getEntity(targets.get(i));
                    double xOffset = 1;
                    Vec3 origin = serverPlayer.position().add(xOffset* cosPsi- yOffset * sinTheta * sinPsi,yOffset * cosTheta,xOffset * sinPsi + yOffset * sinTheta * cosPsi);
                    if (target != null) {
                        NovaStarEntity novaStar = new NovaStarEntity(level, serverPlayer);
                        novaStar.setPos(origin.subtract(0, novaStar.getBbHeight(), 0));
                        var vec = target.getBoundingBox().getCenter().subtract(serverPlayer.getEyePosition()).normalize();
                        novaStar.shoot(vec.scale(.75f));
                        novaStar.setDamage(getDamage(recastInstance.getSpellLevel(), serverPlayer));
                        novaStar.setHomingTarget(target);
                        novaStar.setDuration(getDuration(recastInstance.getSpellLevel(),serverPlayer));
                        level.addFreshEntity(novaStar);
                    }
                }
                if(targets.size() == 1 || i == 1){
                    //Fire Obsidian Star
                    var target = (LivingEntity) ((ServerLevel) serverPlayer.level()).getEntity(targets.get(i));
                    double xOffset = -2;
                    Vec3 origin = serverPlayer.position().add(xOffset* cosPsi- yOffset * sinTheta * sinPsi,yOffset * cosTheta,xOffset * sinPsi + yOffset * sinTheta * cosPsi);
                    if (target != null) {
                        ObsidianStarEntity obsidianStar = new ObsidianStarEntity(level, serverPlayer);
                        obsidianStar.setPos(origin.subtract(0, obsidianStar.getBbHeight(), -1));
                        var vec = target.getBoundingBox().getCenter().subtract(serverPlayer.getEyePosition()).normalize();
                        obsidianStar.shoot(vec.scale(.75f));
                        obsidianStar.setDamage(getDamage(recastInstance.getSpellLevel(), serverPlayer));
                        obsidianStar.setHomingTarget(target);
                        obsidianStar.setDuration(getDuration(recastInstance.getSpellLevel(),serverPlayer));
                        level.addFreshEntity(obsidianStar);
                    }
                }
            }
        }
    }
}
