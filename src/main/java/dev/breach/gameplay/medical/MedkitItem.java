package dev.breach.gameplay.medical;

import dev.breach.core.network.payload.DownedPresentationS2CPayload;
import dev.breach.gameplay.downed.DownedController;
import dev.breach.gameplay.downed.FallenBodyEntity;
import dev.breach.gameplay.injury.InjuryAttachment;
import dev.breach.gameplay.injury.InjuryManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MedkitItem extends Item {
	public MedkitItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity target, InteractionHand hand) {
		if (user.level().isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		if (!(user instanceof ServerPlayer medic) || !(target instanceof ServerPlayer patient)) {
			return InteractionResult.PASS;
		}

		if (!InjuryAttachment.get(patient).isDowned()) {
			medic.sendSystemMessage(Component.literal("That player is not downed."));
			return InteractionResult.FAIL;
		}

		var injury = InjuryAttachment.get(patient);
		ServerLevel returnLevel = resolveReturnLevel(patient, (ServerLevel) patient.level());
		Vec3 returnPos = injury.returnX() != null
				? new Vec3(injury.returnX(), injury.returnY(), injury.returnZ())
				: patient.position();

		DownedController.fieldRevive(patient, returnPos, returnLevel, DownedPresentationS2CPayload.Cue.FIELD_REVIVED, medic);
		stack.shrink(1);
		medic.sendSystemMessage(Component.literal("Field revived " + patient.getGameProfile().name() + "."));
		InjuryManager.sync(patient);
		return InteractionResult.SUCCESS;
	}

	public static boolean useOnFallenBody(ServerPlayer medic, FallenBodyEntity body, ItemStack stack) {
		ServerPlayer owner = medic.level().getServer().getPlayerList().getPlayer(body.getOwnerUuid());
		if (owner == null || !InjuryAttachment.get(owner).isDowned()) {
			return false;
		}

		ServerLevel returnLevel = (ServerLevel) body.level();
		Vec3 pos = body.position();
		body.discard();
		DownedController.fieldRevive(owner, pos, returnLevel, DownedPresentationS2CPayload.Cue.FIELD_REVIVED, medic);
		stack.shrink(1);
		medic.sendSystemMessage(Component.literal("Field revived " + owner.getGameProfile().name() + "."));
		InjuryManager.sync(owner);
		return true;
	}

	private static ServerLevel resolveReturnLevel(ServerPlayer patient, ServerLevel fallback) {
		var injury = InjuryAttachment.get(patient);
		if (injury.returnDimension() == null) {
			return fallback;
		}
		ServerLevel level = patient.level().getServer().getLevel(
				ResourceKey.create(Registries.DIMENSION, Identifier.parse(injury.returnDimension()))
		);
		return level != null ? level : fallback;
	}
}
