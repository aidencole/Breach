package dev.breach.gameplay.challenge;

import dev.breach.BreachMod;
import dev.breach.content.block.BreachBlocks;
import dev.breach.content.dimension.BreachDimensions;
import dev.breach.gameplay.downed.DownedAttachment;
import dev.breach.gameplay.downed.DownedController;
import dev.breach.gameplay.downed.DownedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
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
		var server = player.level().getServer();
		ServerLevel level = server.getLevel(BreachDimensions.CHALLENGE_LEVEL);
		if (level == null) {
			BreachMod.LOGGER.error(
					"Challenge dimension {} is not loaded. Loaded dimensions: {}",
					BreachDimensions.CHALLENGE_LEVEL.identifier(),
					server.levelKeys()
			);
		}
		return level;
	}

	public static void ensureInstance(ServerPlayer player) {
		ServerLevel level = challengeLevel(player);
		if (level == null) {
			return;
		}
		ensurePlatform(level, originFor(player));
	}

	public static BlockPos reviveBlockPos(BlockPos origin) {
		return origin.offset(0, -1, 2);
	}

	public static float reviveFacingYaw() {
		return 0.0f;
	}

	public static void teleportToChallenge(ServerPlayer player) {
		ServerLevel challenge = challengeLevel(player);
		if (challenge == null) {
			player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Challenge dimension unavailable. Contact an admin."));
			return;
		}

		ensureInstance(player);
		BlockPos origin = originFor(player);
		challenge.getChunk(origin);
		player.teleportTo(
				challenge,
				origin.getX() + 0.5,
				origin.getY(),
				origin.getZ() + 0.5,
				Set.of(),
				reviveFacingYaw(),
				0.0f,
				true
		);
		player.sendSystemMessage(Component.literal("Right-click the glowing red block ahead to revive yourself."));
	}

	public static void completeChallengeRevive(ServerPlayer player) {
		if (!DownedAttachment.get(player).isDowned()) {
			return;
		}

		DownedData data = DownedAttachment.get(player);
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
		BlockPos floor = origin.below();
		level.getChunk(floor);

		for (int x = -3; x <= 3; x++) {
			for (int z = -3; z <= 3; z++) {
				level.setBlockAndUpdate(floor.offset(x, 0, z), Blocks.STONE.defaultBlockState());
			}
		}

		for (int x = -3; x <= 3; x++) {
			level.setBlockAndUpdate(floor.offset(x, 0, -3), Blocks.STONE_BRICKS.defaultBlockState());
			level.setBlockAndUpdate(floor.offset(x, 0, 3), Blocks.STONE_BRICKS.defaultBlockState());
		}
		for (int z = -2; z <= 2; z++) {
			level.setBlockAndUpdate(floor.offset(-3, 0, z), Blocks.STONE_BRICKS.defaultBlockState());
			level.setBlockAndUpdate(floor.offset(3, 0, z), Blocks.STONE_BRICKS.defaultBlockState());
		}

		BlockPos revivePos = reviveBlockPos(origin);
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				BlockPos padPos = revivePos.offset(x, 0, z);
				if (padPos.equals(revivePos)) {
					level.setBlockAndUpdate(padPos, BreachBlocks.CHALLENGE_REVIVE.defaultBlockState());
				} else {
					level.setBlockAndUpdate(padPos, Blocks.CONCRETE.pick(DyeColor.RED).defaultBlockState());
				}
			}
		}
	}
}
