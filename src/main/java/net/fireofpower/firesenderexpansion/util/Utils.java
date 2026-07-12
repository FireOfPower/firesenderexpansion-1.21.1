package net.fireofpower.firesenderexpansion.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.function.Predicate;

public class Utils {
    public static boolean shouldBreakHollowCrystal(Projectile target){
        return target.getType().is(ModTags.BREAKS_HOLLOW_CRYSTAL);
    }
    public static boolean hasCurio(Player player, Item item)
    {
        if(!CuriosApi.getCuriosInventory(player).isPresent()) { return false; }
        return CuriosApi.getCuriosInventory(player).stream().anyMatch(iCuriosItemHandler -> iCuriosItemHandler.isEquipped(item));
    }

    public static ChunkPos getChunkPos(BlockPos blockPos) {
        return new ChunkPos(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

    public static int rgbToInt(int r, int g, int b){
        int rgbInteger = (r << 16) | (g << 8) | b;
        return rgbInteger;
    }
}
