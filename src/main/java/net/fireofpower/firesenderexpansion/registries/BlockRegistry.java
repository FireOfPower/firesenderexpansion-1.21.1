package net.fireofpower.firesenderexpansion.registries;

import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.fireofpower.firesenderexpansion.blocks.InfusedAnchorBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegistry {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, FiresEnderExpansion.MODID);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }


    public static final DeferredHolder<Block, Block> INFUSED_ANCHOR = BLOCKS.register("infused_anchor", () -> new InfusedAnchorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANCIENT_DEBRIS).requiresCorrectToolForDrops().strength(50.0F, 1200.0F).lightLevel(state -> 15)));
}
