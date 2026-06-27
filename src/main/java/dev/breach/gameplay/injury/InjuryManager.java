package dev.breach.gameplay.injury;

import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class InjuryManager {
	private static final Map<UUID, BlockPos> BED_HEALING = new HashMap<>();

	private InjuryManager() {
	}

	public static void sync(net.minecraft.server.level.ServerPlayer player) {
		dev.breach.core.network.BreachNetworking.sendInjury(player, InjuryAttachment.get(player));
	}

	public static void damage(net.minecraft.server.level.ServerPlayer player, BodyPart part, int amount) {
		InjuryData data = InjuryAttachment.get(player);
		if (data.isDowned()) {
			return;
		}

		data.damage(part, amount);
		InjuryAttachment.set(player, data);
		sync(player);

		if (data.isCritical()) {
			dev.breach.gameplay.downed.DownedManager.tryDownPlayer(player);
		}
	}

	public static void onVanillaDamage(net.minecraft.server.level.ServerPlayer player, net.minecraft.world.damagesource.DamageSource source, float amount) {
		player.setHealth(player.getMaxHealth());
		if (InjuryAttachment.get(player).isDowned()) {
			return;
		}
		int damage = Math.max(1, Math.round(amount));
		BodyPart part = BodyPart.CHEST;
		damage(player, part, damage);
	}

	public static void healPart(net.minecraft.server.level.ServerPlayer player, BodyPart part, int amount) {
		InjuryData data = InjuryAttachment.get(player);
		data.heal(part, amount);
		InjuryAttachment.set(player, data);
		sync(player);
	}

	public static void startBedHealing(net.minecraft.server.level.ServerPlayer player, BlockPos bedPos) {
		BED_HEALING.put(player.getUUID(), bedPos);
	}

	public static void stopBedHealing(net.minecraft.server.level.ServerPlayer player) {
		BED_HEALING.remove(player.getUUID());
	}

	public static boolean isBedHealing(net.minecraft.server.level.ServerPlayer player) {
		return BED_HEALING.containsKey(player.getUUID());
	}

	public static void tickBedHealing(net.minecraft.server.level.ServerPlayer player) {
		if (!isBedHealing(player)) {
			return;
		}

		if (player.tickCount % InjuryConstants.BED_HEAL_INTERVAL_TICKS != 0) {
			return;
		}

		InjuryData data = InjuryAttachment.get(player);
		if (data.isFullyHealed()) {
			stopBedHealing(player);
			dev.breach.gameplay.medical.MedicalBedBlock.release(player);
			if (player.isSleeping()) {
				player.stopSleepInBed(true, true);
			}
			return;
		}

		for (BodyPart part : BodyPart.values()) {
			if (data.get(part) < InjuryConstants.MAX_PART_HEALTH) {
				data.heal(part, 1);
				InjuryAttachment.set(player, data);
				sync(player);
				return;
			}
		}
	}
}
