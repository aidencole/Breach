package dev.breach.gameplay.downed;

import dev.breach.BreachFeatures;
import dev.breach.BreachMod;
import dev.breach.content.entity.BreachEntities;
import dev.breach.core.network.BreachNetworking;
import dev.breach.core.network.payload.DownedPresentationS2CPayload;
import dev.breach.content.dimension.BreachDimensions;
import dev.breach.gameplay.challenge.ChallengeInstanceManager;
import dev.breach.gameplay.carry.CarryAttachment;
import dev.breach.gameplay.injury.InjuryManager;
import dev.breach.gameplay.medical.MedicalBedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Set;
import java.util.UUID;

public final class DownedController {
	private DownedController() {
	}

	public static void downPlayer(ServerPlayer player) {
		DownedData data = DownedAttachment.get(player);
		if (data.isDowned()) {
			ensureInChallenge(player);
			return;
		}

		ServerLevel origin = (ServerLevel) player.level();
		Vec3 originPos = player.position();

		try {
			beginDowned(player, data, origin, originPos);
		} catch (Exception exception) {
			BreachMod.LOGGER.error("Downed flow failed for {}", player.getGameProfile().name(), exception);
			data.clearDownedState();
			DownedAttachment.set(player, data);
			clearDownedEffects(player);
			player.sendSystemMessage(Component.literal("Downed system failed — try again or contact an admin."));
		}
	}

	public static void ensureInChallenge(ServerPlayer player) {
		if (!DownedAttachment.get(player).isDowned()) {
			return;
		}

		if (player.level().dimension().equals(BreachDimensions.CHALLENGE_LEVEL)) {
			return;
		}

		DownedData data = DownedAttachment.get(player);
		if (data.returnDimension() == null) {
			BreachMod.LOGGER.warn("Clearing stale downed state for {}", player.getGameProfile().name());
			data.clearDownedState();
			DownedAttachment.set(player, data);
			clearDownedEffects(player);
			return;
		}

		ChallengeInstanceManager.ensureInstance(player);
		ChallengeInstanceManager.teleportToChallenge(player);
		player.sendSystemMessage(Component.literal("Returned to the challenge dimension. Right-click the red block to revive."));
	}

	private static void beginDowned(ServerPlayer player, DownedData data, ServerLevel origin, Vec3 originPos) {
		FallenBodyEntity body = spawnBody(player, origin, originPos);
		if (body == null) {
			BreachMod.LOGGER.warn("Failed to spawn fallen body for {} — continuing downed flow without body", player.getGameProfile().name());
		}

		data.setDowned(true);
		if (body != null) {
			data.setBodyEntityId(body.getUUID());
		}
		data.setReturnLocation(
				origin.dimension().identifier().toString(),
				originPos.x,
				originPos.y,
				originPos.z
		);
		DownedAttachment.set(player, data);

		player.setHealth(player.getMaxHealth());
		applyDownedEffects(player);
		ChallengeInstanceManager.ensureInstance(player);
		ChallengeInstanceManager.teleportToChallenge(player);

		player.sendSystemMessage(Component.literal("You are downed. Right-click the red block in the challenge to revive, or wait for rescue."));
		broadcastNearby(origin, originPos, DownedPresentationS2CPayload.Cue.PLAYER_DOWNED, player.getUUID(), null);
		BreachNetworking.sendDownedPresentation(player, DownedPresentationS2CPayload.Cue.PLAYER_DOWNED, player.getUUID(), null);
		BreachMod.LOGGER.info("Started downed state for {} at {} {}", player.getGameProfile().name(), origin.dimension().identifier(), originPos);
	}

	public static void fieldRevive(ServerPlayer player, Vec3 returnPos, ServerLevel returnLevel, DownedPresentationS2CPayload.Cue cue, ServerPlayer medic) {
		DownedData data = DownedAttachment.get(player);
		if (!data.isDowned()) {
			return;
		}

		data.clearDownedState();
		DownedAttachment.set(player, data);

		clearDownedEffects(player);
		removeBody(player);
		restoreFieldReviveHealth(player);
		player.teleportTo(returnLevel, returnPos.x, returnPos.y, returnPos.z, Set.of(), player.getYRot(), player.getXRot(), true);
		player.sendSystemMessage(Component.literal("You were revived."));

		UUID medicId = medic != null ? medic.getUUID() : null;
		broadcastNearby(returnLevel, returnPos, cue, player.getUUID(), medicId);
		BreachNetworking.sendDownedPresentation(player, cue, player.getUUID(), medicId);
	}

	public static void reviveToBed(ServerPlayer player, ServerLevel level, BlockPos bedPos, ServerPlayer medic) {
		DownedData data = DownedAttachment.get(player);
		if (!data.isDowned()) {
			return;
		}

		data.clearDownedState();
		DownedAttachment.set(player, data);

		clearDownedEffects(player);
		removeBody(player);
		restoreFieldReviveHealth(player);
		player.teleportTo(level, bedPos.getX() + 0.5, bedPos.getY() + 0.5, bedPos.getZ() + 0.5, Set.of(), player.getYRot(), player.getXRot(), true);
		MedicalBedBlock.enterBed(player, bedPos);
		player.sendSystemMessage(Component.literal("Placed in medical bed. Rest to recover."));

		UUID medicId = medic != null ? medic.getUUID() : null;
		broadcastNearby(level, Vec3.atCenterOf(bedPos), DownedPresentationS2CPayload.Cue.BED_REVIVED, player.getUUID(), medicId);
		BreachNetworking.sendDownedPresentation(player, DownedPresentationS2CPayload.Cue.BED_REVIVED, player.getUUID(), medicId);
	}

	public static void startCarry(ServerPlayer carrier, FallenBodyEntity body) {
		body.beginCarry(carrier);
		CarryAttachment.setCarrying(carrier, true);
		carrier.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 99999, 1, false, false, true));
		carrier.sendSystemMessage(Component.literal("Carrying " + body.getOwnerName() + ". Shift + empty hand to drop."));

		Vec3 pos = body.position();
		broadcastNearby((ServerLevel) body.level(), pos, DownedPresentationS2CPayload.Cue.CARRY_STARTED, body.getOwnerUuid(), carrier.getUUID());
	}

	public static void stopCarry(ServerPlayer carrier, FallenBodyEntity body) {
		body.endCarry();
		CarryAttachment.setCarrying(carrier, false);
		carrier.removeEffect(MobEffects.SLOWNESS);
		carrier.sendSystemMessage(Component.literal("Dropped body."));

		Vec3 pos = body.position();
		broadcastNearby((ServerLevel) body.level(), pos, DownedPresentationS2CPayload.Cue.CARRY_STOPPED, body.getOwnerUuid(), carrier.getUUID());
	}

	public static FallenBodyEntity findBody(Player player) {
		DownedData data = DownedAttachment.get(player);
		if (data.bodyEntityId() == null || !(player.level() instanceof ServerLevel level)) {
			return null;
		}
		if (level.getEntity(data.bodyEntityId()) instanceof FallenBodyEntity body) {
			return body;
		}
		return null;
	}

	public static void removeBody(Player player) {
		DownedData data = DownedAttachment.get(player);
		if (data.bodyEntityId() == null) {
			return;
		}
		if (player.level() instanceof ServerLevel serverLevel) {
			if (serverLevel.getEntity(data.bodyEntityId()) instanceof FallenBodyEntity body) {
				body.discard();
			}
		}
		data.setBodyEntityId(null);
		DownedAttachment.set(player, data);
	}

	public static void onPlayerLogout(ServerPlayer player) {
		clearDownedEffects(player);
		CarryAttachment.setCarrying(player, false);
		player.removeEffect(MobEffects.SLOWNESS);
	}

	public static void clearDowned(ServerPlayer player) {
		DownedData data = DownedAttachment.get(player);
		if (!data.isDowned()) {
			return;
		}
		removeBody(player);
		data.clearDownedState();
		DownedAttachment.set(player, data);
		clearDownedEffects(player);
		player.setHealth(player.getMaxHealth());
	}

	public static void tickDownedPlayer(ServerPlayer player) {
		if (!DownedAttachment.get(player).isDowned()) {
			return;
		}
		if (!player.hasEffect(MobEffects.SLOWNESS)) {
			player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 2, false, false, false));
		}
	}

	public static void restoreFieldReviveHealth(ServerPlayer player) {
		if (BreachFeatures.INJURY_SYSTEM_ENABLED) {
			var injury = dev.breach.gameplay.injury.InjuryAttachment.get(player);
			injury.applyFieldRevive();
			dev.breach.gameplay.injury.InjuryAttachment.set(player, injury);
			InjuryManager.sync(player);
			return;
		}
		player.setHealth(Math.min(player.getMaxHealth(), DownedConstants.FIELD_REVIVE_HEALTH));
	}

	private static FallenBodyEntity spawnBody(ServerPlayer player, ServerLevel level, Vec3 pos) {
		BlockPos blockPos = BlockPos.containing(pos);
		level.getChunk(blockPos);

		FallenBodyEntity body = new FallenBodyEntity(BreachEntities.FALLEN_BODY, level);
		body.setOwner(player);
		body.snapToGround(pos);
		body.setYRot(player.getYRot());
		if (!level.addFreshEntity(body)) {
			BreachMod.LOGGER.warn("addFreshEntity failed for fallen body of {} at {}", player.getGameProfile().name(), blockPos);
			return null;
		}
		level.playSound(null, body.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 0.8f, 0.7f);
		return body;
	}

	private static void applyDownedEffects(ServerPlayer player) {
		player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 99999, 2, false, false, false));
		player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 99999, 0, false, false, false));
	}

	private static void clearDownedEffects(ServerPlayer player) {
		player.removeEffect(MobEffects.SLOWNESS);
		player.removeEffect(MobEffects.WEAKNESS);
	}

	private static void broadcastNearby(ServerLevel level, Vec3 pos, DownedPresentationS2CPayload.Cue cue, UUID subject, UUID actor) {
		for (ServerPlayer viewer : level.getServer().getPlayerList().getPlayers()) {
			if (viewer.level().dimension().equals(level.dimension()) && viewer.position().distanceToSqr(pos) < 64 * 64) {
				BreachNetworking.sendDownedPresentation(viewer, cue, subject, actor);
			}
		}
	}

	public record ReturnLocation(ResourceKey<Level> dimension, Vec3 position) {
	}
}
