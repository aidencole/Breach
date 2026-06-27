package dev.breach.gameplay.downed;

import dev.breach.BreachMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Intercepts lethal damage and schedules downed flow outside the hurtServer callback. */
public final class DownedDeathHandler {
	private static final Set<UUID> PENDING = ConcurrentHashMap.newKeySet();

	private DownedDeathHandler() {
	}

	public static boolean tryInterceptDeath(LivingEntity entity, DamageSource source, float amount) {
		if (!(entity instanceof ServerPlayer player)) {
			return true;
		}
		if (DownedAttachment.get(player).isDowned() || PENDING.contains(player.getUUID())) {
			return false;
		}

		player.setHealth(player.getMaxHealth());
		scheduleDown(player);
		BreachMod.LOGGER.info("Intercepted lethal damage for {} — scheduling downed state", player.getGameProfile().name());
		return false;
	}

	public static void tick(ServerPlayer player) {
		if (player.getHealth() <= 0.0F && !DownedAttachment.get(player).isDowned() && !PENDING.contains(player.getUUID())) {
			player.setHealth(player.getMaxHealth());
			scheduleDown(player);
			BreachMod.LOGGER.warn("Fallback downed trigger for {} at 0 health", player.getGameProfile().name());
		}
	}

	private static void scheduleDown(ServerPlayer player) {
		MinecraftServer server = player.level().getServer();
		if (server == null) {
			return;
		}

		UUID id = player.getUUID();
		if (!PENDING.add(id)) {
			return;
		}

		server.execute(() -> {
			try {
				if (player.isRemoved() || DownedAttachment.get(player).isDowned()) {
					return;
				}
				player.setHealth(player.getMaxHealth());
				DownedController.downPlayer(player);
			} finally {
				PENDING.remove(id);
			}
		});
	}
}
