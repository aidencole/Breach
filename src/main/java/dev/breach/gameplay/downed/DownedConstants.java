package dev.breach.gameplay.downed;

public final class DownedConstants {
	/** Half-hearts restored on field/challenge revive (4 hearts). */
	public static final float FIELD_REVIVE_HEALTH = 8.0f;
	/** Bed healing: one half-heart every 1.5 seconds at 20 TPS. */
	public static final int BED_HEAL_INTERVAL_TICKS = 30;
	/**
	 * GeckoLib renders model pixels at 1/16 block each. This multiplier brings a standard
	 * player-sized Blockbench rig up to vanilla player scale (~1.8 blocks). Increase if bodies
	 * still look too small in-game.
	 */
	public static final float FALLEN_BODY_GEO_SCALE = 1.875f;
	/** Vanilla player model scale used by the non-GeckoLib fallback renderer. */
	public static final float FALLEN_BODY_VANILLA_MODEL_SCALE = 0.9375f;

	private DownedConstants() {
	}
}
