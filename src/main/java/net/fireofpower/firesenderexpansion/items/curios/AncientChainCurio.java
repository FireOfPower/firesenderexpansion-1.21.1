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

public class AncientChainCurio extends SimpleDescriptiveCurio {

    public AncientChainCurio() {
        super(ItemPropertiesHelper.equipment().rarity(Rarity.EPIC).stacksTo(1), Curios.NECKLACE_SLOT);
    }
}
