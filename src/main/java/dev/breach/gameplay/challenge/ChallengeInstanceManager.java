package dev.breach.gameplay.challenge;

import dev.breach.content.block.BreachBlocks;
import dev.breach.content.dimension.BreachDimensions;
import dev.breach.gameplay.downed.DownedController;
import dev.breach.gameplay.injury.InjuryAttachment;
import dev.breach.gameplay.injury.InjuryData;
import dev.breach.gameplay.injury.InjuryManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public final class ChallengeInstanceManager {
	private static final int SPACING = 512;

	private ChallengeInstanceManager() {
	}

	public static BlockPos originFor(ServerPlayer player) {
		long hash = player.getUUID().getMostSignificantBits() ^ player.getUUID().getLeastSignificantBits();
		int gridX = (int) Math.floorMod(hash, 128);
		int gridZ = (int) Math.floorMod(hash >>> 7, 128);
		return new BlockPos(gridX * SPACING, 64, gridZ * SPACING);
	}

	public static ServerLevel challengeLevel(ServerPlayer player) {
		return player.level().getServer().getLevel(BreachDimensions.CHALLENGE_LEVEL);
	}

	public static void ensureInstance(ServerPlayer player) {
		ServerLevel level = challengeLevel(player);
		if (level == null) {
			return;
		}
		ensurePlatform(level, originFor(player));
	}

	public static BlockPos reviveBlockPos(BlockPos origin) {
		return origin.offset(2, 1, 0);
	}

	public static void teleportToChallenge(ServerPlayer player, DownedController.ReturnLocation returnPos) {
		ServerLevel challenge = challengeLevel(player);
		if (challenge == null) {
			return;
		}

		InjuryData data = InjuryAttachment.get(player);
		data.setReturnLocation(
				returnPos.dimension().identifier().toString(),
				returnPos.position().x,
				returnPos.position().y,
				returnPos.position().z
		);
		InjuryAttachment.set(player, data);

		ensureInstance(player);
		BlockPos origin = originFor(player);
		player.teleportTo(
				challenge,
				origin.getX() + 0.5,
				origin.getY(),
				origin.getZ() + 0.5,
				Set.of(),
				player.getYRot(),
				player.getXRot(),
				true
		);
	}

	public static void completeChallengeRevive(ServerPlayer player) {
		if (!InjuryAttachment.get(player).isDowned()) {
			return;
		}

		var data = InjuryAttachment.get(player);
		if (data.returnDimension() == null || data.returnX() == null) {
			return;
		}

		ServerLevel returnLevel = player.level().getServer().getLevel(
				ResourceKey.create(Registries.DIMENSION, Identifier.parse(data.returnDimension()))
		);
		if (returnLevel == null) {
			returnLevel = player.level().getServer().overworld();
		}

		Vec3 pos = new Vec3(data.returnX(), data.returnY(), data.returnZ());
		DownedController.fieldRevive(
				player,
				pos,
				returnLevel,
				dev.breach.core.network.payload.DownedPresentationS2CPayload.Cue.CHALLENGE_REVIVED,
				null
		);
	}

	private static void ensurePlatform(ServerLevel level, BlockPos origin) {
		BlockPos platform = origin.below();
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				level.setBlockAndUpdate(platform.offset(x, 0, z), net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());
			}
		}

		BlockPos revivePos = reviveBlockPos(origin);
		if (!level.getBlockState(revivePos).is(BreachBlocks.CHALLENGE_REVIVE)) {
			level.setBlockAndUpdate(revivePos, BreachBlocks.CHALLENGE_REVIVE.defaultBlockState());
		}
	}
}
