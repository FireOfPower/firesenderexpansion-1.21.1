package net.fireofpower.firesenderexpansion.mixins;

import io.redspace.ironsspellbooks.capabilities.magic.PocketDimensionManager;
import io.redspace.ironsspellbooks.capabilities.magic.PortalManager;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalEntity;
import net.fireofpower.firesenderexpansion.registries.PotionEffectRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.fireofpower.firesenderexpansion.events.ServerEvents.convertTicksToTime;

@Mixin(PocketDimensionManager.class)
public class PocketDimensionAntiSnapMixin {

//    @Inject(method = "tick*", at = @At("HEAD"), cancellable = true)
    @Shadow
    public BlockPos structurePosForPlayer(Player player){return null;}
    @Shadow
    public BlockPos findPortalForStructure(ServerLevel pocketDimension, BlockPos blockPos) {return null;}
/**
 * @author FireOfPower
 * @reason I need to prevent the pocket dimension from snapping the player back
 */
    @Overwrite
    public void tick(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!serverLevel.dimension().equals(PocketDimensionManager.POCKET_DIMENSION)) {
            return;
        }
        if (serverLevel.getGameTime() % 100 == 0) {
            serverLevel.players().forEach(player -> {
                if (!player.isCreative() && !player.isSpectator() && !player.hasEffect(PotionEffectRegistry.INFINITE_VOID_POTION_EFFECT)) {
                    int pocketX = (int) (player.getX() / PocketDimensionManager.POCKET_SPACING) * PocketDimensionManager.POCKET_SPACING;
                    int pocketZ = (int) (player.getZ() / PocketDimensionManager.POCKET_SPACING) * PocketDimensionManager.POCKET_SPACING;
                    if (player.getX() < pocketX || player.getX() > pocketX + 16
                            || player.getZ() < pocketZ || player.getZ() > pocketZ + 16) {
                        // snap player back into bounds
                        var blockPos = structurePosForPlayer(player);
                        var portalPos = findPortalForStructure(serverLevel, blockPos);
                        player.resetFallDistance();
                        player.moveTo(portalPos.getBottomCenter());
                    }
                }
            });
        }
    }
}
