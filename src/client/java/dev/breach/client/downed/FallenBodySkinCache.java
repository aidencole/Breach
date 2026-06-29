package dev.breach.client.downed;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.UUID;

public final class FallenBodySkinCache {
	private FallenBodySkinCache() {
	}

	public static FallenBodySkinProfile resolve(UUID ownerUuid, String ownerName) {
		if (ownerUuid == null) {
			return FallenBodySkinProfile.fallback(UUID.randomUUID());
		}

		Minecraft client = Minecraft.getInstance();
		String name = ownerName == null ? "" : ownerName;

		if (client.level != null) {
			Player player = client.level.getPlayerByUUID(ownerUuid);
			if (player instanceof AbstractClientPlayer clientPlayer) {
				return FallenBodySkinProfile.from(clientPlayer.getSkin());
			}
		}

		PlayerSkinRenderCache cache = client.playerSkinRenderCache();
		if (cache != null) {
			ResolvableProfile profile = ResolvableProfile.createResolved(new GameProfile(ownerUuid, name));
			return FallenBodySkinProfile.from(cache.getOrDefault(profile).playerSkin());
		}

		return FallenBodySkinProfile.fallback(ownerUuid);
	}
}
