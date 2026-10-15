package net.fireofpower.firesenderexpansion.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.spells.ChainGrapple.EnderChainHookEntity;
import net.fireofpower.firesenderexpansion.entities.spells.ChainGrapple.EnderGrappleChain;
import net.fireofpower.firesenderexpansion.entities.spells.UnstableSwords.rapier.UnstableSummonedRapierEntity;
import net.fireofpower.firesenderexpansion.registries.ItemRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;
import java.util.Optional;

public class ChainGrappleSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "chain_grapple");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.EPIC)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .setMaxLevel(1)
            .setCooldownSeconds(10)
            .build();


    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster){
        return List.of(
                Component.translatable("ui.irons_spellbooks.distance", Utils.stringTruncation(getDistance(spellLevel,caster), 1))
        );
    }

    public ChainGrappleSpell(){
        this.manaCostPerLevel = 15;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 3;
        this.castTime = 0;
        this.baseManaCost = 35;
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
        if(entity instanceof Player player && net.fireofpower.firesenderexpansion.util.Utils.hasCurio(player, ItemRegistry.ANCIENT_CHAIN.get()) && !playerMagicData.getPlayerRecasts().hasRecastForSpell(getSpellId())){
            playerMagicData.getPlayerRecasts().addRecast(new RecastInstance(getSpellId(), spellLevel, 3, 80, castSource, null), playerMagicData);
        }
        EnderChainHookEntity projectile = new EnderChainHookEntity(world, entity);
        projectile.setPos(entity.position().add(0,entity.getEyeHeight(),0));
        projectile.setDamage(10);
        int multiplier = 1;
        if(entity instanceof Player player && net.fireofpower.firesenderexpansion.util.Utils.hasCurio(player, ItemRegistry.ANCIENT_CHAIN.get())){
            multiplier = 2;
        }
        projectile.shoot(entity.getLookAngle().normalize().scale(multiplier));

        EnderGrappleChain chain = new EnderGrappleChain(world, entity, projectile);
        chain.setHealth(10);
        chain.setLifetime(200);
        chain.setMaxLength(getDistance(spellLevel,entity));

        projectile.setChain(chain);

        world.addFreshEntity(projectile);
        world.addFreshEntity(chain);

        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    private float getDistance(int spellLevel, LivingEntity caster)
    {
        return getSpellPower(spellLevel, caster);
    }
}
