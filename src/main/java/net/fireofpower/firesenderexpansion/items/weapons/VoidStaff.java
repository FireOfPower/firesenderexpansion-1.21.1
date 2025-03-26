package net.fireofpower.firesenderexpansion.items.weapons;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.item.weapons.ExtendedWeaponTier;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.fireofpower.firesenderexpansion.registries.SpellRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class VoidStaff extends StaffItem implements IPresetSpellContainer {

    public VoidStaff() {
        super(
//                WeaponTiers.VOID_STAFF,
                ItemPropertiesHelper.equipment().fireResistant().stacksTo(1).rarity(Rarity.EPIC).attributes(ExtendedSwordItem.createAttributes(WeaponTiers.VOID_STAFF))
//                SpellDataRegistryHolder.of(
//                        new SpellDataRegistryHolder(SpellRegistries.HOLLOW_CRYSTAL, 5),
//                        new SpellDataRegistryHolder(SpellRegistries.INFINITE_VOID,1))
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.firesenderexpansion:void_staff_description").
                withStyle(ChatFormatting.DARK_PURPLE).
                withStyle(ChatFormatting.ITALIC));
    }



    @Override
    public void initializeSpellContainer(ItemStack itemStack) {
        if (itemStack == null) {
            return;
        }

        if (!ISpellContainer.isSpellContainer(itemStack)) {
            var spellContainer = ISpellContainer.create(1, true, false).mutableCopy();
            spellContainer.addSpell(SpellRegistries.INFINITE_VOID.get(), 1, true);
            itemStack.set(ComponentRegistry.SPELL_CONTAINER, spellContainer.toImmutable());
        }
    }
}
