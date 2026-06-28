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

		player.setHealth(player.getMaxHealth());

		if (DownedAttachment.get(player).isDowned()) {
			DownedController.ensureInChallenge(player);
			return false;
		}

		if (PENDING.contains(player.getUUID())) {
			return false;
		}

		scheduleDown(player);
		BreachMod.LOGGER.info("Intercepted lethal damage for {} — scheduling downed state", player.getGameProfile().name());
		return false;
	}

	public static void tick(ServerPlayer player) {
		if (player.getHealth() > 0.0F) {
			return;
		}

		player.setHealth(player.getMaxHealth());

		if (DownedAttachment.get(player).isDowned()) {
			DownedController.ensureInChallenge(player);
			return;
		}

		if (!PENDING.contains(player.getUUID())) {
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
				if (player.isRemoved()) {
					BreachMod.LOGGER.warn("Skipped downed for {} because player was removed", player.getGameProfile().name());
					return;
				}

				player.setHealth(player.getMaxHealth());

				if (DownedAttachment.get(player).isDowned()) {
					DownedController.ensureInChallenge(player);
					return;
				}

				DownedController.downPlayer(player);
			} catch (Exception exception) {
				BreachMod.LOGGER.error("Failed to start downed state for {}", player.getGameProfile().name(), exception);
				player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Downed system failed — tell an admin."));
			} finally {
				PENDING.remove(id);
			}
		});
	}
}
