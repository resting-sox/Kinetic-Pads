package resting_sox.soxmodhack.block;

import resting_sox.soxmodhack.FallProtection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KineticPadBlock extends Block {
    private static final int COOLDOWN_TICKS = 10; // half a second between launches
    private static final Map<UUID, Long> LAST_LAUNCH = new HashMap<>();

    private final PadTier tier;

    public KineticPadBlock(Properties properties, PadTier tier) {
        super(properties);
        this.tier = tier;
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