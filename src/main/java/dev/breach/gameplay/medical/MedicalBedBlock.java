package dev.breach.gameplay.medical;

import dev.breach.gameplay.downed.CarryManager;
import dev.breach.gameplay.downed.FallenBodyEntity;
import dev.breach.gameplay.injury.InjuryAttachment;
import dev.breach.gameplay.injury.InjuryManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MedicalBedBlock extends Block {
	private static final Map<BlockPos, UUID> OCCUPANTS = new HashMap<>();

	public MedicalBedBlock(Properties properties) {
		super(properties);
	}

	public boolean tryEnter(ServerPlayer player, BlockPos pos) {
		if (OCCUPANTS.containsKey(pos)) {
			return false;
		}
		OCCUPANTS.put(pos, player.getUUID());
		InjuryManager.startBedHealing(player, pos);
		return true;
	}

	public static void tryEnterStatic(ServerPlayer player, BlockPos pos) {
		if (player.level().getBlockState(pos).getBlock() instanceof MedicalBedBlock bed) {
			bed.tryEnter(player, pos);
		} else {
			InjuryManager.startBedHealing(player, pos);
		}
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

		if (OCCUPANTS.containsKey(pos) && !OCCUPANTS.get(pos).equals(serverPlayer.getUUID())) {
			serverPlayer.sendSystemMessage(Component.literal("Medical bed is occupied."));
			return InteractionResult.FAIL;
		}

		for (FallenBodyEntity body : level.getEntitiesOfClass(FallenBodyEntity.class, new AABB(pos).inflate(4.0))) {
			if (body.getCarrierUuid().map(id -> id.equals(serverPlayer.getUUID())).orElse(false)) {
				CarryManager.tryPlaceInBed(serverPlayer, body, pos);
				return InteractionResult.SUCCESS;
			}
		}

		if (OCCUPANTS.containsKey(pos)) {
			InjuryManager.stopBedHealing(serverPlayer);
			OCCUPANTS.remove(pos);
			serverPlayer.sendSystemMessage(Component.literal("Left medical bed."));
			return InteractionResult.SUCCESS;
		}

		if (tryEnter(serverPlayer, pos)) {
			serverPlayer.sendSystemMessage(Component.literal("Resting in medical bed to heal injuries."));
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.FAIL;
	}

	public static void tickOccupants(Level level) {
		OCCUPANTS.entrySet().removeIf(entry -> {
			ServerPlayer player = level.getServer().getPlayerList().getPlayer(entry.getValue());
			if (player == null) {
				return true;
			}
			if (!player.blockPosition().closerThan(entry.getKey(), 3.0)) {
				InjuryManager.stopBedHealing(player);
				return true;
			}
			return false;
		});
	}

	public static void release(ServerPlayer player) {
		OCCUPANTS.entrySet().removeIf(entry -> entry.getValue().equals(player.getUUID()));
		InjuryManager.stopBedHealing(player);
	}
}
