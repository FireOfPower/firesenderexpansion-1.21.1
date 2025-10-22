package net.fireofpower.firesenderexpansion.registries;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.items.InfusedObsidianFragments;
import net.fireofpower.firesenderexpansion.items.curios.CrystalHeartCurio;
import net.fireofpower.firesenderexpansion.items.weapons.void_staff_holder.VoidStaffHolder;
import net.fireofpower.firesenderexpansion.items.armor.end_lord.EndLordArmorItem;
import net.fireofpower.firesenderexpansion.items.curios.AnchoringRingCurio;
import net.fireofpower.firesenderexpansion.items.curios.CoreOfEnderCurio;
import net.fireofpower.firesenderexpansion.items.StabilizedCoreOfEnder;
import net.fireofpower.firesenderexpansion.items.curios.EnderTreasuryKeyCurio;
import net.fireofpower.firesenderexpansion.items.weapons.void_staff.VoidStaffItem;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.function.Supplier;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FiresEnderExpansion.MODID);
    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }

    //Items
    public static final DeferredHolder<Item, VoidStaffItem> VOID_STAFF = ITEMS.register("void_staff", VoidStaffItem::new);
    public static final DeferredHolder<Item,Item> INFUSED_OBSIDIAN_FRAGMENTS = ITEMS.register("infused_obsidian_fragments", InfusedObsidianFragments::new);
    public static final DeferredHolder<Item,Item> STABILIZED_CORE_OF_ENDER = ITEMS.register("stabilized_core_of_ender", StabilizedCoreOfEnder::new);
    public static final DeferredItem<Item> PORPHYROMANCER_SPAWN_EGG = ITEMS.register("porphyromancer_spawn_egg", () -> new DeferredSpawnEggItem(EntityRegistry.PORPHYROMANCER, 0x0f0517, 0x3f195e, new Item.Properties()));
    public static final DeferredItem<Item> VOID_STAFF_HOLDER = ITEMS.register("void_staff_holder", VoidStaffHolder::new);

    //Armor
    public static final DeferredHolder<Item, Item> END_LORD_HELMET = ITEMS.register("end_lord_helmet", () -> new EndLordArmorItem(ArmorItem.Type.HELMET, ItemPropertiesHelper
            .equipment(1)
            .fireResistant()
            .rarity(Rarity.EPIC)
            .durability(ArmorItem.Type.HELMET.getDurability(37))));
    public static final DeferredHolder<Item, Item> END_LORD_CHESTPLATE = ITEMS.register("end_lord_chestplate", () -> new EndLordArmorItem(ArmorItem.Type.CHESTPLATE, ItemPropertiesHelper
            .equipment(1)
            .fireResistant()
            .rarity(Rarity.EPIC)
            .durability(ArmorItem.Type.CHESTPLATE.getDurability(37))));
    public static final DeferredHolder<Item, Item> END_LORD_LEGGINGS = ITEMS.register("end_lord_leggings", () -> new EndLordArmorItem(ArmorItem.Type.LEGGINGS, ItemPropertiesHelper
            .equipment(1)
            .fireResistant()
            .rarity(Rarity.EPIC)
            .durability(ArmorItem.Type.LEGGINGS.getDurability(37))));
    public static final DeferredHolder<Item, Item> END_LORD_BOOTS = ITEMS.register("end_lord_boots", () -> new EndLordArmorItem(ArmorItem.Type.BOOTS, ItemPropertiesHelper
            .equipment(1)
            .fireResistant()
            .rarity(Rarity.EPIC)
            .durability(ArmorItem.Type.BOOTS.getDurability(37))));

    //Curios
    public static final Supplier<CurioBaseItem> CORE_OF_ENDER = ITEMS.register("core_of_ender", CoreOfEnderCurio::new);
    public static final Supplier<CurioBaseItem> ANCHORING_RING = ITEMS.register("anchoring_ring", AnchoringRingCurio::new);
    public static final Supplier<CurioBaseItem> ENDER_TREASURY_KEY = ITEMS.register("ender_treasury_key", EnderTreasuryKeyCurio::new);
    public static final Supplier<CurioBaseItem> CRYSTAL_HEART = ITEMS.register("crystal_heart", CrystalHeartCurio::new);

    public static final DeferredHolder<Item, Item> ENDCHIRIDION = ITEMS.register("endchiridion", () ->
            new SpellBook(10).withSpellbookAttributes(
                    new AttributeContainer(AttributeRegistry.SPELL_POWER, .05F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                    new AttributeContainer(AttributeRegistry.ENDER_SPELL_POWER,.05f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                    new AttributeContainer(AttributeRegistry.CAST_TIME_REDUCTION,.2F,AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                    new AttributeContainer(AttributeRegistry.MAX_MANA, 200, AttributeModifier.Operation.ADD_VALUE)
            ));


    public static Collection<DeferredHolder<Item, ? extends Item>> getItems()
    {
        return ITEMS.getEntries();
    }
}
