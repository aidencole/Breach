package dev.breach.gameplay.downed;

import dev.breach.gameplay.carry.CarryAttachment;
import dev.breach.gameplay.medical.MedkitItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public class FallenBodyEntity extends Mob {
	private static final EntityDataAccessor<String> CARRIER_ID = SynchedEntityData.defineId(
			FallenBodyEntity.class,
			EntityDataSerializers.STRING
	);
	private static final EntityDataAccessor<String> OWNER_ID = SynchedEntityData.defineId(
			FallenBodyEntity.class,
			EntityDataSerializers.STRING
	);
	private static final EntityDataAccessor<String> OWNER_NAME = SynchedEntityData.defineId(
			FallenBodyEntity.class,
			EntityDataSerializers.STRING
	);

	private UUID ownerUuid = UUID.randomUUID();
	private String ownerName = "Unknown";

	public FallenBodyEntity(EntityType<? extends FallenBodyEntity> type, Level level) {
		super(type, level);
		this.setNoAi(true);
		this.setPersistenceRequired();
		this.setNoGravity(true);
		this.setCustomNameVisible(true);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes();
	}

	public void setOwner(ServerPlayer player) {
		this.ownerUuid = player.getUUID();
		this.ownerName = player.getGameProfile().name();
		this.entityData.set(OWNER_ID, ownerUuid.toString());
		this.entityData.set(OWNER_NAME, ownerName);
		this.setCustomName(Component.literal(ownerName + " (Downed)"));
		this.setYRot(player.getYRot());
		this.setPose(Pose.SLEEPING);
	}

	public UUID getOwnerUuid() {
		return ownerUuid;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public Optional<UUID> getCarrierUuid() {
		String raw = entityData.get(CARRIER_ID);
		return raw.isEmpty() ? Optional.empty() : Optional.of(UUID.fromString(raw));
	}

	public void setCarrierUuid(UUID carrierUuid) {
		if (carrierUuid != null && getCarrierUuid().map(carrierUuid::equals).orElse(false)) {
			return;
		}
		clearCarrier();
		entityData.set(CARRIER_ID, carrierUuid == null ? "" : carrierUuid.toString());
		if (carrierUuid != null && level().getEntity(carrierUuid) instanceof ServerPlayer carrier) {
			CarryAttachment.setCarrying(carrier, true);
		}
	}

	public void clearCarrier() {
		getCarrierUuid().ifPresent(id -> {
			if (level().getEntity(id) instanceof ServerPlayer carrier) {
				CarryAttachment.setCarrying(carrier, false);
			}
		});
		setCarrierUuid(null);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(CARRIER_ID, "");
		builder.define(OWNER_ID, ownerUuid.toString());
		builder.define(OWNER_NAME, ownerName);
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide()) {
			return;
		}

		if (getCarrierUuid().isPresent()) {
			if (level().getEntity(getCarrierUuid().get()) instanceof Player carrier) {
				Vec3 offset = carrier.getLookAngle().scale(0.35);
				setPos(carrier.getX() + offset.x, carrier.getY() + 2.05, carrier.getZ() + offset.z);
				setYRot(carrier.getYRot());
				setPose(Pose.SLEEPING);
			} else {
				clearCarrier();
			}
		} else {
			setPose(Pose.SLEEPING);
		}
	}

	@Override
	public InteractionResult interact(final Player player, final InteractionHand hand, final Vec3 location) {
		if (level().isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		if (!(player instanceof ServerPlayer serverPlayer)) {
			return InteractionResult.PASS;
		}

		ItemStack held = serverPlayer.getItemInHand(hand);
		if (held.getItem() instanceof MedkitItem) {
			if (MedkitItem.useOnFallenBody(serverPlayer, this, held)) {
				return InteractionResult.SUCCESS;
			}
		}

		return CarryManager.tryInteract(serverPlayer, this);
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
		return false;
	}

	@Override
	public void remove(net.minecraft.world.entity.Entity.RemovalReason reason) {
		clearCarrier();
		super.remove(reason);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		ownerUuid = UUID.fromString(input.getStringOr("Owner", ownerUuid.toString()));
		ownerName = input.getStringOr("OwnerName", ownerName);
		entityData.set(OWNER_ID, ownerUuid.toString());
		entityData.set(OWNER_NAME, ownerName);
		input.getString("Carrier").ifPresent(value -> setCarrierUuid(UUID.fromString(value)));
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.putString("Owner", ownerUuid.toString());
		output.putString("OwnerName", ownerName);
		getCarrierUuid().ifPresent(id -> output.putString("Carrier", id.toString()));
	}
}
