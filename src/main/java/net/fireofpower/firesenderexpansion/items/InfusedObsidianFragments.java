package net.fireofpower.firesenderexpansion.items;

import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class InfusedObsidianFragments extends Item {

    private static final Component description = Component.translatable("item.firesenderexpansion.infused_obsidian_fragments.desc").withStyle(ChatFormatting.BLUE);


    public InfusedObsidianFragments() {
        super(ItemPropertiesHelper.equipment().rarity(Rarity.COMMON).fireResistant().stacksTo(64));
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext context, List<Component> lines, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, context, lines, pIsAdvanced);
        lines.add(description);
    }
}
