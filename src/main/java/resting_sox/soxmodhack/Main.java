package resting_sox.soxmodhack;
import resting_sox.soxmodhack.block.ModBlocks;

import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {
	public static final String MOD_ID = "soxmodhack";
	@Override
	public void onInitialize() {
		ModBlocks.register();
		FallProtection.register();
	}
}