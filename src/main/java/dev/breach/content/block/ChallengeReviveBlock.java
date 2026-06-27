package dev.breach.content.block;

import dev.breach.gameplay.challenge.ChallengeInstanceManager;
import dev.breach.gameplay.injury.InjuryAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ChallengeReviveBlock extends Block {
	public ChallengeReviveBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		if (player instanceof ServerPlayer serverPlayer && InjuryAttachment.get(serverPlayer).isDowned()) {
			ChallengeInstanceManager.completeChallengeRevive(serverPlayer);
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}
}
