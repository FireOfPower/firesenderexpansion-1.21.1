package net.fireofpower.firesenderexpansion.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.EarthquakeAoe;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class DragonsFurySpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "dragons_fury");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMaxLevel(7)
            .setCooldownSeconds(15)
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .build();

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", getDamageText(spellLevel,caster)),
                Component.translatable("ui.firesenderexpansion.force", Utils.stringTruncation(getForce(spellLevel,caster),1))
        );
    }

    public DragonsFurySpell(){
        this.manaCostPerLevel = 15;
        this.baseSpellPower = 10;
        this.spellPowerPerLevel = 5;
        this.castTime = 16;
        this.baseManaCost = 75;
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.OVERHEAD_MELEE_SWING_ANIMATION;
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
        return CastType.LONG;
    }

    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        float radius = 5f;
        float range = 1.7f;
        Vec3 smiteLocation = Utils.raycastForBlock(entity.level(), entity.getEyePosition(), entity.getEyePosition().add(entity.getForward().multiply(range, 0, range)), ClipContext.Fluid.NONE).getLocation();
        Vec3 particleLocation = entity.level().clip(new ClipContext(smiteLocation, smiteLocation.add(0, -2, 0), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty())).getLocation().add(0, 0.1, 0);
        MagicManager.spawnParticles(entity.level(), new BlastwaveParticleOptions(SchoolRegistry.ENDER.get().getTargetingColor(), radius * 2),
                particleLocation.x, particleLocation.y, particleLocation.z, 1, 0, 0, 0, 0, true);
        //MagicManager.spawnParticles(entity.level(), ParticleHelper.COMET_FOG, particleLocation.x, particleLocation.y, particleLocation.z, 50, 0, 0, 0, 1, false);
        var entities = entity.level().getEntities(entity, AABB.ofSize(smiteLocation, radius * 2, radius * 4, radius * 2));
        var damageSource = this.getDamageSource(entity);

        EarthquakeAoe aoe = new EarthquakeAoe(level);
        aoe.moveTo(smiteLocation);
        aoe.setOwner(entity);
        aoe.setCircular();
        aoe.setRadius(6);
        aoe.setDuration(20);
        aoe.setDamage(0);
        aoe.setSlownessAmplifier(0);
        level.addFreshEntity(aoe);

        for (Entity targetEntity : entities) {
            if (targetEntity.isAlive() && targetEntity.isPickable() && Utils.hasLineOfSight(entity.level(), smiteLocation.add(0, 1, 0), targetEntity.getBoundingBox().getCenter(), true)) {
                if (DamageSources.applyDamage(targetEntity, getDamage(spellLevel, entity), damageSource)) {
                    handleKnockback(entity,(LivingEntity) targetEntity,spellLevel);
                    EnchantmentHelper.doPostAttackEffects((ServerLevel) entity.level(), targetEntity, damageSource);
                }
            }
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    public void handleKnockback(LivingEntity entity, LivingEntity targetEntity, int spellLevel){
        Vec3 angleVector = entity.position().subtract(targetEntity.position()).normalize();
        var vec = angleVector.multiply(2, 1, 2).normalize().scale(-1 * getForce(spellLevel,entity));
        if(targetEntity instanceof ServerPlayer serverPlayer){
            serverPlayer.hurtMarked = true;
        }
        //System.out.println("Attempted to move " + targetEntity.getType()  + " vec is " + vec);
        targetEntity.setDeltaMovement(targetEntity.getDeltaMovement().add(vec));
        targetEntity.addEffect(new MobEffectInstance(MobEffectRegistry.AIRBORNE, 60, 1));
        Vec3 particleLocation = targetEntity.position();
        MagicManager.spawnParticles(entity.level(), ParticleHelper.UNSTABLE_ENDER, particleLocation.x, particleLocation.y + targetEntity.getBbHeight() / 2, particleLocation.z, 50, 0, 0, 0, 0.2, false);
    }

    @Override
    public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
        //due to melee animation timing, we do not want cast time attribute to affect this spell
        return getCastTime(spellLevel);
    }

    private float getDamage(int spellLevel, LivingEntity entity) {
        return getSpellPower(spellLevel, entity)/5 + getAdditionalDamage(entity);
    }

    private float getForce(int spellLevel, LivingEntity entity) {
        return getSpellPower(spellLevel, entity) * 0.1f;
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.ENDER_DRAGON_AMBIENT);
    }

    private float getAdditionalDamage(LivingEntity entity) {
        if (entity == null) {
            return 0;
        }
        //var weaponItem = entity.getWeaponItem();
//        if (!weaponItem.isEmpty() && weaponItem.has(DataComponents.ENCHANTMENTS)) {
//            weaponDamage += Utils.processEnchantment(entity.level(), Enchantments.SMITE, EnchantmentEffectComponents.DAMAGE, weaponItem.get(DataComponents.ENCHANTMENTS));
//        }
        //System.out.println(Utils.getWeaponDamage(entity));
        return Utils.getWeaponDamage(entity);
    }

    private String getDamageText(int spellLevel, LivingEntity entity) {
        if (entity != null) {
            float weaponDamage = getAdditionalDamage(entity);
            String plus = "";
            if (weaponDamage > 0) {
                plus = String.format(" (+%s)", Utils.stringTruncation(weaponDamage, 1));
            }
            String damage = Utils.stringTruncation(getDamage(spellLevel, entity), 1);
            return damage + plus;
        }
        return "" + getSpellPower(spellLevel, entity);
    }
}
