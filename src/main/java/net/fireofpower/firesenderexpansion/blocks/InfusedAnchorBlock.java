package net.fireofpower.firesenderexpansion.blocks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.extensions.IBlockStateExtension;

import java.util.Optional;

public class InfusedAnchorBlock extends Block implements IBlockStateExtension {

    private static final ImmutableList<Vec3i> RESPAWN_HORIZONTAL_OFFSETS;
    private static final ImmutableList<Vec3i> RESPAWN_OFFSETS;

    public InfusedAnchorBlock(Properties properties) {
        super(properties);
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!canSetSpawn(level)) {
            level.playSound((Player)null, (double)pos.getX() + (double)0.5F, (double)pos.getY() + (double)0.5F, (double)pos.getZ() + (double)0.5F, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("msg.firesenderexpansion.wrong_dimension")
                        .withStyle(s -> s.withColor(TextColor.fromRgb(0xF35F5F)))));
                serverPlayer.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.5f, 1f);
            }
            for(int i = 0; i < 10; i++) {
                double d0 = (double) pos.getX() + (double) 0.5F + ((double) 0.5F - Math.random());
                double d1 = (double) pos.getY() + (double) 1.0F;
                double d2 = (double) pos.getZ() + (double) 0.5F + ((double) 0.5F - Math.random());
                double d3 = (double) Math.random() * 0.04;
                level.addParticle(ParticleTypes.SMOKE, d0, d1, d2, (double) 0.0F, d3, (double) 0.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            if (!level.isClientSide) {
                ServerPlayer serverplayer = (ServerPlayer)player;
                pos = pos.above();
                if (serverplayer.getRespawnDimension() != level.dimension() || !pos.equals(serverplayer.getRespawnPosition())) {
                    serverplayer.setRespawnPosition(level.dimension(), pos.atY(pos.getY()-1), 0.0F, false, true);
                    level.playSound((Player)null, (double)pos.getX() + (double)0.5F, (double)pos.getY() + (double)0.5F, (double)pos.getZ() + (double)0.5F, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return InteractionResult.SUCCESS;
                }
            }

            return InteractionResult.CONSUME;
        }
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(100) == 0) {
            level.playLocalSound(pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }

        if(level.dimension().equals(Level.END)) {
            double d0 = (double) pos.getX() + (double) 0.5F + ((double) 0.5F - random.nextDouble());
            double d1 = (double) pos.getY() + (double) 1.0F;
            double d2 = (double) pos.getZ() + (double) 0.5F + ((double) 0.5F - random.nextDouble());
            double d3 = (double) random.nextFloat() * 0.04;
            level.addParticle(ParticleTypes.END_ROD, d0, d1, d2, (double) 0.0F, d3, (double) 0.0F);
        }

    }

    @Override
    public Optional<ServerPlayer.RespawnPosAngle> getRespawnPosition(BlockState state, EntityType<?> type, LevelReader levelReader, BlockPos pos, float orientation) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        UnmodifiableIterator var5 = RESPAWN_OFFSETS.iterator();

        while(var5.hasNext()) {
            Vec3i vec3i = (Vec3i)var5.next();
            blockpos$mutableblockpos.set(pos).move(vec3i);
            Vec3 vec3 = DismountHelper.findSafeDismountLocation(type, levelReader, blockpos$mutableblockpos, false);
            if (vec3 != null) {
                return Optional.of(new ServerPlayer.RespawnPosAngle(vec3,orientation));
            }
        }

        FiresEnderExpansion.LOGGER.debug("Could not find respawn location");
        return Optional.empty();
    }

    public static boolean canSetSpawn(Level level) {
        return level.dimension().equals(Level.END);
    }

    static {
        RESPAWN_HORIZONTAL_OFFSETS = ImmutableList.of(new Vec3i(0, 0, -1), new Vec3i(-1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(1, 0, 0), new Vec3i(-1, 0, -1), new Vec3i(1, 0, -1), new Vec3i(-1, 0, 1), new Vec3i(1, 0, 1));
        RESPAWN_OFFSETS = (new ImmutableList.Builder()).addAll(RESPAWN_HORIZONTAL_OFFSETS).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vec3i::below).iterator()).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vec3i::above).iterator()).add(new Vec3i(0, 1, 0)).build();
    }
}
