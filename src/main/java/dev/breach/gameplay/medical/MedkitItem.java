package dev.breach.gameplay.medical;

import dev.breach.gameplay.downed.DownedManager;
import dev.breach.gameplay.downed.FallenBodyEntity;
import dev.breach.gameplay.injury.InjuryAttachment;
import dev.breach.gameplay.injury.InjuryManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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

		reviveDownedPlayer(patient, (ServerLevel) patient.level(), patient.position());
		stack.shrink(1);
		medic.sendSystemMessage(Component.literal("Field revived " + patient.getGameProfile().name() + "."));
		return InteractionResult.SUCCESS;
	}

	public static void reviveDownedPlayer(ServerPlayer owner, ServerLevel returnLevel, net.minecraft.world.phys.Vec3 returnPos) {
		DownedManager.fieldRevive(owner, returnPos, returnLevel);
		InjuryManager.sync(owner);
	}

	public static boolean useOnFallenBody(ServerPlayer medic, FallenBodyEntity body, ItemStack stack) {
		ServerPlayer owner = medic.level().getServer().getPlayerList().getPlayer(body.getOwnerUuid());
		if (owner == null || !InjuryAttachment.get(owner).isDowned()) {
			return false;
		}

		ServerLevel returnLevel = (ServerLevel) body.level();
		var pos = body.position();
		body.discard();
		reviveDownedPlayer(owner, returnLevel, pos);
		stack.shrink(1);
		medic.sendSystemMessage(Component.literal("Field revived " + owner.getGameProfile().name() + "."));
		return true;
	}
}
