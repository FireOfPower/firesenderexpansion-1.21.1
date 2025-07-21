package net.fireofpower.firesenderexpansion.items;

import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class StabilizedCoreOfEnder extends Item {
    public StabilizedCoreOfEnder() {
        super(ItemPropertiesHelper.equipment().rarity(Rarity.COMMON).stacksTo(64));
    }
}
