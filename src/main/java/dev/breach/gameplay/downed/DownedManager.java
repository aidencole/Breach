package dev.breach.gameplay.downed;

import dev.breach.content.entity.BreachEntities;
import dev.breach.gameplay.challenge.ChallengeInstanceManager;
import dev.breach.gameplay.injury.InjuryAttachment;
import dev.breach.gameplay.injury.InjuryData;
import dev.breach.gameplay.injury.InjuryManager;
import dev.breach.gameplay.medical.MedicalBedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public final class DownedManager {
	private DownedManager() {
	}

	public static void tryDownPlayer(ServerPlayer player) {
		InjuryData data = InjuryAttachment.get(player);
		if (data.isDowned() || !data.isCritical()) {
			return;
		}

		ServerLevel level = (ServerLevel) player.level();
		Vec3 pos = player.position();

		FallenBodyEntity body = new FallenBodyEntity(BreachEntities.FALLEN_BODY, level);
		body.setOwner(player);
		body.teleportTo(pos.x, pos.y, pos.z);
		body.setYRot(player.getYRot());
		level.addFreshEntity(body);

		data.setDowned(true);
		data.setBodyEntityId(body.getUUID());
		InjuryAttachment.set(player, data);

		ChallengeInstanceManager.ensureInstance(player);
		ChallengeInstanceManager.teleportToChallenge(player, new ReturnLocation(level.dimension(), pos));

		player.sendSystemMessage(Component.literal("You are downed. Complete the challenge or wait for rescue."));
		InjuryManager.sync(player);
	}

	public static void fieldRevive(ServerPlayer player, Vec3 returnPos, ServerLevel returnLevel) {
		InjuryData data = InjuryAttachment.get(player);
		if (!data.isDowned()) {
			return;
		}

		data.applyFieldRevive();
		data.setBodyEntityId(null);
		InjuryAttachment.set(player, data);

		removeBody(player);
		player.teleportTo(returnLevel, returnPos.x, returnPos.y, returnPos.z, Set.of(), player.getYRot(), player.getXRot(), true);
		player.sendSystemMessage(Component.literal("You were revived with injuries."));
		InjuryManager.sync(player);
	}

	public static void reviveToBed(ServerPlayer player, ServerLevel level, BlockPos bedPos) {
		InjuryData data = InjuryAttachment.get(player);
		if (!data.isDowned()) {
			return;
		}

		data.setDowned(false);
		data.setBodyEntityId(null);
		data.clearReturnLocation();
		InjuryAttachment.set(player, data);

		player.teleportTo(level, bedPos.getX() + 0.5, bedPos.getY() + 0.5, bedPos.getZ() + 0.5, Set.of(), player.getYRot(), player.getXRot(), true);
		MedicalBedBlock.enterBed(player, bedPos);
		player.sendSystemMessage(Component.literal("Placed in medical bed. Stay nearby to heal."));
		InjuryManager.sync(player);
	}

	public static void removeBody(Player player) {
		InjuryData data = InjuryAttachment.get(player);
		if (data.bodyEntityId() == null) {
			return;
		}

		if (player.level() instanceof ServerLevel serverLevel) {
			var entity = serverLevel.getEntity(data.bodyEntityId());
			if (entity != null) {
				entity.discard();
			}
		}
		data.setBodyEntityId(null);
		InjuryAttachment.set(player, data);
	}

	public record ReturnLocation(net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimension, Vec3 position) {
	}
}
