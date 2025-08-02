package net.fireofpower.firesenderexpansion.items.curios;

import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.item.curios.SimpleDescriptiveCurio;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class CoreOfEnderCurio extends SimpleDescriptiveCurio {

    private static final Component description = Component.translatable("item.firesenderexpansion.core_of_ender.desc").withStyle(ChatFormatting.DARK_PURPLE);

    public CoreOfEnderCurio() {
        super(ItemPropertiesHelper.equipment().rarity(Rarity.RARE).stacksTo(1), Curios.RING_SLOT);
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext context, List<Component> lines, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, context, lines, pIsAdvanced);
    }
}
