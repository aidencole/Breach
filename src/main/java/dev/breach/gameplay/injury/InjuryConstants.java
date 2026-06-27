package dev.breach.gameplay.injury;

public final class InjuryConstants {
	public static final int PART_COUNT = 6;
	/** Three hearts per body part, stored as half-hearts. */
	public static final int MAX_PART_HEALTH = 6;
	/** Bed healing: 1.5 seconds per half-heart at 20 TPS. */
	public static final int BED_HEAL_INTERVAL_TICKS = 30;
	/** Field revive restores head/chest to this many half-hearts. */
	public static final int FIELD_REVIVE_VITAL_HEALTH = 2;

	private InjuryConstants() {
	}
}
