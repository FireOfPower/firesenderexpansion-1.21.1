package net.fireofpower.firesenderexpansion.entities.mobs.void_wyrm;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.util.OwnerHelper;
import net.fireofpower.firesenderexpansion.registries.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;
import java.util.UUID;

public class VoidWyrm extends AbstractSpellCastingMob implements GeoEntity, IAnimatedAttacker, IMagicSummon, IMagicEntity {

    public static final Vec3 TORSO_OFFSET = new Vec3(0, 18, 0);
    public final Vec3[] cornerPins = {Vec3.ZERO, Vec3.ZERO, Vec3.ZERO, Vec3.ZERO};

    public boolean wantsToCastSpells;
    VoidWyrmPartEntity[] subEntities;
    public Vec3 normal = Vec3.ZERO, lastNormal = Vec3.ZERO;
    protected LivingEntity cachedSummoner;
    protected UUID summonerUUID;


    public VoidWyrm(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noCulling = true;
        subEntities = new VoidWyrmPartEntity[]{
                //head
                new VoidWyrmPartEntity(this, TORSO_OFFSET.add(0, 0, 16), 1.2f, .8f),
                //torso
                new VoidWyrmPartEntity(this, TORSO_OFFSET, 0.75f, 0.75f),
                //abdomen
                new VoidWyrmPartEntity(this, TORSO_OFFSET.add(0, 0, -20), 1.75f, 1.5f)
        };
        this.setId(ENTITY_COUNTER.getAndAdd(this.subEntities.length + 1) + 1); // Copy of forge fix to sub entity id's
        this.moveControl = createMoveControl();
    }

    public VoidWyrm(Level level) {
        this(EntityRegistry.VOID_WYRM.get(), level);
    }

    public static AttributeSupplier.Builder prepareAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.0)
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.MOVEMENT_SPEED, .25);
    }

    public double getCrouchHeightMultiplier() {
        return 1;
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        float y = getYRot();
        this.yRotO = y;
        this.yBodyRot = y;
        this.yBodyRotO = y;
        this.yHeadRot = y;
        this.yHeadRotO = y;
    }

    @Override
    public void tick() {
        super.tick();
        float scalar = getScale() * 4;
        Vec3 worldpos = this.position();
        // 1 -- 3
        // |    |  <- index map relative to forward
        // 0 -- 2
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                Vec3 vec = rotateWithBody(new Vec3((x - 0.5) * scalar, 0, (y - 0.5) * scalar));
                int maxStep = 2;
                cornerPins[x * 2 + y] = Utils.moveToRelativeGroundLevel(level(), worldpos.add(vec), maxStep, maxStep).subtract(worldpos);
            }
        }
        Vec3[] vx = cornerPins;
        Vec3 n0 = vx[1].subtract(vx[0]).cross(vx[2].subtract(vx[0]));
        Vec3 n1 = vx[3].subtract(vx[1]).cross(vx[0].subtract(vx[1]));
        Vec3 n2 = vx[0].subtract(vx[2]).cross(vx[3].subtract(vx[2]));
        Vec3 n3 = vx[2].subtract(vx[3]).cross(vx[1].subtract(vx[3]));
        Vec3 targetNormal = n0.add(n1).add(n2).add(n3).normalize();
        this.lastNormal = normal;
        this.normal = Utils.lerp(.2f, normal, targetNormal);
        var quat = Utils.rotationBetweenVectors(new Vector3f(0, 1, 0), Utils.v3f(normal));
        for (VoidWyrmPartEntity part : subEntities) {
            part.positionSelf(quat);
        }
    }

    @Override
    public void castComplete() {
        super.castComplete();
        wantsToCastSpells = false;
    }

    @Override
    public void initiateCastSpell(AbstractSpell spell, int spellLevel) {
        if (!wantsToCastSpells) {
            return;
        }
        if (spell.getCastType() == CastType.INSTANT) {
            serverTriggerAnimation("attack_fang_basic");
        } else {
            serverTriggerAnimation("long_cast");
        }
        super.initiateCastSpell(spell, spellLevel);
    }

    protected MoveControl createMoveControl() {
        return new MoveControl(this) {
            //This fixes a bug where a mob tries to path into the block it's already standing, and spins around trying to look "forward"
            //We nullify our rotation calculation if we are close to block we are trying to get to
            @Override
            protected float rotlerp(float pSourceAngle, float pTargetAngle, float pMaximumChange) {
                double d0 = this.wantedX - this.mob.getX();
                double d1 = this.wantedZ - this.mob.getZ();
                if (d0 * d0 + d1 * d1 < .5f) {
                    return pSourceAngle;
                } else {
                    return super.rotlerp(pSourceAngle, pTargetAngle, pMaximumChange * .25f);
                }
            }
        };
    }

    protected PathNavigation createNavigation(Level pLevel) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, pLevel);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    public @org.jetbrains.annotations.Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @org.jetbrains.annotations.Nullable SpawnGroupData spawnGroupData) {
        this.setNoGravity(true);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public boolean isAlliedTo(Entity pEntity) {
        return super.isAlliedTo(pEntity) || this.isAlliedHelper(pEntity);
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        return !this.shouldIgnoreDamage(pSource) && super.hurt(pSource, pAmount);
    }

    // NBT
    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.summonerUUID = OwnerHelper.deserializeOwner(pCompound);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        OwnerHelper.serializeOwner(pCompound, summonerUUID);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        return;
    }

    @Override
    protected void registerGoals() {
//        this.goalSelector.addGoal(1, new LeapBackGoal(this));
//        this.goalSelector.addGoal(1, new PounceGrappleGoal(this));
//        attackGoal = (IceSpiderAttackGoal) new IceSpiderAttackGoal(this, 1.1, 0, 40)
//                .setMoveset(List.of(
//                        new AttackAnimationData.Builder("attack_bite").length(22).attacks(new AttackKeyframe(14, new Vec3(0, 0, 1))).build(),
//                        new AttackAnimationData.Builder("attack_fang_basic").length(20).attacks(new AttackKeyframe(12, new Vec3(0, 0, 1))).build(),
//                        new AttackAnimationData.Builder("attack_right_swipe").length(14).attacks(new AttackKeyframe(10, new Vec3(0, 0.1, -1), new Vec3(0, 0, 1))).build()
//                ))
//                .setMeleeBias(1f, 1f)
//                .setSpells(List.of(SpellRegistry.SNOWBALL_SPELL.get(), SpellRegistry.ICE_SPIKES_SPELL.get()), List.of(), List.of(), List.of()).setSpellQuality(.75f, .75f);
//        this.goalSelector.addGoal(2, attackGoal
//        );
//        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 32, 0.08f));
//        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.7));
//        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
//
//        this.targetSelector.addGoal(1, new MomentHurtByTargetGoal(this, IceSpiderEntity.class));
//        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, livingEntity ->
//                livingEntity instanceof Player
//                        || livingEntity instanceof IronGolem));
//        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, livingEntity ->
//                livingEntity instanceof Animal
//                        || livingEntity instanceof AbstractVillager
//                        || livingEntity instanceof Raider));
    }

//    @Override
//    protected SoundEvent getHurtSound(DamageSource damageSource) {
//        return //SoundRegistry.ICE_SPIDER_HURT.get();
//    }
//
//    @Override
//    protected SoundEvent getDeathSound() {
//        return //SoundRegistry.ICE_SPIDER_DEATH.get();
//    }
//
//    @org.jetbrains.annotations.Nullable
//    @Override
//    protected SoundEvent getAmbientSound() {
//        return //SoundRegistry.ICE_SPIDER_AMBIENT.get();
//    }

    @Override
    protected LookControl createLookControl() {
        return super.createLookControl();
    }

//    @Override
//    protected BodyRotationControl createBodyControl() {
//        return new BodyRotationControl(this) {
//            @Override
//            public void rotateHeadTowardsFront() {
//                float rot = mob.yBodyRot;
//                super.rotateHeadTowardsFront();
//                if (rot != mob.yBodyRot) {
//                    VoidWyrm.this.updateWalkAnimation(1);
//                }
//            }
//        };
//    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return true;
    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
        this.discard();
//        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
//                getDeathSound(), SoundSource.PLAYERS, 0.5f, 1f);
    }

    public Vec3 rotateWithBody(Vec3 vec3) {
        float y = -this.yBodyRot + Mth.HALF_PI;
        return vec3.yRot(y * Mth.DEG_TO_RAD);
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        for (int i = 0; i < this.subEntities.length; i++) {
            this.subEntities[i].setId(id + i + 1);
        }
    }

    @Override
    public @Nullable PartEntity<?>[] getParts() {
        return subEntities;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void refreshDimensions() {
        super.refreshDimensions();
        for (VoidWyrmPartEntity part : this.subEntities) {
            part.refreshDimensions();
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
//        if (pKey == DATA_IS_CROUCHING) {
//            refreshDimensions();
//            crouchTick = tickCount;
//        } else if (pKey == Entity.DATA_POSE) {
//            if (this.getPose() == Pose.EMERGING) {
//                playAnimation("emerge_from_ground");
//                emergeTick = EMERGE_TIME;
//            }
//        }
    }

    RawAnimation animationToPlay = null;
    private final AnimationController<VoidWyrm> meleeController = new AnimationController<>(this, "melee_animations", 0, this::predicate);

    @Override
    public void playAnimation(String animationId) {
        animationToPlay = RawAnimation.begin().thenPlay(animationId);
    }

    private PlayState predicate(AnimationState<VoidWyrm> animationEvent) {
        var controller = animationEvent.getController();

        if (this.animationToPlay != null) {
            controller.forceAnimationReset();
            controller.setAnimation(animationToPlay);
            animationToPlay = null;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(meleeController);
    }

    @Override
    public boolean isAnimating() {
        return meleeController.getAnimationState() == AnimationController.State.RUNNING;
    }

    @Override
    public void onUnSummon() {
        //TODO: play death sound
    }
}
