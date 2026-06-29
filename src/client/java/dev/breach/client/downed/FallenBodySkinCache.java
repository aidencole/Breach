package dev.breach.client.downed;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.PlayerSkin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class FallenBodySkinCache {
	private static final Map<UUID, FallenBodySkinProfile> CACHE = new ConcurrentHashMap<>();

	private FallenBodySkinCache() {
	}

	public static FallenBodySkinProfile resolve(UUID ownerUuid, String ownerName) {
		if (ownerUuid == null) {
			return FallenBodySkinProfile.fallback(UUID.randomUUID());
		}
		return CACHE.computeIfAbsent(ownerUuid, id -> fetchSkin(id, ownerName));
	}

	private static FallenBodySkinProfile fetchSkin(UUID ownerUuid, String ownerName) {
		Minecraft client = Minecraft.getInstance();
		if (client.getSkinManager() != null) {
			GameProfile profile = new GameProfile(ownerUuid, ownerName == null ? "" : ownerName);
			PlayerSkin skin = client.getSkinManager().get(profile).getNow(java.util.Optional.empty()).orElse(null);
			if (skin != null) {
				return new FallenBodySkinProfile(skin.body().texturePath(), skin.model());
			}
		}
		return FallenBodySkinProfile.fallback(ownerUuid);
	}

	public static void clear() {
		CACHE.clear();
	}
}
