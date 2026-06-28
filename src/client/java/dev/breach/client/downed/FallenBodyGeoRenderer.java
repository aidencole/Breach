package dev.breach.client.downed;

import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import dev.breach.gameplay.downed.FallenBodyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class FallenBodyGeoRenderer extends GeoEntityRenderer<FallenBodyEntity, LivingEntityRenderState> {
	public FallenBodyGeoRenderer(EntityRendererProvider.Context context) {
		super(context, new FallenBodyGeoModel());
	}

	@Override
	public void extractRenderState(FallenBodyEntity entity, LivingEntityRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		GeoRenderState geoState = (GeoRenderState) state;
		geoState.addGeckolibData(
				FallenBodyGeoModel.SKIN_TEXTURE,
				FallenBodySkinCache.resolve(entity.getOwnerUuid(), entity.getOwnerName())
		);
	}
}
