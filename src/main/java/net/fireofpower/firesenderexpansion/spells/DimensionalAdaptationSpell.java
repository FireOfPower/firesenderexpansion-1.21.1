package net.fireofpower.firesenderexpansion.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.registries.ItemRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class DimensionalAdaptationSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "dimensional_adaptation");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMaxLevel(5)
            .setCooldownSeconds(60)
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .build();

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getDuration(spellLevel,caster), 1))
        );
    }

    public DimensionalAdaptationSpell()
    {
        this.manaCostPerLevel = 25;
        this.baseSpellPower = 30;
        this.spellPowerPerLevel = 8;
        this.castTime = 0;
        this.baseManaCost = 55;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if(entity.level().dimension() == Level.OVERWORLD){
            entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, getDuration(spellLevel,entity), 0, false, false, true));
        } else if (entity.level().dimension() == Level.NETHER){
            entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, getDuration(spellLevel,entity), 0, false, false, true));
        } else if (entity.level().dimension() == Level.END){
            entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, getDuration(spellLevel,entity), 0, false, false, true));
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    public int getDuration(int spellLevel, LivingEntity entity){
        if(entity == null){
            return (int) getSpellPower(spellLevel, entity) * 7;
        }
        if(entity.level().dimension() == Level.OVERWORLD){
            return (int) getSpellPower(spellLevel, entity) * 7;
        } else if (entity.level().dimension() == Level.NETHER){
            return (int) getSpellPower(spellLevel, entity) * 5;
        } else if (entity.level().dimension() == Level.END){
            return (int) getSpellPower(spellLevel, entity) * 7;
        } else{
            return 0;
        }
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.ENDERMAN_AMBIENT);
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
    public boolean canBeCraftedBy(Player player) {
        Item guide = ItemRegistry.ENDERMAN_TRAVEL_GUIDE.get();
        return player.getMainHandItem().is(guide);
    }
}
