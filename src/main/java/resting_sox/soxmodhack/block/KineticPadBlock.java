package resting_sox.soxmodhack.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import resting_sox.soxmodhack.FallProtection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KineticPadBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(0,0,0,16,4,16);

    private static final int COOLDOWN_TICKS = 10; // half a second between launches
    private static final Map<UUID, Long> LAST_LAUNCH = new HashMap<>();

    private final PadTier tier;

    public KineticPadBlock(Properties properties, PadTier tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context){
        return SHAPE;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos){
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level,below,Direction.UP);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
                                     BlockPos pos, Direction direction, BlockPos neighborPos,
                                     BlockState neighborState, RandomSource random){
        if(!state.canSurvive(level,pos)){
            return Blocks.AIR.defaultBlockState();

        }
        return super.updateShape(state,level,ticks,pos,direction,neighborPos,neighborState,random);

    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level instanceof ServerLevel serverLevel && entity instanceof ServerPlayer player) {
            if (isOnCooldown(player, level)) {
                super.stepOn(level, pos, state, entity);
                return;
            }
            launch(player);
            playEffects(serverLevel, pos);
        }
        super.stepOn(level, pos, state, entity);
    }

    private boolean isOnCooldown(ServerPlayer player, Level level) {
        long now = level.getGameTime();
        Long last = LAST_LAUNCH.get(player.getUUID());
        if (last != null && now - last < COOLDOWN_TICKS) {
            return true;
        }
        LAST_LAUNCH.put(player.getUUID(), now);
        return false;
    }

    private void launch(ServerPlayer player) {
        player.setDeltaMovement(player.getDeltaMovement().x, tier.launchPower(), player.getDeltaMovement().z);
        player.hurtMarked = true;
        player.resetFallDistance();
        FallProtection.grant(player, tier.protectionTicks());
    }

    private void playEffects(ServerLevel level, BlockPos pos) {
        level.sendParticles(
                ParticleTypes.WHITE_SMOKE,
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                tier.particleCount(), 0.3, 0.1, 0.3, 0.02
        );
        level.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_LAUNCH,
                SoundSource.BLOCKS, 1.0f, 1.0f);
    }
}