package dev.breach.client.downed;

import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import dev.breach.gameplay.downed.DownedConstants;
import dev.breach.gameplay.downed.FallenBodyEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

@SuppressWarnings({"rawtypes", "unchecked"})
final class FallenBodyOuterLayerGeoLayer extends GeoRenderLayer {
	FallenBodyOuterLayerGeoLayer(GeoEntityRenderer<FallenBodyEntity, LivingEntityRenderState> renderer) {
		super(renderer);
	}

	@Override
	public void preRender(RenderPassInfo renderPass, SubmitNodeCollector collector) {
		if (!renderPass.willRender()) {
			return;
		}
		renderPass.addBoneUpdater((info, bones) -> configureOuterPass(info, bones));
	}

	@Override
	public void submitRenderTask(RenderPassInfo renderPass, SubmitNodeCollector collector) {
		if (!renderPass.willRender()) {
			return;
		}
		LivingEntityRenderState state = (LivingEntityRenderState) renderPass.renderState();
		GeoEntityRenderer<FallenBodyEntity, LivingEntityRenderState> entityRenderer =
				(GeoEntityRenderer<FallenBodyEntity, LivingEntityRenderState>) getRenderer();
		Identifier texture = entityRenderer.getTextureLocation(state);
		RenderType renderType = entityRenderer.getRenderType(state, texture);
		if (renderType == null) {
			return;
		}
		entityRenderer.submitRenderTasks(renderPass, collector.order(1), renderType);
	}

	private static void configureOuterPass(RenderPassInfo<?> renderPass, BoneSnapshots bones) {
		float inflate = DownedConstants.FALLEN_BODY_OUTER_LAYER_INFLATE;
		for (var entry : renderPass.model().boneLookup().get().entrySet()) {
			String boneName = entry.getKey();
			bones.ifPresent(boneName, snapshot -> {
				if (FallenBodyLayerBones.isOuterLayer(boneName)) {
					snapshot.skipRender(false).setScale(inflate, inflate, inflate);
				} else {
					snapshot.skipRender(true);
				}
			});
		}
	}
}
