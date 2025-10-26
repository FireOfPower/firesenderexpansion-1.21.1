package net.fireofpower.firesenderexpansion.entities.mobs.void_wyrm;

import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class VoidWyrmPartEntity extends PartEntity<VoidWyrm> {
    public final VoidWyrm parent;
    private final EntityDimensions size;
    private final Vec3 baseOffset;
    private final boolean collision;

    public VoidWyrmPartEntity(VoidWyrm pParentMob, Vec3 offset16, float pWidth, float pHeight, boolean collision) {
        super(pParentMob);
        float inflate = 0.1f;
        this.size = EntityDimensions.scalable(pWidth + inflate * 2, pHeight + inflate * 2);
        this.parent = pParentMob;
        this.refreshDimensions();
        this.baseOffset = offset16.scale(0.0625f).subtract(0, inflate, 0);
        this.collision = collision;
    }

    public VoidWyrmPartEntity(VoidWyrm pParentMob, Vec3 offset16, float pWidth, float pHeight) {
        this(pParentMob, offset16, pWidth, pHeight, false);
    }

    public void positionSelf(Quaternionf normal) {
        Vec3 parentPos = parent.position();
        Vec3 offset = baseOffset.multiply(1, parent.getCrouchHeightMultiplier(), 1);
        //Vec3 localVec = Utils.v3d(normal.transform(Utils.v3f(parent.rotateWithBody(offset))));
        //hardSetPos(parentPos.add(localVec.scale(parent.getScale())));
    }

    @Override
    public boolean canBeCollidedWith() {
        return collision;
    }

    private void hardSetPos(Vec3 newVector) {
        this.setPos(newVector);
        this.setDeltaMovement(newVector);
        var vec3 = this.position();
        this.xo = vec3.x;
        this.yo = vec3.y;
        this.zo = vec3.z;
        this.xOld = vec3.x;
        this.yOld = vec3.y;
        this.zOld = vec3.z;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        //return parent.hurt(this, pSource, pAmount);
        return true;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public ItemStack getPickResult() {
        return this.parent.getPickResult();
    }

    @Override
    public boolean is(Entity pEntity) {
        return this == pEntity || this.parent == pEntity;
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        return this.size.scale(parent.getScale());
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
}
