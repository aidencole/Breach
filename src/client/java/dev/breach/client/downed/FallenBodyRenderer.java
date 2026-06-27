package dev.breach.client.downed;

import com.mojang.math.Axis;
import dev.breach.gameplay.downed.FallenBodyEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Pose;
import com.mojang.blaze3d.vertex.PoseStack;

public class FallenBodyRenderer extends HumanoidMobRenderer<FallenBodyEntity, FallenBodyRenderState, HumanoidModel<FallenBodyRenderState>> {
	public FallenBodyRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.375f);
	}

	@Override
	public FallenBodyRenderState createRenderState() {
		return new FallenBodyRenderState();
	}

	@Override
	public void extractRenderState(FallenBodyEntity entity, FallenBodyRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		state.ownerUuid = entity.getOwnerUuid();
		state.carried = entity.getCarrierUuid().isPresent();
		state.pose = Pose.SLEEPING;
		state.bedOrientation = Direction.fromYRot(entity.getYRot());
	}

	@Override
	public Identifier getTextureLocation(FallenBodyRenderState state) {
		return DefaultPlayerSkin.get(state.ownerUuid).body().texturePath();
	}

	@Override
	protected void setupRotations(FallenBodyRenderState state, PoseStack poseStack, float bodyRot, float partialTick) {
		super.setupRotations(state, poseStack, bodyRot, partialTick);
		if (!state.carried) {
			poseStack.translate(0.0, 0.35, 0.0);
			poseStack.mulPose(Axis.XP.rotationDegrees(90.0f));
		}
	}
}
