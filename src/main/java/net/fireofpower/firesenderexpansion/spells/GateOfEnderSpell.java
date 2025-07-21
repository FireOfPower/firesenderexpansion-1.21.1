package net.fireofpower.firesenderexpansion.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.spells.GateOfEnder.GatePortal;
import net.fireofpower.firesenderexpansion.entities.spells.ObsidianRod.ObsidianRod;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.UnstableWeaponEntity;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.claymore.UnstableSummonedClaymoreEntity;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.rapier.UnstableSummonedRapierEntity;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.sword.UnstableSummonedSwordEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

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
    public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
        if (playerMagicData != null && (playerMagicData.getCastDurationRemaining() + 1) % 5 == 0) {
            int swords = getNumSwords(spellLevel,entity);
            for(int i = 0; i < swords; i++) {
                this.shootRandomSword(level, spellLevel, entity);
            }
        }
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
        gate.shoot(entity.getLookAngle());
        gate.setDamage(this.getDamage(spellLevel,entity));
        //world.playSound((Player)null, origin.x, origin.y, origin.z, SoundRegistry.DIVINE_SMITE_WINDUP, SoundSource.PLAYERS, 2.0F, 1.0F);
        world.addFreshEntity(gate);
        gate.shootSword();
    }

    private float getDamage(int spellLevel, LivingEntity entity) {
        return spellLevel + ((float) (getSpellPower(spellLevel, entity) * 0.5));
    }

    private float getRadius(int spellLevel, LivingEntity entity){
        return spellLevel + 3 * (float)Math.sqrt(getSpellPower(spellLevel,entity) * 0.25f / Math.PI);
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
