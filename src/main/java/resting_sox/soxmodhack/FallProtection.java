package resting_sox.soxmodhack;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FallProtection {
    private static final Map<UUID, Long> PROTECTED_UNTIL = new HashMap<>();

    public static void grant(ServerPlayer player, int ticks){
        PROTECTED_UNTIL.put(player.getUUID(),player.level().getGameTime()+ticks);
    }

    public static void register(){
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if(source.is(DamageTypes.FALL)&& entity instanceof ServerPlayer player){
                Long until = PROTECTED_UNTIL.get(player.getUUID());
                if(until != null){
                    if (player.level().getGameTime()<=until){
                        return false;
                    }
                    PROTECTED_UNTIL.remove(player.getUUID());
                }
            }
            return true;
        });
    }
}
