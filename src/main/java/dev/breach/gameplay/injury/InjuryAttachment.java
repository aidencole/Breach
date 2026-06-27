package dev.breach.gameplay.injury;

import dev.breach.BreachMod;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.player.Player;

public final class InjuryAttachment {
	public static final AttachmentType<InjuryData> INJURY = AttachmentRegistry.create(
			BreachMod.id("injury"),
			builder -> builder
					.initializer(InjuryData::createDefault)
					.persistent(InjuryData.CODEC)
					.syncWith(InjuryData.STREAM_CODEC, AttachmentSyncPredicate.all())
	);

	private InjuryAttachment() {
	}

	public static InjuryData get(Player player) {
		return player.getAttachedOrCreate(INJURY);
	}

	public static void set(Player player, InjuryData data) {
		player.setAttached(INJURY, data);
	}
}
