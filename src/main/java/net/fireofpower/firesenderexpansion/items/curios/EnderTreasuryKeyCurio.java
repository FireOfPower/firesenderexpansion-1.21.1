package net.fireofpower.firesenderexpansion.items.curios;

import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.item.UniqueItem;
import io.redspace.ironsspellbooks.item.curios.SimpleDescriptiveCurio;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class EnderTreasuryKeyCurio extends SimpleDescriptiveCurio implements IPresetSpellContainer, UniqueItem {

    private static final Component description = Component.translatable("item.firesenderexpansion.ender_treasury_key.desc").withStyle(ChatFormatting.DARK_PURPLE);

    public EnderTreasuryKeyCurio() {
        super(ItemPropertiesHelper.equipment().rarity(Rarity.EPIC).stacksTo(1), Curios.NECKLACE_SLOT);
    }

    @Override
    public void initializeSpellContainer(ItemStack itemStack) {
        if (itemStack == null) {
            return;
        }

        if (!ISpellContainer.isSpellContainer(itemStack)) {
            var spellContainer = ISpellContainer.create(1, true, true).mutableCopy();
            spellContainer.addSpell(SpellRegistries.GATE_OF_ENDER.get(), 1, true);
            itemStack.set(ComponentRegistry.SPELL_CONTAINER, spellContainer.toImmutable());
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext context, List<Component> lines, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, context, lines, pIsAdvanced);
    }
}
