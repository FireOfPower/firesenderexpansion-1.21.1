package net.fireofpower.firesenderexpansion.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.registries.ItemRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addItemStackInfo(new ItemStack(ItemRegistry.VOID_STAFF.get()), Component.translatable("item.firesenderexpansion.void_staff.guide"));
        registration.addItemStackInfo(new ItemStack(ItemRegistry.INFUSED_OBSIDIAN_FRAGMENTS.get()), Component.translatable("item.firesenderexpansion.infused_obsidian_fragments.guide"));
        registration.addItemStackInfo(new ItemStack(ItemRegistry.CRYSTAL_HEART.get()), Component.translatable("item.firesenderexpansion.crystal_heart.guide"));
    }
}
