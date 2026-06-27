package dev.breach.content.entity;

import dev.breach.BreachMod;

/**
 * Custom enemies, bosses, and ambient Sculk creatures.
 */
public final class BreachEntities {
	private BreachEntities() {
	}

	public static void register() {
		// Register entity types and attributes here as content is authored.
		BreachMod.LOGGER.info("Breach entities registry ready");
	}
}
