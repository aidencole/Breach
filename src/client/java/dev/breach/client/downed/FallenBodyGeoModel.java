package dev.breach.client.downed;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import dev.breach.BreachMod;
import dev.breach.gameplay.downed.FallenBodyEntity;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;

public class FallenBodyGeoModel extends DefaultedEntityGeoModel<FallenBodyEntity> {
	public static final DataTicket<Identifier> SKIN_TEXTURE = DataTicket.create("skin_texture", Identifier.class);

	public FallenBodyGeoModel() {
		super(BreachMod.id("fallen_body_wide"));
	}

	@Override
	public Identifier getTextureResource(GeoRenderState renderState) {
		return renderState.getOrDefaultGeckolibData(
				SKIN_TEXTURE,
				DefaultPlayerSkin.getDefaultSkin().body().texturePath()
		);
	}
}
