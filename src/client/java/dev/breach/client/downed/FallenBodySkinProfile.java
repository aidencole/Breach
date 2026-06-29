package dev.breach.client.downed;

import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;

import java.util.UUID;

public record FallenBodySkinProfile(Identifier texture, PlayerModelType modelType) {
	public static FallenBodySkinProfile fallback(UUID ownerUuid) {
		PlayerSkin skin = DefaultPlayerSkin.get(ownerUuid);
		return new FallenBodySkinProfile(skin.body().texturePath(), skin.model());
	}

	public boolean slim() {
		return modelType == PlayerModelType.SLIM;
	}
}
