package dev.breach.gameplay.injury;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public final class InjuryData {
	public static final Codec<InjuryData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.listOf().fieldOf("parts").forGetter(InjuryData::partsList),
			Codec.BOOL.fieldOf("downed").forGetter(InjuryData::isDowned),
			Codec.STRING.optionalFieldOf("body_entity").forGetter(data -> Optional.ofNullable(data.bodyEntityId).map(UUID::toString)),
			Codec.STRING.optionalFieldOf("return_dim").forGetter(data -> Optional.ofNullable(data.returnDimension)),
			Codec.DOUBLE.optionalFieldOf("return_x").forGetter(data -> Optional.ofNullable(data.returnX)),
			Codec.DOUBLE.optionalFieldOf("return_y").forGetter(data -> Optional.ofNullable(data.returnY)),
			Codec.DOUBLE.optionalFieldOf("return_z").forGetter(data -> Optional.ofNullable(data.returnZ))
	).apply(instance, (parts, downed, bodyId, returnDim, returnX, returnY, returnZ) -> {
		int[] array = new int[InjuryConstants.PART_COUNT];
		for (int i = 0; i < InjuryConstants.PART_COUNT; i++) {
			array[i] = i < parts.size() ? parts.get(i) : InjuryConstants.MAX_PART_HEALTH;
		}
		UUID bodyUuid = bodyId.map(UUID::fromString).orElse(null);
		return new InjuryData(
				array,
				downed,
				bodyUuid,
				returnDim.orElse(null),
				returnX.orElse(null),
				returnY.orElse(null),
				returnZ.orElse(null)
		);
	}));

	public static final StreamCodec<FriendlyByteBuf, InjuryData> STREAM_CODEC = StreamCodec.of(
			InjuryData::write,
			InjuryData::read
	);

	private final int[] parts;
	private boolean downed;
	private UUID bodyEntityId;
	private String returnDimension;
	private Double returnX;
	private Double returnY;
	private Double returnZ;

	public InjuryData(
			int[] parts,
			boolean downed,
			UUID bodyEntityId,
			String returnDimension,
			Double returnX,
			Double returnY,
			Double returnZ
	) {
		this.parts = Arrays.copyOf(parts, InjuryConstants.PART_COUNT);
		this.downed = downed;
		this.bodyEntityId = bodyEntityId;
		this.returnDimension = returnDimension;
		this.returnX = returnX;
		this.returnY = returnY;
		this.returnZ = returnZ;
	}

	public static InjuryData createDefault() {
		int[] parts = new int[InjuryConstants.PART_COUNT];
		Arrays.fill(parts, InjuryConstants.MAX_PART_HEALTH);
		return new InjuryData(parts, false, null, null, null, null, null);
	}

	private static InjuryData read(FriendlyByteBuf buf) {
		int[] parts = new int[InjuryConstants.PART_COUNT];
		for (int i = 0; i < InjuryConstants.PART_COUNT; i++) {
			parts[i] = buf.readVarInt();
		}
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
		return new InjuryData(parts, downed, bodyId, returnDim, x, y, z);
	}

	private static void write(FriendlyByteBuf buf, InjuryData data) {
		for (int part : data.parts) {
			buf.writeVarInt(part);
		}
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

	public java.util.List<Integer> partsList() {
		return Arrays.stream(parts).boxed().toList();
	}

	public int get(BodyPart part) {
		return parts[part.index()];
	}

	public void set(BodyPart part, int health) {
		parts[part.index()] = Math.clamp(health, 0, InjuryConstants.MAX_PART_HEALTH);
	}

	public void damage(BodyPart part, int amount) {
		set(part, get(part) - amount);
	}

	public void heal(BodyPart part, int amount) {
		set(part, get(part) + amount);
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

	public boolean isCritical() {
		return get(BodyPart.HEAD) <= 0 || get(BodyPart.CHEST) <= 0;
	}

	public void applyFieldRevive() {
		downed = false;
		if (get(BodyPart.HEAD) <= 0) {
			set(BodyPart.HEAD, InjuryConstants.FIELD_REVIVE_VITAL_HEALTH);
		}
		if (get(BodyPart.CHEST) <= 0) {
			set(BodyPart.CHEST, InjuryConstants.FIELD_REVIVE_VITAL_HEALTH);
		}
		clearReturnLocation();
	}

	public boolean isFullyHealed() {
		for (BodyPart part : BodyPart.values()) {
			if (get(part) < InjuryConstants.MAX_PART_HEALTH) {
				return false;
			}
		}
		return true;
	}

	public void fullHeal() {
		Arrays.fill(parts, InjuryConstants.MAX_PART_HEALTH);
		downed = false;
		clearReturnLocation();
	}
}
