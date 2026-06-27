package dev.breach.core.event;

import dev.breach.BreachMod;
import dev.breach.content.dimension.BreachDimensions;
import dev.breach.core.network.BreachNetworking;
import dev.breach.core.network.payload.EventStateS2CPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Server-authoritative session state for the Breach event.
 * Designed for 30+ concurrent players with a single synced storyline phase.
 */
public final class EventSessionManager {
	private static final int SYNC_INTERVAL_TICKS = 20;

	private static EventPhase phase = EventPhase.INACTIVE;
	private static final Set<UUID> participants = new HashSet<>();
	private static int ticksSinceSync;

	private EventSessionManager() {
	}

	public static void init() {
		ServerTickEvents.END_SERVER_TICK.register(EventSessionManager::onEndServerTick);
		BreachMod.LOGGER.info("Event session manager ready (supports large multiplayer sessions)");
	}

	public static EventPhase getPhase() {
		return phase;
	}

	public static Set<UUID> getParticipants() {
		return Collections.unmodifiableSet(participants);
	}

	public static int getParticipantCount() {
		return participants.size();
	}

	public static boolean isParticipant(ServerPlayer player) {
		return participants.contains(player.getUUID());
	}

	public static void startEvent(MinecraftServer server) {
		if (phase.isActive()) {
			return;
		}

		phase = EventPhase.BRIEFING;
		participants.clear();
		syncToAll(server);
		BreachMod.LOGGER.info("Breach event started");
	}

	public static void setPhase(MinecraftServer server, EventPhase nextPhase) {
		if (phase == nextPhase) {
			return;
		}

		phase = nextPhase;
		syncToAll(server);
		BreachMod.LOGGER.info("Breach event phase -> {}", nextPhase);
	}

	public static void endEvent(MinecraftServer server) {
		phase = EventPhase.INACTIVE;
		participants.clear();
		syncToAll(server);
		BreachMod.LOGGER.info("Breach event ended");
	}

	public static void joinParticipant(ServerPlayer player) {
		if (participants.add(player.getUUID())) {
			MinecraftServer server = player.level().getServer();
			if (server != null) {
				syncToAll(server);
			}
			BreachMod.LOGGER.info("Player joined Breach event: {} (total: {})", player.getGameProfile().name(), participants.size());
		}
	}

	public static void leaveParticipant(ServerPlayer player) {
		if (participants.remove(player.getUUID())) {
			MinecraftServer server = player.level().getServer();
			if (server != null) {
				syncToAll(server);
			}
			BreachMod.LOGGER.info("Player left Breach event: {} (total: {})", player.getGameProfile().name(), participants.size());
		}
	}

	private static void onEndServerTick(MinecraftServer server) {
		if (!phase.isActive()) {
			return;
		}

		ticksSinceSync++;
		if (ticksSinceSync >= SYNC_INTERVAL_TICKS) {
			ticksSinceSync = 0;
			syncToAll(server);
		}
	}

	private static void syncToAll(MinecraftServer server) {
		EventStateS2CPayload payload = new EventStateS2CPayload(phase, participants.size());
		BreachNetworking.broadcastEventState(server, payload);
	}

	public static boolean isInSculkDimension(ServerLevel level) {
		return level.dimension().equals(BreachDimensions.SCULK_LEVEL);
	}
}
