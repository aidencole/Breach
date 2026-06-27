package dev.breach.core.network;

import dev.breach.core.network.payload.EventStateS2CPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class BreachNetworking {
	private BreachNetworking() {
	}

	public static void registerPayloads() {
		PayloadTypeRegistry.clientboundPlay().register(EventStateS2CPayload.TYPE, EventStateS2CPayload.CODEC);
	}

	public static void registerClientReceivers() {
		// Additional client-bound payloads register here.
	}

	public static void broadcastEventState(MinecraftServer server, EventStateS2CPayload payload) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			ServerPlayNetworking.send(player, payload);
		}
	}

	public static void sendEventState(ServerPlayer player, EventStateS2CPayload payload) {
		ServerPlayNetworking.send(player, payload);
	}
}
