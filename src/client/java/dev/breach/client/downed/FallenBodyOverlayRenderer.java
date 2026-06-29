package dev.breach.client.downed;

import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import dev.breach.gameplay.downed.DownedConstants;
import dev.breach.gameplay.downed.FallenBodyPhase;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;

public final class FallenBodyOverlayRenderer {
	private final FallenBodyOverlayModel wideModel;
	private final FallenBodyOverlayModel slimModel;
	private final PlayerSkinRenderCache skinRenderCache;

	public FallenBodyOverlayRenderer(EntityRendererProvider.Context context) {
		this.wideModel = new FallenBodyOverlayModel(context.bakeLayer(ModelLayers.PLAYER), false);
		this.slimModel = new FallenBodyOverlayModel(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
		this.skinRenderCache = context.getPlayerSkinRenderCache();
	}

	public void render(RenderPassInfo<?> pass, SubmitNodeCollector collector) {
		GeoRenderState geoState = (GeoRenderState) pass.renderState();
		if (!(geoState instanceof LivingEntityRenderState renderState)) {
			return;
		}

		FallenBodySkinProfile profile = geoState.getOrDefaultGeckolibData(
				FallenBodyGeoModel.SKIN_PROFILE,
				FallenBodySkinProfile.fallback(java.util.UUID.randomUUID())
		);
		FallenBodyPhase phase = geoState.getOrDefaultGeckolibData(
				FallenBodyGeoModel.BODY_PHASE,
				FallenBodyPhase.GROUND
		);

		PlayerModel model = profile.slim() ? this.slimModel : this.wideModel;
		FallenBodyOverlayState overlayState = FallenBodyOverlayState.forPhase(phase, renderState.ageInTicks);
		model.setupAnim(overlayState);

		RenderType renderType = this.skinRenderCache.getOrDefault(
				profile.toResolvableProfile(
						geoState.getOrDefaultGeckolibData(FallenBodyGeoModel.OWNER_UUID, java.util.UUID.randomUUID()),
						geoState.getOrDefaultGeckolibData(FallenBodyGeoModel.OWNER_NAME, "")
				)
		).renderType();

		var poseStack = pass.poseStack();
		poseStack.pushPose();
		poseStack.last().set(pass.getModelRenderMatrixPose());

		if (phase == FallenBodyPhase.GROUND) {
			poseStack.translate(0.0, 0.02, 0.0);
		} else {
			poseStack.translate(0.0, -0.55, 0.0);
		}

		float overlayScale = DownedConstants.FALLEN_BODY_VANILLA_MODEL_SCALE / DownedConstants.FALLEN_BODY_GEO_SCALE;
		poseStack.scale(overlayScale, overlayScale, overlayScale);

		int light = pass.packedLight();
		int overlay = pass.packedOverlay();
		int color = pass.renderColor();
		ModelPart root = model.root();

		submitPart(collector, poseStack, renderType, light, overlay, color, model.hat, root, model.head);
		submitPart(collector, poseStack, renderType, light, overlay, color, model.jacket, root, model.body);
		submitPart(collector, poseStack, renderType, light, overlay, color, model.leftSleeve, root, model.leftArm);
		submitPart(collector, poseStack, renderType, light, overlay, color, model.rightSleeve, root, model.rightArm);
		submitPart(collector, poseStack, renderType, light, overlay, color, model.leftPants, root, model.leftLeg);
		submitPart(collector, poseStack, renderType, light, overlay, color, model.rightPants, root, model.rightLeg);

		poseStack.popPose();
	}

	private static void submitPart(
			SubmitNodeCollector collector,
			com.mojang.blaze3d.vertex.PoseStack poseStack,
			RenderType renderType,
			int light,
			int overlay,
			int color,
			ModelPart part,
			ModelPart... chain
	) {
		poseStack.pushPose();
		for (ModelPart link : chain) {
			link.translateAndRotate(poseStack);
		}
		collector.submitModelPart(part, poseStack, renderType, light, overlay, null, color, null);
		poseStack.popPose();
	}
}
