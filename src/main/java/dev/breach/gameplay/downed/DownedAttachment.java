package dev.breach.gameplay.downed;

import dev.breach.BreachMod;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.entity.player.Player;

public final class DownedAttachment {
	public static final AttachmentType<DownedData> DOWNED = AttachmentRegistry.create(
			BreachMod.id("downed"),
			builder -> builder
					.initializer(DownedData::createDefault)
					.persistent(DownedData.CODEC)
					.syncWith(DownedData.STREAM_CODEC, AttachmentSyncPredicate.targetOnly())
	);

	private DownedAttachment() {
	}

	public static DownedData get(Player player) {
		return player.getAttachedOrCreate(DOWNED);
	}

	public static void set(Player player, DownedData data) {
		player.setAttached(DOWNED, data);
	}
}
