package dev.breach.core.network.payload;

import dev.breach.BreachMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.UUID;

public record DownedPresentationS2CPayload(Cue cue, UUID subjectId, UUID actorId) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<DownedPresentationS2CPayload> TYPE = new CustomPacketPayload.Type<>(
			Identifier.fromNamespaceAndPath(BreachMod.MOD_ID, "downed_presentation")
	);

	public static final StreamCodec<FriendlyByteBuf, DownedPresentationS2CPayload> CODEC = StreamCodec.of(
			(buf, payload) -> {
				buf.writeEnum(payload.cue);
				buf.writeUUID(payload.subjectId);
				buf.writeBoolean(payload.actorId != null);
				if (payload.actorId != null) {
					buf.writeUUID(payload.actorId);
				}
			},
			buf -> {
				Cue cue = buf.readEnum(Cue.class);
				UUID subject = buf.readUUID();
				UUID actor = buf.readBoolean() ? buf.readUUID() : null;
				return new DownedPresentationS2CPayload(cue, subject, actor);
			}
	);

	public enum Cue {
		PLAYER_DOWNED,
		CARRY_STARTED,
		CARRY_STOPPED,
		FIELD_REVIVED,
		BED_REVIVED,
		CHALLENGE_REVIVED
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
