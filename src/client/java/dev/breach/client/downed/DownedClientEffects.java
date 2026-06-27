package dev.breach.client.downed;

import dev.breach.core.network.payload.DownedPresentationS2CPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public final class DownedClientEffects {
	private DownedClientEffects() {
	}

	public static void handle(DownedPresentationS2CPayload payload) {
		Minecraft client = Minecraft.getInstance();
		if (client.level == null) {
			return;
		}

		Player subject = findPlayer(client, payload.subjectId());
		Player actor = payload.actorId() != null ? findPlayer(client, payload.actorId()) : null;

		switch (payload.cue()) {
			case PLAYER_DOWNED -> playDowned(subject);
			case CARRY_STARTED -> playCarryStart(subject, actor);
			case CARRY_STOPPED -> playCarryStop(subject, actor);
			case FIELD_REVIVED, CHALLENGE_REVIVED -> playFieldRevive(subject);
			case BED_REVIVED -> playBedRevive(subject);
		}
	}

	private static void playDowned(Player subject) {
		if (subject == null) {
			return;
		}
		LocalPlayer local = Minecraft.getInstance().player;
		subject.level().playLocalSound(
				subject.getX(), subject.getY(), subject.getZ(),
				SoundEvents.PLAYER_DEATH, SoundSource.PLAYERS,
				subject == local ? 0.6f : 0.35f,
				0.85f,
				false
		);
		if (subject == local) {
			local.level().addParticle(
					ParticleTypes.LARGE_SMOKE,
					local.getX(), local.getEyeY(), local.getZ(),
					0.0, 0.05, 0.0
			);
		}
	}

	private static void playCarryStart(Player subject, Player actor) {
		if (actor == null) {
			return;
		}
		actor.level().playLocalSound(
				actor.getX(), actor.getY(), actor.getZ(),
				SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS,
				0.55f, 0.75f,
				false
		);
	}

	private static void playCarryStop(Player subject, Player actor) {
		if (actor == null) {
			return;
		}
		actor.level().playLocalSound(
				actor.getX(), actor.getY(), actor.getZ(),
				SoundEvents.WOOL_STEP, SoundSource.PLAYERS,
				0.45f, 0.8f,
				false
		);
	}

	private static void playFieldRevive(Player subject) {
		if (subject == null) {
			return;
		}
		subject.level().playLocalSound(
				subject.getX(), subject.getY(), subject.getZ(),
				SoundEvents.TOTEM_USE, SoundSource.PLAYERS,
				0.35f, 1.35f,
				false
		);
		for (int i = 0; i < 8; i++) {
			subject.level().addParticle(
					ParticleTypes.HEART,
					subject.getX() + (subject.getRandom().nextDouble() - 0.5) * 0.6,
					subject.getY() + 1.0,
					subject.getZ() + (subject.getRandom().nextDouble() - 0.5) * 0.6,
					0.0, 0.04, 0.0
			);
		}
	}

	private static void playBedRevive(Player subject) {
		if (subject == null) {
			return;
		}
		subject.level().playLocalSound(
				subject.getX(), subject.getY(), subject.getZ(),
				SoundEvents.WOOL_PLACE, SoundSource.PLAYERS,
				0.5f, 0.9f,
				false
		);
	}

	private static Player findPlayer(Minecraft client, java.util.UUID id) {
		if (client.level == null) {
			return null;
		}
		if (client.player != null && client.player.getUUID().equals(id)) {
			return client.player;
		}
		for (Player player : client.level.players()) {
			if (player.getUUID().equals(id)) {
				return player;
			}
		}
		return null;
	}
}
