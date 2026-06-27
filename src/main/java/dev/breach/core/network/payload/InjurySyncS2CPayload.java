package dev.breach.core.network.payload;

import dev.breach.BreachMod;
import dev.breach.gameplay.injury.InjuryData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record InjurySyncS2CPayload(InjuryData data) implements CustomPacketPayload {
	public static final Identifier ID = BreachMod.id("injury_sync");
	public static final CustomPacketPayload.Type<InjurySyncS2CPayload> TYPE = new CustomPacketPayload.Type<>(ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, InjurySyncS2CPayload> CODEC = StreamCodec.composite(
			InjuryData.STREAM_CODEC,
			InjurySyncS2CPayload::data,
			InjurySyncS2CPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
