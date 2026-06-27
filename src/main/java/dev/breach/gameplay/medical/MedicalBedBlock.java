package dev.breach.gameplay.medical;

import com.mojang.datafixers.util.Either;
import dev.breach.content.BreachBedType;
import dev.breach.gameplay.downed.CarryManager;
import dev.breach.gameplay.downed.FallenBodyEntity;
import dev.breach.gameplay.injury.InjuryAttachment;
import dev.breach.gameplay.injury.InjuryManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MedicalBedBlock extends BedBlock {
	private static final Map<BlockPos, UUID> HEALING = new HashMap<>();

	public MedicalBedBlock(BreachBedType bedType, Properties properties) {
		super(bedType.dyeColor(), properties);
	}

	public static BlockPos headPos(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof BedBlock)) {
			return pos;
		}
		if (state.getValue(PART) == BedPart.HEAD) {
			return pos;
		}
		return pos.relative(getConnectedDirection(state));
	}

	public static void enterBed(ServerPlayer player, BlockPos anyPartPos) {
		Level level = player.level();
		BlockPos headPos = headPos(level, anyPartPos);
		Either<Player.BedSleepingProblem, Unit> result = player.startSleepInBed(headPos);
		if (result.left().isPresent()) {
			player.sendSystemMessage(Component.literal("Cannot use the medical bed right now."));
			return;
		}
		startHealing(player, headPos);
	}

	public static void startHealing(ServerPlayer player, BlockPos headPos) {
		HEALING.put(headPos, player.getUUID());
		InjuryManager.startBedHealing(player, headPos);
	}

	public static void stopHealing(ServerPlayer player) {
		HEALING.entrySet().removeIf(entry -> {
			if (entry.getValue().equals(player.getUUID())) {
				InjuryManager.stopBedHealing(player);
				return true;
			}
			return false;
		});
	}

	public static void release(ServerPlayer player) {
		stopHealing(player);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		if (!(player instanceof ServerPlayer serverPlayer)) {
			return InteractionResult.PASS;
		}

		if (InjuryAttachment.get(serverPlayer).isDowned()) {
			return InteractionResult.PASS;
		}

		BlockPos headPos = headPos(level, pos);

		for (FallenBodyEntity body : level.getEntitiesOfClass(FallenBodyEntity.class, new AABB(headPos).inflate(4.0))) {
			if (body.getCarrierUuid().map(id -> id.equals(serverPlayer.getUUID())).orElse(false)) {
				CarryManager.tryPlaceInBed(serverPlayer, body, headPos);
				return InteractionResult.SUCCESS;
			}
		}

		if (serverPlayer.isSleeping() && serverPlayer.getSleepingPos().map(headPos::equals).orElse(false)) {
			serverPlayer.stopSleepInBed(true, true);
			stopHealing(serverPlayer);
			serverPlayer.sendSystemMessage(Component.literal("Left medical bed."));
			return InteractionResult.SUCCESS;
		}

		if (state.getValue(OCCUPIED) && !isHealingPlayer(serverPlayer, headPos)) {
			serverPlayer.sendSystemMessage(Component.literal("Medical bed is occupied."));
			return InteractionResult.FAIL;
		}

		enterBed(serverPlayer, pos);
		serverPlayer.sendSystemMessage(Component.literal("Resting in medical bed to heal injuries."));
		return InteractionResult.SUCCESS;
	}

	private static boolean isHealingPlayer(ServerPlayer player, BlockPos headPos) {
		return player.getUUID().equals(HEALING.get(headPos));
	}

	public static void tickOccupants(Level level) {
		HEALING.entrySet().removeIf(entry -> {
			ServerPlayer player = level.getServer().getPlayerList().getPlayer(entry.getValue());
			if (player == null) {
				return true;
			}
			if (!player.isSleeping() || !player.getSleepingPos().map(entry.getKey()::equals).orElse(false)) {
				InjuryManager.stopBedHealing(player);
				return true;
			}
			return false;
		});
	}
}
