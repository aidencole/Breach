package dev.breach.gameplay.downed;

import dev.breach.gameplay.challenge.ChallengeInstanceManager;
import dev.breach.gameplay.injury.InjuryManager;
import dev.breach.gameplay.medical.MedicalBedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/** @deprecated Use {@link DownedController} directly. */
@Deprecated
public final class DownedManager {
	private DownedManager() {
	}

	public static void tryDownPlayer(ServerPlayer player) {
		DownedController.downPlayer(player);
	}

	public static void fieldRevive(ServerPlayer player, Vec3 returnPos, ServerLevel returnLevel) {
		DownedController.fieldRevive(
				player,
				returnPos,
				returnLevel,
				dev.breach.core.network.payload.DownedPresentationS2CPayload.Cue.FIELD_REVIVED,
				null
		);
	}

	public static void reviveToBed(ServerPlayer player, ServerLevel level, BlockPos bedPos) {
		DownedController.reviveToBed(player, level, bedPos, null);
	}

	public static void removeBody(Player player) {
		DownedController.removeBody(player);
	}

	public record ReturnLocation(net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimension, Vec3 position) {
		public static ReturnLocation from(DownedController.ReturnLocation location) {
			return new ReturnLocation(location.dimension(), location.position());
		}
	}
}
