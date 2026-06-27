package dev.breach.gameplay.downed;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.UUID;

public final class DownedData {
	public static final Codec<DownedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.fieldOf("downed").forGetter(DownedData::isDowned),
			Codec.STRING.optionalFieldOf("body_entity").forGetter(data -> Optional.ofNullable(data.bodyEntityId).map(UUID::toString)),
			Codec.STRING.optionalFieldOf("return_dim").forGetter(data -> Optional.ofNullable(data.returnDimension)),
			Codec.DOUBLE.optionalFieldOf("return_x").forGetter(data -> Optional.ofNullable(data.returnX)),
			Codec.DOUBLE.optionalFieldOf("return_y").forGetter(data -> Optional.ofNullable(data.returnY)),
			Codec.DOUBLE.optionalFieldOf("return_z").forGetter(data -> Optional.ofNullable(data.returnZ))
	).apply(instance, (downed, bodyId, returnDim, returnX, returnY, returnZ) -> new DownedData(
			downed,
			bodyId.map(UUID::fromString).orElse(null),
			returnDim.orElse(null),
			returnX.orElse(null),
			returnY.orElse(null),
			returnZ.orElse(null)
	)));

	public static final StreamCodec<FriendlyByteBuf, DownedData> STREAM_CODEC = StreamCodec.of(DownedData::write, DownedData::read);

	private boolean downed;
	private UUID bodyEntityId;
	private String returnDimension;
	private Double returnX;
	private Double returnY;
	private Double returnZ;

	public DownedData(boolean downed, UUID bodyEntityId, String returnDimension, Double returnX, Double returnY, Double returnZ) {
		this.downed = downed;
		this.bodyEntityId = bodyEntityId;
		this.returnDimension = returnDimension;
		this.returnX = returnX;
		this.returnY = returnY;
		this.returnZ = returnZ;
	}

	public static DownedData createDefault() {
		return new DownedData(false, null, null, null, null, null);
	}

	public boolean isDowned() {
		return downed;
	}

	public void setDowned(boolean downed) {
		this.downed = downed;
	}

	public UUID bodyEntityId() {
		return bodyEntityId;
	}

	public void setBodyEntityId(UUID bodyEntityId) {
		this.bodyEntityId = bodyEntityId;
	}

	public void setReturnLocation(String dimension, double x, double y, double z) {
		this.returnDimension = dimension;
		this.returnX = x;
		this.returnY = y;
		this.returnZ = z;
	}

	public String returnDimension() {
		return returnDimension;
	}

	public Double returnX() {
		return returnX;
	}

	public Double returnY() {
		return returnY;
	}

	public Double returnZ() {
		return returnZ;
	}

	public void clearReturnLocation() {
		this.returnDimension = null;
		this.returnX = null;
		this.returnY = null;
		this.returnZ = null;
	}

	public void clearDownedState() {
		downed = false;
		bodyEntityId = null;
		clearReturnLocation();
	}

	private static DownedData read(FriendlyByteBuf buf) {
		boolean downed = buf.readBoolean();
		UUID bodyId = buf.readBoolean() ? buf.readUUID() : null;
		String returnDim = buf.readBoolean() ? buf.readUtf() : null;
		Double x = null;
		Double y = null;
		Double z = null;
		if (buf.readBoolean()) {
			x = buf.readDouble();
			y = buf.readDouble();
			z = buf.readDouble();
		}
		return new DownedData(downed, bodyId, returnDim, x, y, z);
	}

	private static void write(FriendlyByteBuf buf, DownedData data) {
		buf.writeBoolean(data.downed);
		buf.writeBoolean(data.bodyEntityId != null);
		if (data.bodyEntityId != null) {
			buf.writeUUID(data.bodyEntityId);
		}
		buf.writeBoolean(data.returnDimension != null);
		if (data.returnDimension != null) {
			buf.writeUtf(data.returnDimension);
		}
		buf.writeBoolean(data.returnX != null);
		if (data.returnX != null) {
			buf.writeDouble(data.returnX);
			buf.writeDouble(data.returnY != null ? data.returnY : 0);
			buf.writeDouble(data.returnZ != null ? data.returnZ : 0);
		}
	}
}
