package net.fireofpower.firesenderexpansion.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.effect_dimension_matcher.EffectDetails;
import net.fireofpower.firesenderexpansion.effect_dimension_matcher.EffectDimensionMatcher;
import net.fireofpower.firesenderexpansion.registries.ItemRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class DimensionalAdaptationSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "dimensional_adaptation");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMaxLevel(6)
            .setCooldownSeconds(60)
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .build();

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.firesenderexpansion.duration_multiplier", Utils.stringTruncation(getDurationMultiplier(spellLevel,caster), 1))
        );
    }

    public DimensionalAdaptationSpell()
    {
        this.manaCostPerLevel = 25;
        this.baseSpellPower = 10;
        this.spellPowerPerLevel = 2;
        this.castTime = 0;
        this.baseManaCost = 55;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        //System.out.println("Applying " + EffectDimensionMatcher.INSTANCE.getEffectDetailsForDimension(entity.level().dimension().location().getNamespace() + ":" + entity.level().dimension().location().getPath()).getEffect());
        if(EffectDimensionMatcher.INSTANCE.getEffectDetailsForDimension(entity.level().dimension().location().getNamespace() + ":" + entity.level().dimension().location().getPath()).getEffect() == null){
            if(entity instanceof ServerPlayer serverPlayer){
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("msg.firesenderexpansion.cannot_adapt")
                        .withStyle(s -> s.withColor(TextColor.fromRgb(0xF35F5F)))));
                serverPlayer.level().playSound(null, serverPlayer.position().x, serverPlayer.position().y, serverPlayer.position().z,
                        SoundEvents.ENDERMAN_SCREAM, SoundSource.PLAYERS, 0.5f, 1f);
            }
        }else {
            EffectDetails details = EffectDimensionMatcher.INSTANCE.getEffectDetailsForDimension(entity.level().dimension().location().getNamespace() + ":" + entity.level().dimension().location().getPath());
            Holder<MobEffect> savedEffect = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.tryBySeparator(details.getEffect(), ':')));
            entity.addEffect(new MobEffectInstance(savedEffect, (int) (getDurationMultiplier(spellLevel, entity) * details.getDuration()), details.getAmplifier(), false, false, true));
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    public float getDurationMultiplier(int spellLevel, LivingEntity entity){
        return getSpellPower(spellLevel,entity) / 10;
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
    public Component getLockedMessage() {
        return Component.translatable("msg.firesenderexpansion.dimensional_adaptation");
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
        Item guide = ItemRegistry.ENDCHIRIDION.get();
        //return player.getMainHandItem().is(guide);
        return player.getInventory().contains(guide.getDefaultInstance()) || net.fireofpower.firesenderexpansion.util.Utils.hasCurio(player,guide);
    }
}
