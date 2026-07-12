package net.fireofpower.firesenderexpansion.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.events.SpellSummonEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.*;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.spells.MagicEndCrystal.MagicEndCrystal;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class EndsRejuvenationSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "ends_rejuvenation");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.duration", Utils.stringTruncation(getHealth(caster,spellLevel),2)),
                Component.translatable("ui.irons_spellbooks.summon_count", getSummonCount(caster,spellLevel)),
                Component.translatable("ui.irons_spellbooks.hp", Utils.stringTruncation(getHealth(caster,spellLevel),2))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMaxLevel(3)
            .setCooldownSeconds(180)
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .build();

    public EndsRejuvenationSpell(){
        this.manaCostPerLevel = 50;
        this.baseSpellPower = 10;
        this.spellPowerPerLevel = 5;
        this.castTime = 40;
        this.baseManaCost = 150;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
        PlayerRecasts recasts = playerMagicData.getPlayerRecasts();
        if(!recasts.hasRecastForSpell(getSpellId())) {
            SummonedEntitiesCastData summonedEntitiesCastData = new SummonedEntitiesCastData();
            int summonTime = (int) (20 * getDuration(entity,spellLevel));
            double degree = 2 * Mth.PI / getSummonCount(entity, spellLevel);
            double offset = degree * Math.random();
            float radius = 5;
            for (int i = 0; i < getSummonCount(entity, spellLevel); i++) {
                radius += (float) (Math.random() * 5);
                Vec3 circlePos = entity.position().add(new Vec3(radius * Math.sin(offset + i * degree), 0, radius * Math.cos(offset + i * degree)));
                float yDifference = (float) (Utils.findRelativeGroundLevel(level, circlePos, 3) - entity.position().y());
                Vec3 spawnPos = circlePos.add(0, yDifference, 0);

                MagicEndCrystal crystal = new MagicEndCrystal(level, spawnPos.x, spawnPos.y, spawnPos.z);
                crystal.setOwner(entity);
                crystal.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(getHealth(entity,spellLevel));
                crystal.setHealth(crystal.getMaxHealth());
                SummonManager.initSummon(entity, crystal, summonTime, summonedEntitiesCastData);
                level.addFreshEntity(crystal);
            }
            RecastInstance recastInstance = new RecastInstance(this.getSpellId(), spellLevel, getRecastCount(spellLevel, entity), summonTime, castSource, summonedEntitiesCastData);
            recasts.addRecast(recastInstance, playerMagicData);
        }
    }

    @Override
    public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
        if (SummonManager.recastFinishedHelper(serverPlayer, recastInstance, recastResult, castDataSerializable)) {
            super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
        }
    }

    @Override
    public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
        return 2;
    }

    @Override
    public ICastDataSerializable getEmptyCastData() {
        return new SummonedEntitiesCastData();
    }

    public float getHealth(LivingEntity entity, int spellLevel){
        return getSpellPower(spellLevel,entity);
    }

    public int getSummonCount(LivingEntity entity, int spellLevel){
        return spellLevel;
    }

    public int getHealing(LivingEntity entity, int spellLevel){
        return 3;
    }

    public float getDuration(LivingEntity entity, int spellLevel){
        return getSpellPower(spellLevel,entity) / 2;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.TOUCH_GROUND_ANIMATION;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.ENDER_EYE_DEATH);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.ENDER_DRAGON_GROWL);
    }
}
