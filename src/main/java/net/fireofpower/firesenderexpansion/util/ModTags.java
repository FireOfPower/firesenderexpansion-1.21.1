package net.fireofpower.firesenderexpansion.util;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class ModTags {
    public static final TagKey<EntityType<?>> BREAKS_HOLLOW_CRYSTAL = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "breaks_hollow_crystal"));

}
