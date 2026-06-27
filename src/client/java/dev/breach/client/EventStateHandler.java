package dev.breach.client;

import dev.breach.core.event.EventPhase;
import dev.breach.core.network.payload.EventStateS2CPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Applies server-authoritative event state on the client (UI, cutscenes, ambient effects).
 */
public final class EventStateHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger("breach-client");

	private static EventPhase currentPhase = EventPhase.INACTIVE;
	private static int participantCount;

	private EventStateHandler() {
	}

	public static void onEventState(EventStateS2CPayload payload) {
		currentPhase = payload.phase();
		participantCount = payload.participantCount();
		LOGGER.debug("Event state synced: phase={}, participants={}", currentPhase, participantCount);
	}

	public static EventPhase getCurrentPhase() {
		return currentPhase;
	}

	public static int getParticipantCount() {
		return participantCount;
	}
}
