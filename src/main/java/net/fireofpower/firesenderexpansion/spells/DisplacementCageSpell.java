package net.fireofpower.firesenderexpansion.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.spells.TeleportAoe;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class DisplacementCageSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "displacement_cage");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.radius", Utils.stringTruncation(getRadius(spellLevel, caster), 1)),
                Component.translatable("ui.irons_spellbooks.duration", Utils.timeFromTicks(getDuration(spellLevel, caster), 1))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .setMaxLevel(10)
            .setCooldownSeconds(25)
            .build();

    public DisplacementCageSpell() {
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 2;
        this.spellPowerPerLevel = 1;
        this.castTime = 20;
        this.baseManaCost = 40;
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
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, .15f, false);
        return true;
    }

    @Override
    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        Vec3 spawn = null;
        if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData castTargetingData) {
            var target = castTargetingData.getTarget((ServerLevel) world);
            if (target != null)
                spawn = target.position();
        }
        if (spawn == null) {
            spawn = Utils.raycastForEntity(world, entity, 32, true, .15f).getLocation();
            spawn = Utils.moveToRelativeGroundLevel(world, spawn, 6);
        }

        int duration = getDuration(spellLevel, entity);
        float radius = getRadius(spellLevel, entity);


        TeleportAoe aoeEntity = new TeleportAoe(world);
        aoeEntity.setOwner(entity);
        aoeEntity.setCircular();
        aoeEntity.setRadius(radius);
        aoeEntity.setDuration(duration);
        aoeEntity.setPos(spawn);
        world.addFreshEntity(aoeEntity);

        TargetedAreaEntity visualEntity = TargetedAreaEntity.createTargetAreaEntity(world, spawn, radius, 0xcf00ff);
        visualEntity.setDuration(duration);
        visualEntity.setOwner(aoeEntity);
        visualEntity.setShouldFade(true);

        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    private float getRadius(int spellLevel, LivingEntity caster) {
        return 8;
    }

    private int getDuration(int spellLevel, LivingEntity caster) {
        return spellLevel * 10 + (int)(getSpellPower(spellLevel,caster)) * 5;
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.PREPARE_CROSS_ARMS;
    }

    @Override
    public Vector3f getTargetingColor() {
        return SchoolRegistry.ENDER.get().getTargetingColor();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(net.fireofpower.firesenderexpansion.registries.SoundRegistry.DISPLACEMENT_CAGE_SPELL_CAST.get());
    }
}
