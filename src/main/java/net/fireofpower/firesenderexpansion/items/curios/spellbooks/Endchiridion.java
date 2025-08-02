package net.fireofpower.firesenderexpansion.items.curios.spellbooks;

import io.redspace.ironsspellbooks.item.SpellBook;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class Endchiridion extends SpellBook {
    private static final Component description = Component.translatable("item.firesenderexpansion.endchiridion.desc").withStyle(ChatFormatting.DARK_PURPLE);

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext context, List<Component> lines, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, context, lines, pIsAdvanced);
    }
}
