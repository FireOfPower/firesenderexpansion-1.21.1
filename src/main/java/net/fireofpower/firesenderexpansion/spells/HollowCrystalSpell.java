package net.fireofpower.firesenderexpansion.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.HollowCrystal.HollowCrystal;
import net.fireofpower.firesenderexpansion.registries.PotionEffectRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AutoSpellConfig
public class HollowCrystalSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "hollow_crystal");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage"),
                Component.translatable("ui.fires_ender_expansion.recasts")
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(40)
            .build();

    public HollowCrystalSpell()
    {
        this.manaCostPerLevel = 25;
        this.baseSpellPower = 30;
        this.spellPowerPerLevel = 8;
        this.castTime = 1;
        this.baseManaCost = 55;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(getSpellId())) {
            playerMagicData.getPlayerRecasts().addRecast(new RecastInstance(getSpellId(), spellLevel, getRecastCount(spellLevel, entity), 80, castSource, null), playerMagicData);
        }
        if(entity.hasEffect(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT)){
            entity.addEffect(new MobEffectInstance(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT, 80, entity.getEffect(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT).getAmplifier() + 1, false, false, true));
        }else{
            entity.addEffect(new MobEffectInstance(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT, (int) getSpellPower(spellLevel, entity) * 20, 1, false, false, true));
        }


        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
        return 5;
    }

    @Override
    public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
        super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
        if(serverPlayer.hasEffect(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT)){
            //System.out.println("Launch a Hollow Crystal with " + serverPlayer.getEffect(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT).getAmplifier() + " Power");
            serverPlayer.removeEffect(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT);
            HollowCrystal hollowCrystal = new HollowCrystal(serverPlayer.level(), serverPlayer);
            hollowCrystal.setPos(serverPlayer.position().add(0, serverPlayer.getEyeHeight() + hollowCrystal.getBoundingBox().getYsize() * .5f, 0).add(serverPlayer.getForward().multiply(3,3,3)));
            hollowCrystal.shoot(serverPlayer.getLookAngle());
            hollowCrystal.setDeltaMovement(hollowCrystal.getDeltaMovement().multiply(0.25,0.25,0.25));
            hollowCrystal.setDamage(getDamage(serverPlayer));
            serverPlayer.level().addFreshEntity(hollowCrystal);
        }
    }

    public float getDamage(LivingEntity entity){
        return entity.getEffect(PotionEffectRegistry.HOLLOW_CRYSTAL_POTION_EFFECT).getAmplifier() * 20;
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
}
