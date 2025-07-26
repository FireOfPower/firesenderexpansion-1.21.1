package net.fireofpower.firesenderexpansion.capabilities.magic;

import io.redspace.ironsspellbooks.capabilities.magic.PocketDimensionManager;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class VoidDimensionManager {
    public static final ResourceKey<Level> VOID_DIMENSION = ResourceKey.create(Registries.DIMENSION, FiresEnderExpansion.id("void_dimension"));
}
