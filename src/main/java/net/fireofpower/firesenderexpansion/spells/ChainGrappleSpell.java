package net.fireofpower.firesenderexpansion.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.spells.ChainGrapple.EnderChainHookEntity;
import net.fireofpower.firesenderexpansion.entities.spells.ChainGrapple.EnderGrappleChain;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.rapier.UnstableSummonedRapierEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class ChainGrappleSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "chain_grapple");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .setMaxLevel(3)
            .setCooldownSeconds(35)
            .build();


    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster){
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(spellLevel,caster), 1))
        );
    }

    public ChainGrappleSpell(){
        this.manaCostPerLevel = 15;
        this.baseSpellPower = 12;
        this.spellPowerPerLevel = 3;
        this.castTime = 0;
        this.baseManaCost = 75;
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.THROW_SINGLE_ITEM;
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.SHADOW_SLASH.get());
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

    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        EnderChainHookEntity projectile = new EnderChainHookEntity(world, entity);
        projectile.setPos(entity.position().add(0,entity.getEyeHeight(),0));
        projectile.setDamage(10);
        projectile.shoot(entity.getLookAngle());

        EnderGrappleChain chain = new EnderGrappleChain(world, entity, projectile);
        chain.setHealth(100);
        chain.setLifetime(200);
        chain.setMaxLength(24);

        projectile.setChain(chain);

        world.addFreshEntity(projectile);
        world.addFreshEntity(chain);

        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    private float getDamage(int spellLevel, LivingEntity caster)
    {
        return ((float) (getSpellPower(spellLevel, caster) * 0.77));
    }
}
