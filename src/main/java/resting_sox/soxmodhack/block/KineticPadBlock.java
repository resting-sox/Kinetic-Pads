package resting_sox.soxmodhack.block;

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
import resting_sox.soxmodhack.FallProtection;


public class KineticPadBlock extends Block {
    private final double launchPower;
    private final int protectionTicks;

    public KineticPadBlock(Properties properties, double launchPower, int protectionTicks) {
        super(properties);
        this.launchPower = launchPower;
        this.protectionTicks = protectionTicks;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity){
        if(level instanceof ServerLevel serverLevel && entity instanceof  ServerPlayer player) {

            player.setDeltaMovement(player.getDeltaMovement().x, launchPower, player.getDeltaMovement().z); //keep sidewars speed, only add speed upwards
            player.hurtMarked = true;

            player.resetFallDistance();
            FallProtection.grant(player, protectionTicks);

            // Smoke particles
            serverLevel.sendParticles(
                    ParticleTypes.WHITE_SMOKE,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    30, 0.3, 0.1, 0.3, 0.02
            );

            // Sound (null = audible to everyone nearby)
            level.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_LAUNCH,
                    SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        super.stepOn(level,pos,state,entity);
    }

    }
