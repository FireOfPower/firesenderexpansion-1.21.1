package net.fireofpower.firesenderexpansion.items.curios;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.item.curios.SimpleDescriptiveCurio;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class CrystalHeartCurio extends SimpleDescriptiveCurio {
    private static final Component description = Component.translatable("item.firesenderexpansion.crystal_heart.desc").withStyle(ChatFormatting.DARK_PURPLE);

    public CrystalHeartCurio() {
        super(ItemPropertiesHelper.equipment().rarity(Rarity.EPIC).stacksTo(1), Curios.RING_SLOT);
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext context, List<Component> lines, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, context, lines, pIsAdvanced);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attr = LinkedHashMultimap.create();
        attr.put(AttributeRegistry.ENDER_SPELL_POWER, new AttributeModifier(id, 0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        attr.put(AttributeRegistry.CAST_TIME_REDUCTION, new AttributeModifier(id, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

        return attr;
    }
}
