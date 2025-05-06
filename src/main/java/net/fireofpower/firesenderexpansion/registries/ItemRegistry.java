package net.fireofpower.firesenderexpansion.registries;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.StaffTier;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.items.EndermanTravelGuide;
import net.fireofpower.firesenderexpansion.items.weapons.VoidStaff;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, FiresEnderExpansion.MODID);
    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }

    public static final DeferredHolder<Item, VoidStaff> VOID_STAFF = ITEMS.register("void_staff", VoidStaff::new);
    public static final DeferredHolder<Item, Item> ENDERMAN_TRAVEL_GUIDE = ITEMS.register("enderman_travel_guide", EndermanTravelGuide::new);


}
