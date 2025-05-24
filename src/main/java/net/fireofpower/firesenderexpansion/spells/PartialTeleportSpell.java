package net.fireofpower.firesenderexpansion.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.effect.EchoingStrikesEffect;
import io.redspace.ironsspellbooks.entity.spells.EchoingStrikeEntity;
import io.redspace.ironsspellbooks.entity.spells.blood_needle.BloodNeedle;
import io.redspace.ironsspellbooks.spells.blood.AcupunctureSpell;
import io.redspace.ironsspellbooks.spells.ender.EchoingStrikesSpell;
import io.redspace.ironsspellbooks.spells.ender.MagicArrowSpell;
import io.redspace.ironsspellbooks.spells.lightning.LightningBoltSpell;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.entities.TeleportRend;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;

import java.util.List;

@AutoSpellConfig
public class PartialTeleportSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "partial_teleport");

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

    public PartialTeleportSpell(){
        this.manaCostPerLevel = 15;
        this.baseSpellPower = 10;
        this.spellPowerPerLevel = 5;
        this.castTime = 0;
        this.baseManaCost = 75;
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.ONE_HANDED_HORIZONTAL_SWING_ANIMATION;
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

    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, 0.15F);
    }

    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        ICastData targetEntityData = playerMagicData.getAdditionalCastData();
        if (targetEntityData instanceof TargetEntityCastData targetData) {
            LivingEntity targetEntity = targetData.getTarget((ServerLevel)world);
            if (targetEntity != null) {
                TeleportRend echo = new TeleportRend(world, entity, 0, 1.0F, 1);
                echo.setPos(targetEntity.getBoundingBox().getCenter().subtract((double)0.0F, (double)(echo.getBbHeight() * 0.5F), (double)0.0F));
                world.addFreshEntity(echo);
                DamageSources.applyDamage(targetEntity,getDamage(spellLevel,entity,targetEntity),this.getDamageSource(echo,entity));
            }
        }

        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    private float getDamage(int spellLevel, LivingEntity caster, LivingEntity target)
    {
        if(target instanceof ServerPlayer){
            ServerPlayer player = (ServerPlayer) target;
            MagicData magicData = MagicData.getPlayerMagicData(player);
            double maxMana = target.getAttributeValue(AttributeRegistry.MAX_MANA);
            if(maxMana != 0){
                return (float) ((getSpellPower(spellLevel,caster) * 0.77 * (1.5f - (magicData.getMana() / maxMana) / 2 )));
            }
        }
        return ((float)(getSpellPower(spellLevel, caster) * 0.77));
    }
    private float getDamage(int spellLevel, LivingEntity caster)
    {
        return ((float) (getSpellPower(spellLevel, caster) * 0.77));
    }
}
