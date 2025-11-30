package net.fireofpower.firesenderexpansion.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import net.fireofpower.firesenderexpansion.Config;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.spells.GateOfEnder.GatePortal;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class GateOfEnderSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "gate_of_ender");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMaxLevel(1)
            .setCooldownSeconds(60)
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .build();

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(spellLevel,caster),1)),
                Component.translatable("ui.firesenderexpansion.radius",Utils.stringTruncation(getRadius(spellLevel,caster),1)),
                Component.translatable("ui.firesenderexpansion.numSwords",getNumSwords(spellLevel,caster))
        );
    }

    public GateOfEnderSpell(){
        this.manaCostPerLevel = 1;
        this.baseSpellPower = 10;
        this.spellPowerPerLevel = 0;
        this.castTime = 200;
        this.baseManaCost = 15;
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return FEESpellAnimations.ANIMATION_GATE_OF_ENDER_OPEN;
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
        return CastType.CONTINUOUS;
    }

    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, .15f, false);
        return true;
    }

    @Override
    public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
        if (playerMagicData != null && (playerMagicData.getCastDurationRemaining() + 1) % 5 == 0) {
            if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData castTargetingData && castTargetingData.getTarget((ServerLevel) level) != null) {
                //targeted function
                LivingEntity targeted = castTargetingData.getTarget(level.getServer().getLevel(level.dimension()));
                int swords = getNumSwords(spellLevel,entity);
                assert targeted != null;
                for (int i = 0; i < swords / 2; i++) {
                    shootTargetedSword(level,spellLevel,entity,targeted);
                }
            }else {
                if(entity.isCrouching() && Config.ALLOW_SWORD_HAIL.get()){
                    //hail function
                    int swords = getNumSwords(spellLevel,entity);
                    for (int i = 0; i < swords; i++) {
                        this.shootHailSword(level, spellLevel, entity);
                    }
                }else {
                    //normal function
                    int swords = getNumSwords(spellLevel, entity);
                    for (int i = 0; i < swords; i++) {
                        this.shootRandomSword(level, spellLevel, entity);
                    }
                }
            }
        }
    }

    public void shootTargetedSword(Level world, int spellLevel, LivingEntity caster, LivingEntity targeted){
        assert targeted != null;
        float radius = 3;
        if(targeted.getBbWidth() < targeted.getBbHeight()){
            radius += targeted.getBbHeight();
        }else{
            radius += targeted.getBbWidth();
        }
        double angle = Math.random() * 2 * Math.PI; //0-360
        Vec3 spawnPos = targeted.position().add(Math.cos(angle) * radius, Math.random() * radius ,Math.sin(angle) * radius);
        GatePortal gate = new GatePortal(world,caster);
        gate.setPos(spawnPos);
        Vec3 lookAngle = targeted.position().add(0,targeted.getBbHeight()/2,0).subtract(spawnPos);
        float xRot = ((float)(Mth.atan2(lookAngle.horizontalDistance(), lookAngle.y) * (180F / Math.PI)) - 90.0F);
        float yRot = ((float)(Mth.atan2(lookAngle.z, lookAngle.x) * (180F / Math.PI)) - 90);
        gate.setDamage(this.getDamage(spellLevel, caster) / 2);
        world.addFreshEntity(gate);
        gate.setXRot(Mth.wrapDegrees(xRot));
        gate.setYRot(Mth.wrapDegrees(yRot));
    }

    public void shootRandomSword(Level world, int spellLevel, LivingEntity entity) {
        double degree = Math.random() * Math.PI * 1.5 - Math.PI * 0.25;
        double radius = Math.random() * 3 + 1;
        radius *= getRadius(spellLevel,entity) / 3;
        double xOffset = Math.cos(degree) * radius;
        double yOffset = Math.sin(degree) * radius + 2;
        double cosPsi = Math.cos(Math.toRadians(entity.getYRot()));
        double sinPsi = Math.sin(Math.toRadians(entity.getYRot()));
        double cosTheta = Math.cos(Math.toRadians(entity.getXRot()));
        double sinTheta = Math.sin(Math.toRadians(entity.getXRot()));
        Vec3 origin = entity.position().add(xOffset* cosPsi- yOffset * sinTheta * sinPsi,yOffset * cosTheta,xOffset * sinPsi + yOffset * sinTheta * cosPsi);
        GatePortal gate = new GatePortal(world,entity);
        gate.setPos(origin);
        Vec3 lookAngle = entity.getLookAngle();
        float xRot = ((float)(Mth.atan2(lookAngle.horizontalDistance(), lookAngle.y) * (180F / Math.PI)) - 90.0F);
        float yRot = ((float)(Mth.atan2(lookAngle.z, lookAngle.x) * (180F / Math.PI)) - 90);
        gate.setDamage(this.getDamage(spellLevel,entity));
        world.addFreshEntity(gate);
        gate.setXRot(Mth.wrapDegrees(xRot));
        gate.setYRot(Mth.wrapDegrees(yRot));
    }

    public void shootHailSword(Level world, int spellLevel, LivingEntity entity) {
        double degree = Math.random() * Math.PI * 1.5 - Math.PI * 0.25;
        double radius = Math.random() * 3 + 1;
        radius *= getRadius(spellLevel,entity) / 3;
        double xOffset = Math.cos(degree) * radius;
        double yOffset = Math.sin(degree) * radius + 4;
        double cosPsi = Math.cos(Math.toRadians(entity.getYRot()));
        double sinPsi = Math.sin(Math.toRadians(entity.getYRot()));
        double cosTheta = Math.cos(Math.toRadians(-90));
        double sinTheta = Math.sin(Math.toRadians(-90));
        Vec3 origin = entity.position().add(xOffset* cosPsi- yOffset * sinTheta * sinPsi,yOffset * cosTheta + 1,xOffset * sinPsi + yOffset * sinTheta * cosPsi);
        GatePortal gate = new GatePortal(world,entity);
        gate.setPos(origin);
        Vec3 lookAngle = new Vec3(0,1,0);
        float xRot = ((float)(Mth.atan2(lookAngle.horizontalDistance(), lookAngle.y) * (180F / Math.PI)) - 90.0F);
        float yRot = ((float)(Mth.atan2(lookAngle.z, lookAngle.x) * (180F / Math.PI)) - 90);
        gate.setDamage(this.getDamage(spellLevel,entity));
        gate.swordHoming = true;
        world.addFreshEntity(gate);
        gate.setXRot(Mth.wrapDegrees(xRot));
        gate.setYRot(Mth.wrapDegrees(yRot));
    }

    private float getDamage(int spellLevel, LivingEntity entity) {
        return spellLevel + ((float) (getSpellPower(spellLevel, entity) * 0.5));
    }

    private float getRadius(int spellLevel, LivingEntity entity){
        return (float) spellLevel / 2 + 3 * (float)Math.sqrt(getSpellPower(spellLevel,entity) * 0.25f / Math.PI);
    }

    private int getNumSwords(int spellLevel, LivingEntity caster){
        return (int)(getSpellPower(spellLevel,caster) * 0.2) + spellLevel;
    }

    @Override
    public boolean canBeCraftedBy(Player player) {
        return false;
    }

    @Override
    public boolean allowCrafting() {
        return false;
    }

    @Override
    public boolean allowLooting() {
        return false;
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }
}
