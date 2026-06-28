package dev.breach.gameplay.downed;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import dev.breach.content.entity.BreachEntities;
import dev.breach.gameplay.medical.MedkitItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public class FallenBodyEntity extends Mob implements GeoEntity {
	private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
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
	private static final EntityDataAccessor<Byte> BODY_PHASE = SynchedEntityData.defineId(
			FallenBodyEntity.class,
			EntityDataSerializers.BYTE
	);

	private UUID ownerUuid;
	private String ownerName = "";
	private double smoothX;
	private double smoothY;
	private double smoothZ;
	private boolean initialized;

	public FallenBodyEntity(EntityType<? extends FallenBodyEntity> type, Level level) {
		super(type, level);
		this.setNoAi(true);
		this.setPersistenceRequired();
		this.setNoGravity(true);
		this.setCustomNameVisible(false);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes();
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return geoCache;
	}

	public void setOwner(ServerPlayer player) {
		this.ownerUuid = player.getUUID();
		this.ownerName = player.getGameProfile().name();
		this.entityData.set(OWNER_ID, ownerUuid.toString());
		this.entityData.set(OWNER_NAME, ownerName);
		this.setCustomName(Component.literal(ownerName));
	}

	public UUID getOwnerUuid() {
		if (ownerUuid == null) {
			String raw = entityData.get(OWNER_ID);
			if (!raw.isEmpty()) {
				ownerUuid = UUID.fromString(raw);
			}
		}
		return ownerUuid;
	}

	public String getOwnerName() {
		String synced = entityData.get(OWNER_NAME);
		return synced.isEmpty() ? ownerName : synced;
	}

	public FallenBodyPhase getBodyPhase() {
		return FallenBodyPhase.values()[entityData.get(BODY_PHASE) & 0x1];
	}

	private void setBodyPhase(FallenBodyPhase phase) {
		entityData.set(BODY_PHASE, (byte) phase.ordinal());
	}

	public Optional<UUID> getCarrierUuid() {
		String raw = entityData.get(CARRIER_ID);
		return raw.isEmpty() ? Optional.empty() : Optional.of(UUID.fromString(raw));
	}

	public void beginCarry(ServerPlayer carrier) {
		entityData.set(CARRIER_ID, carrier.getUUID().toString());
		setBodyPhase(FallenBodyPhase.CARRIED);
	}

	public void endCarry() {
		getCarrierUuid().ifPresent(id -> {
			if (level().getEntity(id) instanceof ServerPlayer carrier) {
				carrier.removeEffect(net.minecraft.world.effect.MobEffects.SLOWNESS);
			}
		});
		entityData.set(CARRIER_ID, "");
		setBodyPhase(FallenBodyPhase.GROUND);
		snapToGround(position());
	}

	public void snapToGround(Vec3 pos) {
		if (level().isClientSide()) {
			return;
		}
		placeOnSurface(pos);
	}

	public void placeOnSurface(Vec3 pos) {
		BlockPos surface = BlockPos.containing(pos.x, pos.y - 0.01, pos.z);
		double groundY = surface.getY() + 1.0;
		setPos(pos.x, groundY, pos.z);
		setXRot(0.0f);
		smoothX = pos.x;
		smoothY = groundY;
		smoothZ = pos.z;
		initialized = true;
	}

	public void alignFacing(float yRot) {
		setYRot(yRot);
		setYBodyRot(yRot);
		setYHeadRot(yRot);
		setXRot(0.0f);
	}

	public static FallenBodyEntity spawnModel(ServerLevel level, ServerPlayer owner, Vec3 pos, float yRot) {
		FallenBodyEntity body = new FallenBodyEntity(BreachEntities.FALLEN_BODY, level);
		body.setOwner(owner);
		body.alignFacing(yRot);
		body.placeOnSurface(pos);
		if (!level.addFreshEntity(body)) {
			return null;
		}
		return body;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(CARRIER_ID, "");
		builder.define(OWNER_ID, "");
		builder.define(OWNER_NAME, "");
		builder.define(BODY_PHASE, (byte) FallenBodyPhase.GROUND.ordinal());
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide()) {
			return;
		}

		if (!initialized) {
			snapToGround(position());
		}

		if (getBodyPhase() == FallenBodyPhase.CARRIED && getCarrierUuid().isPresent()) {
			if (level().getEntity(getCarrierUuid().get()) instanceof Player carrier) {
				Vec3 target = CarryAnchor.worldPosition(carrier);
				smoothX = Mth.lerp(0.45, smoothX, target.x);
				smoothY = Mth.lerp(0.45, smoothY, target.y);
				smoothZ = Mth.lerp(0.45, smoothZ, target.z);
				setPos(smoothX, smoothY, smoothZ);
				setYRot(Mth.lerp(0.35f, getYRot(), carrier.getYRot()));
				setXRot(0.0f);
			} else {
				endCarry();
			}
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
	public void remove(RemovalReason reason) {
		endCarry();
		super.remove(reason);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		String ownerId = input.getStringOr("Owner", "");
		if (!ownerId.isEmpty()) {
			ownerUuid = UUID.fromString(ownerId);
			entityData.set(OWNER_ID, ownerId);
		}
		ownerName = input.getStringOr("OwnerName", "");
		entityData.set(OWNER_NAME, ownerName);
		input.getString("Carrier").ifPresent(value -> entityData.set(CARRIER_ID, value));
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		if (ownerUuid != null) {
			output.putString("Owner", ownerUuid.toString());
		}
		output.putString("OwnerName", getOwnerName());
		getCarrierUuid().ifPresent(id -> output.putString("Carrier", id.toString()));
	}
}
