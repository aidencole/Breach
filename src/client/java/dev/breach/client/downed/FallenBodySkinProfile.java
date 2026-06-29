package dev.breach.client.downed;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.UUID;

public record FallenBodySkinProfile(Identifier texture, PlayerModelType modelType, PlayerSkin skin) {
	public static FallenBodySkinProfile from(PlayerSkin skin) {
		return new FallenBodySkinProfile(skin.body().texturePath(), skin.model(), skin);
	}

	public static FallenBodySkinProfile fallback(UUID ownerUuid) {
		return from(DefaultPlayerSkin.get(ownerUuid));
	}

	public ResolvableProfile toResolvableProfile(UUID ownerUuid, String ownerName) {
		return ResolvableProfile.createResolved(new GameProfile(ownerUuid, ownerName == null ? "" : ownerName));
	}

	public boolean slim() {
		return modelType == PlayerModelType.SLIM;
	}
}
