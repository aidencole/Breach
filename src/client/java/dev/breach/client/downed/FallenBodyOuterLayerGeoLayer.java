package dev.breach.client.downed;

import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import dev.breach.gameplay.downed.DownedConstants;
import dev.breach.gameplay.downed.FallenBodyEntity;
import dev.breach.gameplay.downed.FallenBodyPhase;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

@SuppressWarnings({"rawtypes", "unchecked"})
public class FallenBodyOuterLayerGeoLayer extends GeoRenderLayer {
	private final FallenBodyOverlayModel wideModel;
	private final FallenBodyOverlayModel slimModel;

	public FallenBodyOuterLayerGeoLayer(
			EntityRendererProvider.Context context,
			GeoEntityRenderer<FallenBodyEntity, LivingEntityRenderState> renderer
	) {
		super(renderer);
		this.wideModel = new FallenBodyOverlayModel(context.bakeLayer(ModelLayers.PLAYER), false);
		this.slimModel = new FallenBodyOverlayModel(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
	}

	@Override
	public void submitRenderTask(RenderPassInfo pass, SubmitNodeCollector collector) {
		if (!pass.willRender()) {
			return;
		}

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

		FallenBodyOverlayModel model = profile.slim() ? this.slimModel : this.wideModel;
		FallenBodyOverlayState overlayState = FallenBodyOverlayState.forPhase(phase, renderState.ageInTicks);
		Identifier texture = profile.texture();

		var poseStack = pass.poseStack();
		poseStack.pushPose();
		if (phase == FallenBodyPhase.GROUND) {
			poseStack.translate(0.0, 0.02, 0.0);
		} else {
			poseStack.translate(0.0, -0.55, 0.0);
		}

		float overlayScale = DownedConstants.FALLEN_BODY_VANILLA_MODEL_SCALE / DownedConstants.FALLEN_BODY_GEO_SCALE;
		poseStack.scale(overlayScale, overlayScale, overlayScale);

		collector.order(1).submitModel(
				model,
				overlayState,
				poseStack,
				texture,
				pass.packedLight(),
				pass.packedOverlay(),
				pass.renderColor(),
				null
		);
		poseStack.popPose();
	}
}
