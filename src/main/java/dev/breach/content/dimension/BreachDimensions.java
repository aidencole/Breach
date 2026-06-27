package dev.breach.content.dimension;

import dev.breach.BreachMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class BreachDimensions {
	public static final ResourceKey<Level> SCULK_LEVEL = ResourceKey.create(
			Registries.DIMENSION,
			BreachMod.id("sculk_breach")
	);

	public static final ResourceKey<Level> CHALLENGE_LEVEL = ResourceKey.create(
			Registries.DIMENSION,
			BreachMod.id("challenge")
	);

	private BreachDimensions() {
	}

	public static void register() {
		BreachMod.LOGGER.info("Registered Breach dimension keys");
	}
}
