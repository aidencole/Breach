package dev.breach.gameplay;

import dev.breach.command.BreachCommands;
import dev.breach.gameplay.injury.InjuryAttachment;
import dev.breach.gameplay.injury.InjuryManager;
import dev.breach.gameplay.medical.MedicalBedBlock;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
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

		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			if (!alive) {
				newPlayer.setAttached(InjuryAttachment.INJURY, oldPlayer.getAttachedOrCreate(InjuryAttachment.INJURY));
			}
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				InjuryManager.tickBedHealing(player);
			}
			for (var level : server.getAllLevels()) {
				MedicalBedBlock.tickOccupants(level);
			}
		});
	}
}
