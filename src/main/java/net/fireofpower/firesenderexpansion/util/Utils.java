package net.fireofpower.firesenderexpansion.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Timer;
import java.util.TimerTask;

public class Utils {
    public static boolean shouldBreakHollowCrystal(Projectile target){
        return target.getType().is(ModTags.BREAKS_HOLLOW_CRYSTAL);
    }
    public static boolean hasCurio(Player player, Item item)
    {
        return CuriosApi.getCuriosHelper().findEquippedCurio(item, player).isPresent();
    }

    public static ChunkPos getChunkPos(BlockPos blockPos) {
        return new ChunkPos(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }
}
