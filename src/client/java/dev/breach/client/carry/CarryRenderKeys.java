package dev.breach.client.carry;

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;

public final class CarryRenderKeys {
	public static final RenderStateDataKey<Boolean> CARRYING = RenderStateDataKey.create(() -> "breach_carrying");

	private CarryRenderKeys() {
	}
}
