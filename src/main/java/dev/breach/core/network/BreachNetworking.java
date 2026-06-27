package dev.breach.core.network;

import dev.breach.core.network.payload.DownedPresentationS2CPayload;
import dev.breach.core.network.payload.EventStateS2CPayload;
import dev.breach.core.network.payload.InjurySyncS2CPayload;
import dev.breach.gameplay.injury.InjuryData;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public final class BreachNetworking {
	private BreachNetworking() {
	}

	public static void registerPayloads() {
		PayloadTypeRegistry.clientboundPlay().register(EventStateS2CPayload.TYPE, EventStateS2CPayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(InjurySyncS2CPayload.TYPE, InjurySyncS2CPayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(DownedPresentationS2CPayload.TYPE, DownedPresentationS2CPayload.CODEC);
	}

	public static void registerClientReceivers() {
		// Handled in BreachClient
	}

	public static void sendInjury(ServerPlayer player, InjuryData data) {
		ServerPlayNetworking.send(player, new InjurySyncS2CPayload(data));
	}

	public static void sendDownedPresentation(ServerPlayer player, DownedPresentationS2CPayload.Cue cue, UUID subjectId, UUID actorId) {
		ServerPlayNetworking.send(player, new DownedPresentationS2CPayload(cue, subjectId, actorId));
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
