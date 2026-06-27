package dev.breach.content.dimension;

import dev.breach.BreachMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/**
 * The Sculk Dimension — primary setting for the Breach multiplayer event.
 * Dimension data files live under data/breach/dimension and dimension_type.
 */
public final class BreachDimensions {
	public static final ResourceKey<Level> SCULK_LEVEL = ResourceKey.create(
			Registries.DIMENSION,
			BreachMod.id("sculk_breach")
	);

	private BreachDimensions() {
	}

	public static void register() {
		BreachMod.LOGGER.info("Breach dimension keys registered (data-driven setup via JSON)");
	}
}
