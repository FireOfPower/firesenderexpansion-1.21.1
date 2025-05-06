package net.fireofpower.firesenderexpansion.items;

import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EndermanTravelGuide extends Item {
    private static final Component description = Component.translatable("item.firesenderexpansion.enderman_travel_guide.desc").withStyle(ChatFormatting.DARK_PURPLE);
    public EndermanTravelGuide() {
        super(ItemPropertiesHelper.equipment().rarity(Rarity.RARE).stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext context, List<Component> lines, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, context, lines, pIsAdvanced);
        lines.add(description);
    }
}
