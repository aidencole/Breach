package dev.breach.client;

import dev.breach.core.network.BreachNetworking;
import dev.breach.core.network.payload.EventStateS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BreachClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("breach-client");

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Breach client systems");

		ClientPlayNetworking.registerGlobalReceiver(EventStateS2CPayload.TYPE, (payload, context) -> {
			context.client().execute(() -> EventStateHandler.onEventState(payload));
		});

		BreachNetworking.registerClientReceivers();
	}

	private BreachClient() {
	}
}
