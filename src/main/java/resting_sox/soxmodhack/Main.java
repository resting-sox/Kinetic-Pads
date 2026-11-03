package resting_sox.soxmodhack;
import resting_sox.soxmodhack.block.ModBlocks;

import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {
	@Override
	public void onInitialize() {
		ModBlocks.register();
		FallProtection.register();
	}
}