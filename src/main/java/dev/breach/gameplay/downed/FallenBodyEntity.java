package dev.breach.gameplay.downed;

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
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public class FallenBodyEntity extends ArmorStand {
	private static final EntityDataAccessor<String> CARRIER_ID = SynchedEntityData.defineId(
			FallenBodyEntity.class,
			EntityDataSerializers.STRING
	);

	private UUID ownerUuid = UUID.randomUUID();
	private String ownerName = "Unknown";

	public FallenBodyEntity(EntityType<? extends FallenBodyEntity> type, Level level) {
		super(type, level);
		this.setNoGravity(true);
		this.setInvisible(false);
		this.setCustomNameVisible(true);
		this.setYBodyRot(0.0f);
	}

	public void setOwner(ServerPlayer player) {
		this.ownerUuid = player.getUUID();
		this.ownerName = player.getGameProfile().name();
		this.setCustomName(Component.literal(ownerName + " (Downed)"));
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
		entityData.set(CARRIER_ID, carrierUuid == null ? "" : carrierUuid.toString());
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(CARRIER_ID, "");
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide()) {
			return;
		}

		getCarrierUuid().ifPresent(carrierId -> {
			if (level().getEntity(carrierId) instanceof Player carrier) {
				setPos(carrier.getX(), carrier.getY() + 2.0, carrier.getZ());
				setYRot(carrier.getYRot());
			} else {
				setCarrierUuid(null);
			}
		});
	}

	@Override
	public InteractionResult interact(final Player player, final InteractionHand hand, final Vec3 location) {
		if (level().isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		if (player instanceof ServerPlayer serverPlayer) {
			ItemStack held = serverPlayer.getItemInHand(hand);
			if (held.getItem() instanceof MedkitItem) {
				if (MedkitItem.useOnFallenBody(serverPlayer, this, held)) {
					return InteractionResult.SUCCESS;
				}
			}
			return CarryManager.tryInteract(serverPlayer, this);
		}

		return InteractionResult.PASS;
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
		return false;
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		ownerUuid = UUID.fromString(input.getStringOr("Owner", ownerUuid.toString()));
		ownerName = input.getStringOr("OwnerName", ownerName);
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
