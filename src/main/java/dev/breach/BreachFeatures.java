package dev.breach;

/**
 * Runtime feature toggles while systems are being rebuilt.
 * Flip {@link #INJURY_SYSTEM_ENABLED} back on when the injury/downed rewrite is ready.
 */
public final class BreachFeatures {
	/** When false, players use vanilla health, damage, and death. Injury HUD and downed flow are inactive. */
	public static final boolean INJURY_SYSTEM_ENABLED = false;

	private BreachFeatures() {
	}
}
