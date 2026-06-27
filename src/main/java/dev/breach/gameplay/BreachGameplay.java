package dev.breach.gameplay;

import dev.breach.BreachFeatures;
import dev.breach.BreachMod;
import dev.breach.command.BreachCommands;
import dev.breach.gameplay.downed.DownedAttachment;
import dev.breach.gameplay.downed.DownedController;
import dev.breach.gameplay.downed.DownedDeathHandler;
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

		if (BreachFeatures.INJURY_SYSTEM_ENABLED) {
			registerInjurySystems();
		} else {
			BreachMod.LOGGER.info("Injury system disabled — vanilla damage and health are active");
		}

		if (BreachFeatures.DOWNED_SYSTEM_ENABLED) {
			registerDownedSystems();
		} else {
			BreachMod.LOGGER.info("Downed system disabled");
		}
	}

	private static void registerInjurySystems() {
		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
			if (entity instanceof ServerPlayer player) {
				InjuryManager.onVanillaDamage(player, source, damageTaken);
				player.setHealth(player.getMaxHealth());
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> InjuryManager.sync(handler.player));

		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			if (!alive) {
				newPlayer.setAttached(InjuryAttachment.INJURY, oldPlayer.getAttachedOrCreate(InjuryAttachment.INJURY));
			}
		});
	}

	private static void registerDownedSystems() {
		DownedAttachment.DOWNED.getClass();

		ServerLivingEntityEvents.ALLOW_DEATH.register(DownedDeathHandler::tryInterceptDeath);

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			if (handler.player instanceof ServerPlayer player) {
				DownedController.onPlayerLogout(player);
			}
		});

		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			if (!alive) {
				newPlayer.setAttached(DownedAttachment.DOWNED, oldPlayer.getAttachedOrCreate(DownedAttachment.DOWNED));
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
				DownedDeathHandler.tick(player);
				DownedController.tickDownedPlayer(player);
				if (BreachFeatures.INJURY_SYSTEM_ENABLED) {
					InjuryManager.tickBedHealing(player);
				} else {
					MedicalBedBlock.tickVanillaHealing(player);
				}
			}
			for (var level : server.getAllLevels()) {
				MedicalBedBlock.tickOccupants(level);
			}
		});
	}
}
