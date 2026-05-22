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
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class ArcaneSliceSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "arcane_slice");

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

    public ArcaneSliceSpell(){
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

    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, 0.15F);
    }

    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        ICastData targetEntityData = playerMagicData.getAdditionalCastData();
        if (targetEntityData instanceof TargetEntityCastData targetData) {
            LivingEntity targetEntity = targetData.getTarget((ServerLevel)world);
            if (targetEntity != null) {
                double degree = Math.PI / (Math.random() * 2 + 4);
                if(Math.random() < 0.5){
                    degree *= -1;
                }
                double cosPsi = Math.cos(Math.toRadians(entity.getYRot()));
                double sinPsi = Math.sin(Math.toRadians(entity.getYRot()));
                double cosTheta = Math.cos(Math.toRadians(entity.getXRot()));
                double sinTheta = Math.sin(Math.toRadians(entity.getXRot()));
                for(int i = -20; i < 20; i++) {
                    double radius = i * 0.1;
                    double xOffset = Math.cos(degree) * radius;
                    double yOffset = Math.sin(degree) * radius;
                    Vec3 origin = targetEntity.position().add(xOffset * cosPsi - yOffset * sinTheta * sinPsi, yOffset * cosTheta, xOffset * sinPsi + yOffset * sinTheta * cosPsi);
                    ((ServerLevel) world).sendParticles(ParticleRegistry.UNSTABLE_ENDER_PARTICLE.get(), origin.x, origin.y + targetEntity.getBbHeight()/2, origin.z,1,0,0,0,0);
                }
                DamageSources.applyDamage(targetEntity,getDamage(spellLevel,entity,targetEntity),this.getDamageSource(entity));
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
