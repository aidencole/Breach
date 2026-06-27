package dev.breach.core.network.payload;

import dev.breach.BreachMod;
import dev.breach.core.event.EventPhase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record EventStateS2CPayload(EventPhase phase, int participantCount) implements CustomPacketPayload {
	public static final Identifier ID = BreachMod.id("event_state");
	public static final CustomPacketPayload.Type<EventStateS2CPayload> TYPE = new CustomPacketPayload.Type<>(ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, EventStateS2CPayload> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8.map(EventPhase::valueOf, EventPhase::name),
			EventStateS2CPayload::phase,
			ByteBufCodecs.VAR_INT,
			EventStateS2CPayload::participantCount,
			EventStateS2CPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
