package dev.breach;

/**
 * Runtime feature toggles while systems are being rebuilt.
 */
public final class BreachFeatures {
	/** Body-part injury HUD, damage redirection, and part-based downed triggers. */
	public static final boolean INJURY_SYSTEM_ENABLED = false;

	/** Fallen body, challenge teleport, carry, medkit/bed/challenge revive. */
	public static final boolean DOWNED_SYSTEM_ENABLED = true;

	/** Use Blockbench/GeckoLib model for fallen bodies instead of the vanilla player model. */
	public static final boolean GECKOLIB_FALLEN_BODY = true;

	private BreachFeatures() {
	}
}
