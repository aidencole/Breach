package dev.breach.gameplay.medical;

import dev.breach.BreachFeatures;
import dev.breach.core.network.payload.DownedPresentationS2CPayload;
import dev.breach.gameplay.downed.DownedAttachment;
import dev.breach.gameplay.downed.DownedController;
import dev.breach.gameplay.downed.DownedData;
import dev.breach.gameplay.downed.FallenBodyEntity;
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

		if (!DownedAttachment.get(patient).isDowned()) {
			medic.sendSystemMessage(Component.literal("That player is not downed."));
			return InteractionResult.FAIL;
		}

		DownedData downed = DownedAttachment.get(patient);
		ServerLevel returnLevel = resolveReturnLevel(patient, downed, (ServerLevel) patient.level());
		Vec3 returnPos = downed.returnX() != null
				? new Vec3(downed.returnX(), downed.returnY(), downed.returnZ())
				: patient.position();

		DownedController.fieldRevive(patient, returnPos, returnLevel, DownedPresentationS2CPayload.Cue.FIELD_REVIVED, medic);
		stack.shrink(1);
		medic.sendSystemMessage(Component.literal("Field revived " + patient.getGameProfile().name() + "."));
		return InteractionResult.SUCCESS;
	}

	public static boolean useOnFallenBody(ServerPlayer medic, FallenBodyEntity body, ItemStack stack) {
		ServerPlayer owner = medic.level().getServer().getPlayerList().getPlayer(body.getOwnerUuid());
		if (owner == null || !DownedAttachment.get(owner).isDowned()) {
			return false;
		}

		ServerLevel returnLevel = (ServerLevel) body.level();
		Vec3 pos = body.position();
		body.discard();
		DownedController.fieldRevive(owner, pos, returnLevel, DownedPresentationS2CPayload.Cue.FIELD_REVIVED, medic);
		stack.shrink(1);
		medic.sendSystemMessage(Component.literal("Field revived " + owner.getGameProfile().name() + "."));
		return true;
	}

	private static ServerLevel resolveReturnLevel(ServerPlayer patient, DownedData downed, ServerLevel fallback) {
		if (downed.returnDimension() == null) {
			return fallback;
		}
		ServerLevel level = patient.level().getServer().getLevel(
				ResourceKey.create(Registries.DIMENSION, Identifier.parse(downed.returnDimension()))
		);
		return level != null ? level : fallback;
	}
}
