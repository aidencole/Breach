package dev.breach.client.downed;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.breach.gameplay.downed.FallenBodyEntity;
import dev.breach.gameplay.downed.FallenBodyPhase;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class FallenBodyGeoRenderer extends GeoEntityRenderer<FallenBodyEntity, LivingEntityRenderState> {
	public FallenBodyGeoRenderer(EntityRendererProvider.Context context) {
		super(context, new FallenBodyGeoModel());
		this.shadowRadius = 0.25f;
	}

	@Override
	public void extractRenderState(FallenBodyEntity entity, LivingEntityRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		float yaw = entity.getYRot();
		state.yRot = yaw;
		state.bodyRot = yaw;
		state.xRot = 0.0f;
		state.walkAnimationPos = 0.0f;
		state.walkAnimationSpeed = 0.0f;
		state.isInWater = false;
		state.isAutoSpinAttack = false;
		state.isUpsideDown = false;

		GeoRenderState geoState = (GeoRenderState) state;
		geoState.addGeckolibData(
				FallenBodyGeoModel.SKIN_TEXTURE,
				FallenBodySkinCache.resolve(entity.getOwnerUuid(), entity.getOwnerName())
		);
		geoState.addGeckolibData(DataTickets.ENTITY_BODY_YAW, yaw);
		this.shadowRadius = entity.getBodyPhase() == FallenBodyPhase.CARRIED ? 0.05f : 0.25f;
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected void applyRotations(RenderPassInfo renderPass, PoseStack poseStack, float nativeScale) {
		GeoRenderState geoState = (GeoRenderState) renderPass.renderState();
		LivingEntityRenderState state = convertRenderStateToLiving((LivingEntityRenderState) renderPass.renderState());
		float yaw = geoState.getOrDefaultGeckolibData(DataTickets.ENTITY_BODY_YAW, state.bodyRot);
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0f - yaw));
	}
}
