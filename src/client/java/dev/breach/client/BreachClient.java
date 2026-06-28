package dev.breach.client;

import dev.breach.BreachFeatures;
import dev.breach.BreachMod;
import dev.breach.client.downed.DownedClientEffects;
import dev.breach.client.downed.FallenBodyGeoRenderer;
import dev.breach.client.downed.FallenBodyRenderer;
import dev.breach.client.injury.BodyHudOverlay;
import dev.breach.content.entity.BreachEntities;
import dev.breach.core.network.BreachNetworking;
import dev.breach.core.network.payload.DownedPresentationS2CPayload;
import dev.breach.core.network.payload.EventStateS2CPayload;
import dev.breach.core.network.payload.InjurySyncS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

public final class BreachClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(EventStateS2CPayload.TYPE, (payload, context) -> {
			context.client().execute(() -> EventStateHandler.onEventState(payload));
		});

		BreachNetworking.registerClientReceivers();

		if (BreachFeatures.GECKOLIB_FALLEN_BODY) {
			EntityRendererRegistry.register(BreachEntities.FALLEN_BODY, FallenBodyGeoRenderer::new);
		} else {
			EntityRendererRegistry.register(BreachEntities.FALLEN_BODY, FallenBodyRenderer::new);
		}

		if (BreachFeatures.DOWNED_SYSTEM_ENABLED) {
			ClientPlayNetworking.registerGlobalReceiver(DownedPresentationS2CPayload.TYPE, (payload, context) -> {
				context.client().execute(() -> DownedClientEffects.handle(payload));
			});
		}

		if (!BreachFeatures.INJURY_SYSTEM_ENABLED) {
			return;
		}

		ClientPlayNetworking.registerGlobalReceiver(InjurySyncS2CPayload.TYPE, (payload, context) -> {
			context.client().execute(() -> BodyHudOverlay.update(payload.data()));
		});

		HudElementRegistry.attachElementAfter(
				VanillaHudElements.MISC_OVERLAYS,
				BreachMod.id("body_injury_hud"),
				(graphics, tickCounter) -> {
					if (net.minecraft.client.Minecraft.getInstance().gui.hud.isHidden()) {
						return;
					}
					if (graphics.guiWidth() <= 0 || graphics.guiHeight() <= 0) {
						return;
					}
					BodyHudOverlay.render(graphics);
				}
		);
	}
}
