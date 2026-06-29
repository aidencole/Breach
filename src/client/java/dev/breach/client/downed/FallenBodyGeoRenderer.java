package dev.breach.client.downed;

import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import dev.breach.gameplay.downed.DownedConstants;
import dev.breach.gameplay.downed.FallenBodyEntity;
import dev.breach.gameplay.downed.FallenBodyPhase;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class FallenBodyGeoRenderer extends GeoEntityRenderer<FallenBodyEntity, LivingEntityRenderState> {
	public FallenBodyGeoRenderer(EntityRendererProvider.Context context) {
		super(context, new FallenBodyGeoModel());
		this.withScale(DownedConstants.FALLEN_BODY_GEO_SCALE);
		this.shadowRadius = 0.25f;
	}

	@Override
	public void extractRenderState(FallenBodyEntity entity, LivingEntityRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		state.scale = 1.0f;
		state.ageScale = 1.0f;
		state.isBaby = false;

		GeoRenderState geoState = (GeoRenderState) state;
		geoState.addGeckolibData(
				FallenBodyGeoModel.SKIN_PROFILE,
				FallenBodySkinCache.resolve(entity.getOwnerUuid(), entity.getOwnerName())
		);
		this.shadowRadius = entity.getBodyPhase() == FallenBodyPhase.CARRIED ? 0.05f : 0.25f;
	}
}
