package dev.breach.client.downed;

import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import dev.breach.gameplay.downed.DownedConstants;
import dev.breach.gameplay.downed.FallenBodyEntity;
import dev.breach.gameplay.downed.FallenBodyPhase;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

@SuppressWarnings({"rawtypes", "unchecked"})
public class FallenBodyGeoRenderer extends GeoEntityRenderer<FallenBodyEntity, LivingEntityRenderState> {
	private final FallenBodyOverlayRenderer overlayRenderer;

	public FallenBodyGeoRenderer(EntityRendererProvider.Context context) {
		super(context, new FallenBodyGeoModel());
		this.overlayRenderer = new FallenBodyOverlayRenderer(context);
		this.withScale(DownedConstants.FALLEN_BODY_GEO_SCALE);
		this.shadowRadius = 0.25f;
	}

	@Override
	public void applyRenderLayers(RenderPassInfo pass, SubmitNodeCollector collector) {
		super.applyRenderLayers(pass, collector);
		this.overlayRenderer.render(pass, collector);
	}

	@Override
	public void extractRenderState(FallenBodyEntity entity, LivingEntityRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		state.scale = 1.0f;
		state.ageScale = 1.0f;
		state.isBaby = false;

		GeoRenderState geoState = (GeoRenderState) state;
		java.util.UUID ownerUuid = entity.getOwnerUuid();
		String ownerName = entity.getOwnerName();
		geoState.addGeckolibData(FallenBodyGeoModel.OWNER_UUID, ownerUuid);
		geoState.addGeckolibData(FallenBodyGeoModel.OWNER_NAME, ownerName);
		geoState.addGeckolibData(
				FallenBodyGeoModel.SKIN_PROFILE,
				FallenBodySkinCache.resolve(ownerUuid, ownerName)
		);
		geoState.addGeckolibData(FallenBodyGeoModel.BODY_PHASE, entity.getBodyPhase());
		this.shadowRadius = entity.getBodyPhase() == FallenBodyPhase.CARRIED ? 0.05f : 0.25f;
	}
}
