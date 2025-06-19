package net.fireofpower.firesenderexpansion.registries;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.items.EndermanTravelGuide;
import net.fireofpower.firesenderexpansion.items.weapons.void_staff.VoidStaffItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    //public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, FiresEnderExpansion.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FiresEnderExpansion.MODID);
    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }

    public static final DeferredHolder<Item, VoidStaffItem> VOID_STAFF = ITEMS.register("void_staff", VoidStaffItem::new);
    public static final DeferredHolder<Item, Item> ENDERMAN_TRAVEL_GUIDE = ITEMS.register("enderman_travel_guide", EndermanTravelGuide::new);
    public static final DeferredItem<Item> PORPHYROMANCER_SPAWN_EGG = ITEMS.register("porphyromancer_spawn_egg", () -> new DeferredSpawnEggItem(EntityRegistry.PORPHYROMANCER, 0x0f0517, 0x3f195e, new Item.Properties()));


}
