package dev.breach.client.downed;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import dev.breach.BreachMod;
import dev.breach.gameplay.downed.FallenBodyEntity;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;

public class FallenBodyGeoModel extends GeoModel<FallenBodyEntity> {
	public static final DataTicket<Identifier> SKIN_TEXTURE = DataTicket.create("skin_texture", Identifier.class);

	private static final Identifier MODEL = BreachMod.id("geo/fallen_body_wide");

	@Override
	public Identifier getModelResource(GeoRenderState renderState) {
		return MODEL;
	}

	@Override
	public Identifier getTextureResource(GeoRenderState renderState) {
		return renderState.getOrDefaultGeckolibData(
				SKIN_TEXTURE,
				DefaultPlayerSkin.getDefaultSkin().body().texturePath()
		);
	}

	@Override
	public Identifier getAnimationResource(FallenBodyEntity animatable) {
		return null;
	}
}
