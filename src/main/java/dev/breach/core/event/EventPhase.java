package dev.breach.core.event;

/**
 * Story phases for the Breach Sculk Dimension event.
 * Server drives transitions; clients react via synced state and cutscenes.
 */
public enum EventPhase {
	INACTIVE,
	BRIEFING,
	DESCENT,
	EXPLORATION,
	DUNGEON,
	BOSS,
	ESCAPE,
	EPILOGUE;

	public boolean isActive() {
		return this != INACTIVE && this != EPILOGUE;
	}
}
