package net.fireofpower.firesenderexpansion.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.spells.InfiniteVoid.InfiniteVoid;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

@AutoSpellConfig
public class InfiniteVoidSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "infinite_void");
    private final int duration = 15;
    private final int range = 20;

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(duration * 20, 1)),
                Component.translatable("ui.aces_spell_utils.refinement", Utils.stringTruncation(getRefinement(spellLevel,caster), 0))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .setMaxLevel(1)
            .setCooldownSeconds(600)
            .build();

    public InfiniteVoidSpell()
    {
        this.manaCostPerLevel = 0;
        this.baseSpellPower = 30;
        this.spellPowerPerLevel = 8;
        this.castTime = 60;
        this.baseManaCost = 500;
    }
    
    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        InfiniteVoid infiniteVoid = new InfiniteVoid(entity.level(), entity);
        infiniteVoid.setPos(entity.position().add(0, entity.getEyeHeight() - infiniteVoid.getBoundingBox().getYsize() * 0.5f, 0));
        //infiniteVoid.setDeltaMovement(new Vec3(0, 0, 0));
        infiniteVoid.setRefinement((int)(getRefinement(spellLevel, entity)));
        infiniteVoid.setRadius(range);
        infiniteVoid.setDuration(duration);
        entity.level().addFreshEntity(infiniteVoid);
        CameraShakeManager.addCameraShake(new CameraShakeData(level,40, entity.position(), 20));
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 10, false, false, false));
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public void onClientPreCast(Level level, int spellLevel, LivingEntity entity, InteractionHand hand, @Nullable MagicData playerMagicData) {
        super.onClientPreCast(level, spellLevel, entity, hand, playerMagicData);
        // attempt to align body with arms so the sword animation plays more smoothly
        entity.setYBodyRot(entity.getYRot());
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.BEACON_ACTIVATE);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.BLACK_HOLE_CAST.get());
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return FEESpellAnimations.ANIMATION_INFINITE_VOID_CAST;
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

    public float getRefinement(int spellLevel, LivingEntity caster){
        return getSpellPower(spellLevel, caster) * 100;
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
}
