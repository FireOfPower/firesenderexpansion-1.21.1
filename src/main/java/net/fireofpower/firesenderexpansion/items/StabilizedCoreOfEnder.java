package net.fireofpower.firesenderexpansion.items;

import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class StabilizedCoreOfEnder extends Item {
    public StabilizedCoreOfEnder() {
        super(ItemPropertiesHelper.equipment().rarity(Rarity.COMMON).stacksTo(64));
    }
}
