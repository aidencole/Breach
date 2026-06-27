package dev.breach.client.downed;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class FallenBodySkinCache {
	private static final Map<UUID, Identifier> CACHE = new ConcurrentHashMap<>();

	private FallenBodySkinCache() {
	}

	public static Identifier resolve(UUID ownerUuid, String ownerName) {
		return CACHE.computeIfAbsent(ownerUuid, id -> fetchSkin(id, ownerName));
	}

	private static Identifier fetchSkin(UUID ownerUuid, String ownerName) {
		Minecraft client = Minecraft.getInstance();
		if (client.getSkinManager() != null) {
			GameProfile profile = new GameProfile(ownerUuid, ownerName);
			PlayerSkin skin = client.getSkinManager().get(profile).getNow(java.util.Optional.empty()).orElse(null);
			if (skin != null) {
				return skin.body().texturePath();
			}
		}
		return net.minecraft.client.resources.DefaultPlayerSkin.get(ownerUuid).body().texturePath();
	}

	public static void clear() {
		CACHE.clear();
	}
}
