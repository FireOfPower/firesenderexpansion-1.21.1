package net.fireofpower.firesenderexpansion.items.curios;

import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.item.curios.SimpleDescriptiveCurio;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.fireofpower.firesenderexpansion.registries.PotionEffectRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import top.theillusivec4.curios.api.SlotContext;

public class AnchoringRingCurio extends SimpleDescriptiveCurio {
    private static final Component description = Component.translatable("item.firesenderexpansion.core_of_ender.desc").withStyle(ChatFormatting.DARK_PURPLE);
    public AnchoringRingCurio() {
        super(ItemPropertiesHelper.equipment().rarity(Rarity.RARE).stacksTo(1), Curios.RING_SLOT);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player)){
            return;
        }
        player.addEffect(new MobEffectInstance(PotionEffectRegistry.ANCHORED_POTION_EFFECT, 20, 0, false, false, false));

    }
}
