package dev.breach.gameplay;

import dev.breach.BreachFeatures;
import dev.breach.BreachMod;
import dev.breach.command.BreachCommands;
import dev.breach.gameplay.downed.DownedController;
import dev.breach.gameplay.injury.InjuryAttachment;
import dev.breach.gameplay.injury.InjuryManager;
import dev.breach.gameplay.medical.MedicalBedBlock;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

public final class BreachGameplay {
	private BreachGameplay() {
	}

	public static void init() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> BreachCommands.register(dispatcher));

		if (!BreachFeatures.INJURY_SYSTEM_ENABLED) {
			BreachMod.LOGGER.info("Injury and downed systems are disabled — vanilla damage and death are active");
			return;
		}

		registerInjurySystems();
	}

	private static void registerInjurySystems() {
		ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {
			if (entity instanceof ServerPlayer player) {
				player.setHealth(player.getMaxHealth());
				return false;
			}
			return true;
		});

		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
			if (entity instanceof ServerPlayer player) {
				InjuryManager.onVanillaDamage(player, source, damageTaken);
				player.setHealth(player.getMaxHealth());
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> InjuryManager.sync(handler.player));

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			if (handler.player instanceof ServerPlayer player) {
				DownedController.onPlayerLogout(player);
			}
		});

		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			if (!alive) {
				newPlayer.setAttached(InjuryAttachment.INJURY, oldPlayer.getAttachedOrCreate(InjuryAttachment.INJURY));
			}
		});

		EntitySleepEvents.ALLOW_RESETTING_TIME.register(player -> false);
		EntitySleepEvents.ALLOW_SETTING_SPAWN.register((player, sleepingPos) -> false);
		EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {
			if (entity instanceof ServerPlayer player) {
				MedicalBedBlock.stopHealing(player);
			}
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				DownedController.tickDownedPlayer(player);
				InjuryManager.tickBedHealing(player);
			}
			for (var level : server.getAllLevels()) {
				MedicalBedBlock.tickOccupants(level);
			}
		});
	}
}
