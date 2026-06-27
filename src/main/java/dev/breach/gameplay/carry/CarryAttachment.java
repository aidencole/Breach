package dev.breach.gameplay.carry;

import dev.breach.BreachMod;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class CarryAttachment {
	public static final AttachmentType<Boolean> CARRYING = AttachmentRegistry.create(
			BreachMod.id("carrying"),
			builder -> builder
					.initializer(() -> false)
					.syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.targetOnly())
	);

	private CarryAttachment() {
	}

	public static boolean isCarrying(LivingEntity entity) {
		if (!(entity instanceof Player player)) {
			return false;
		}
		return player.getAttachedOrCreate(CARRYING);
	}

	public static void setCarrying(Player player, boolean carrying) {
		player.setAttached(CARRYING, carrying);
	}
}
