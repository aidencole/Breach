package dev.breach.client.downed;

import com.mojang.math.Axis;
import dev.breach.gameplay.downed.DownedConstants;
import dev.breach.gameplay.downed.FallenBodyEntity;
import dev.breach.gameplay.downed.FallenBodyPhase;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.PoseStack;

public class FallenBodyRenderer extends HumanoidMobRenderer<FallenBodyEntity, FallenBodyRenderState, FallenBodyModel> {
	public FallenBodyRenderer(EntityRendererProvider.Context context) {
		super(context, new FallenBodyModel(context.bakeLayer(ModelLayers.PLAYER)), 0.375f);
	}

	@Override
	public FallenBodyRenderState createRenderState() {
		return new FallenBodyRenderState();
	}

	@Override
	public void extractRenderState(FallenBodyEntity entity, FallenBodyRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		state.ownerUuid = entity.getOwnerUuid();
		state.ownerName = entity.getOwnerName();
		state.bodyPhase = entity.getBodyPhase();
		state.ageInTicks = entity.tickCount + partialTick;
	}

	@Override
	public Identifier getTextureLocation(FallenBodyRenderState state) {
		return FallenBodySkinCache.resolve(state.ownerUuid, state.ownerName).texture();
	}

	@Override
	protected void setupRotations(FallenBodyRenderState state, PoseStack poseStack, float bodyRot, float partialTick) {
		if (state.bodyPhase == FallenBodyPhase.GROUND) {
			poseStack.mulPose(Axis.YP.rotationDegrees(180.0f - bodyRot));
			poseStack.translate(0.0, 0.02, 0.0);
			return;
		}
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0f - bodyRot));
		poseStack.translate(0.0, -0.55, 0.0);
	}

	@Override
	protected void scale(FallenBodyRenderState state, PoseStack poseStack) {
		float scale = DownedConstants.FALLEN_BODY_VANILLA_MODEL_SCALE;
		poseStack.scale(scale, scale, scale);
	}

	@Override
	protected int getModelTint(FallenBodyRenderState state) {
		if (state.bodyPhase == FallenBodyPhase.CARRIED) {
			return 0xFFFFFFFF;
		}
		return super.getModelTint(state);
	}

	@Override
	protected float getShadowRadius(FallenBodyRenderState state) {
		return state.bodyPhase == FallenBodyPhase.GROUND ? 0.25f : 0.05f;
	}
}
