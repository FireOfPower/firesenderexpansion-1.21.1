package net.fireofpower.firesenderexpansion.entities.spells.ObsidianRod;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.fireofpower.firesenderexpansion.Config;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.fireofpower.firesenderexpansion.registries.ItemRegistry;
import net.fireofpower.firesenderexpansion.registries.EffectRegistry;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;


public class ObsidianRod extends AbstractMagicProjectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Integer> EFFECT_LENGTH = SynchedEntityData.defineId(ObsidianRod.class, EntityDataSerializers.INT);


    public ObsidianRod(Level level, LivingEntity shooter, int effectLength) {
        this((EntityType) EntityRegistry.OBSIDIAN_ROD.get(), level);
        this.setOwner(shooter);
        setEffectLength(effectLength);
    }

    public ObsidianRod(EntityType<ObsidianRod> obsidianRodEntityType, Level level) {
        super(obsidianRodEntityType,level);
    }

    public void shoot(Vec3 rotation, float inaccuracy) {
        double speed = rotation.length();
        Vec3 offset = Utils.getRandomVec3((double)1.0F).normalize().scale((double)inaccuracy);
        Vec3 motion = rotation.normalize().add(offset).normalize().scale(speed);
        super.shoot(motion);
    }

    protected void onHitBlock(BlockHitResult blockHitResult) {
        BlockState hitBlock = level().getBlockState(blockHitResult.getBlockPos());
        Block block = hitBlock.getBlock();
        if(block.equals(Blocks.ANCIENT_DEBRIS) && Config.ALLOW_INFUSED_OBSIDIAN_FRAGMENTS.get()){
            ItemStack result = new ItemStack(ItemRegistry.INFUSED_OBSIDIAN_FRAGMENTS, 1);
            ItemEntity entity = new ItemEntity(level(), this.position().x(), this.position().y(), this.position().z(), result);
            level().addFreshEntity(entity);
            level().destroyBlock(blockHitResult.getBlockPos(), false);
            level().playLocalSound(this.position().x(), this.position().y(), this.position().z(), Blocks.OBSIDIAN.asItem().getBreakingSound(), SoundSource.BLOCKS, 15f, 1f, true);
        }
        super.onHitBlock(blockHitResult);
        this.discard();
    }
    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        var target = pResult.getEntity();
        DamageSources.applyDamage(target, damage,
                SpellRegistries.OBSIDIAN_ROD.get().getDamageSource(this, getOwner()));
        if (target instanceof LivingEntity livingTarget)
        {
            livingTarget.addEffect(new MobEffectInstance(EffectRegistry.ANCHORED_EFFECT, getEffectLength(), 0));
        }
        discard();
    }

    public void setEffectLength(int duration){
        this.entityData.set(EFFECT_LENGTH,duration);
    }

    public int getEffectLength() {
        return this.entityData.get(EFFECT_LENGTH);
    }

    @Override
    public void trailParticles() {

    }

    @Override
    public void impactParticles(double v, double v1, double v2) {
        MagicManager.spawnParticles(this.level(), ParticleHelper.PORTAL_FRAME, v, v1, v2, 12, 0.08, 0.08, 0.08, 0.3, false);
    }

    @Override
    public float getSpeed() {
        return 3.0F;
    }

    @Override
    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.empty();
    }


    private final AnimationController<ObsidianRod> animationController = new AnimationController<>(this, "controller", 0, this::predicate);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(animationController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private PlayState predicate(AnimationState<ObsidianRod> event){
        event.getController().setAnimation(DefaultAnimations.IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setEffectLength(tag.getInt("Effect Length"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Effect Length",this.getEffectLength());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(EFFECT_LENGTH, 0);
    }
}

