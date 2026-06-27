package dev.breach.gameplay.downed;

import dev.breach.content.block.BreachBlocks;
import dev.breach.content.item.BreachItems;
import dev.breach.gameplay.medical.MedkitItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class CarryManager {
	private CarryManager() {
	}

	public static net.minecraft.world.InteractionResult tryInteract(ServerPlayer carrier, FallenBodyEntity body) {
		var held = carrier.getMainHandItem();
		if (held.is(BreachItems.MEDKIT)) {
			if (MedkitItem.useOnFallenBody(carrier, body, held)) {
				return net.minecraft.world.InteractionResult.SUCCESS;
			}
		}

		if (body.getCarrierUuid().isPresent()) {
			if (body.getCarrierUuid().get().equals(carrier.getUUID())) {
				body.setCarrierUuid(null);
				carrier.sendSystemMessage(Component.literal("Dropped body."));
				return net.minecraft.world.InteractionResult.SUCCESS;
			}
			return net.minecraft.world.InteractionResult.FAIL;
		}

		if (carrier.level().getServer().getPlayerList().getPlayer(body.getOwnerUuid()) == null) {
			carrier.sendSystemMessage(Component.literal("Cannot carry this body."));
			return net.minecraft.world.InteractionResult.FAIL;
		}

		body.setCarrierUuid(carrier.getUUID());
		carrier.sendSystemMessage(Component.literal("Carrying " + body.getOwnerName() + ". Use medkit, medical bed, or use again to drop."));
		return net.minecraft.world.InteractionResult.SUCCESS;
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
		DownedManager.reviveToBed(owner, (net.minecraft.server.level.ServerLevel) carrier.level(), bedPos);
		carrier.sendSystemMessage(Component.literal("Placed " + owner.getGameProfile().name() + " in medical bed."));
	}
}
