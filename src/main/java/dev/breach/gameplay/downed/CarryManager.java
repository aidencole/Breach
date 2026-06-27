package dev.breach.gameplay.downed;

import dev.breach.content.block.BreachBlocks;
import dev.breach.content.item.BreachItems;
import dev.breach.gameplay.medical.MedkitItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

public final class CarryManager {
	private CarryManager() {
	}

	public static InteractionResult tryInteract(ServerPlayer carrier, FallenBodyEntity body) {
		ItemStack mainHand = carrier.getItemInHand(InteractionHand.MAIN_HAND);
		if (mainHand.is(BreachItems.MEDKIT)) {
			if (MedkitItem.useOnFallenBody(carrier, body, mainHand)) {
				return InteractionResult.SUCCESS;
			}
		}

		if (body.getCarrierUuid().isPresent()) {
			if (body.getCarrierUuid().get().equals(carrier.getUUID()) && canCarryInteract(carrier)) {
				DownedController.stopCarry(carrier, body);
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		}

		if (!canCarryInteract(carrier)) {
			return InteractionResult.PASS;
		}

		if (carrier.level().getServer().getPlayerList().getPlayer(body.getOwnerUuid()) == null) {
			carrier.sendSystemMessage(Component.literal("Cannot carry this body."));
			return InteractionResult.FAIL;
		}

		DownedController.startCarry(carrier, body);
		return InteractionResult.SUCCESS;
	}

	public static boolean canCarryInteract(ServerPlayer player) {
		return player.isShiftKeyDown()
				&& player.getMainHandItem().isEmpty()
				&& player.getOffhandItem().isEmpty();
	}

	public static void tryPlaceInBed(ServerPlayer carrier, FallenBodyEntity body, net.minecraft.core.BlockPos bedPos) {
		if (!carrier.level().getBlockState(bedPos).is(BreachBlocks.MEDICAL_BED)) {
			return;
		}

		ServerPlayer owner = carrier.level().getServer().getPlayerList().getPlayer(body.getOwnerUuid());
		if (owner == null) {
			return;
		}

		body.discard();
		DownedController.reviveToBed(owner, (net.minecraft.server.level.ServerLevel) carrier.level(), bedPos, carrier);
		carrier.sendSystemMessage(Component.literal("Placed " + owner.getGameProfile().name() + " in medical bed."));
	}
}
