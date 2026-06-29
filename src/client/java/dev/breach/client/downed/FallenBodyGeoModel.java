package dev.breach.client.downed;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import dev.breach.BreachMod;
import dev.breach.gameplay.downed.FallenBodyEntity;
import dev.breach.gameplay.downed.FallenBodyPhase;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;

public class FallenBodyGeoModel extends DefaultedEntityGeoModel<FallenBodyEntity> {
	public static final DataTicket<FallenBodySkinProfile> SKIN_PROFILE = DataTicket.create("skin_profile", FallenBodySkinProfile.class);
	public static final DataTicket<FallenBodyPhase> BODY_PHASE = DataTicket.create("body_phase", FallenBodyPhase.class);

	private static final Identifier WIDE_MODEL = BreachMod.id("entity/fallen_body_wide");
	private static final Identifier SLIM_MODEL = BreachMod.id("entity/fallen_body_slim");

	public FallenBodyGeoModel() {
		super(BreachMod.id("fallen_body_wide"));
	}

	@Override
	public Identifier getModelResource(GeoRenderState renderState) {
		FallenBodySkinProfile profile = renderState.getOrDefaultGeckolibData(
				SKIN_PROFILE,
				FallenBodySkinProfile.fallback(java.util.UUID.randomUUID())
		);
		return profile.modelType() == PlayerModelType.SLIM ? SLIM_MODEL : WIDE_MODEL;
	}

	@Override
	public Identifier getTextureResource(GeoRenderState renderState) {
		return renderState.getOrDefaultGeckolibData(
				SKIN_PROFILE,
				FallenBodySkinProfile.fallback(java.util.UUID.randomUUID())
		).texture();
	}
}
