package dev.breach.client.downed;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

public class FallenBodyOverlayModel extends PlayerModel {
	public FallenBodyOverlayModel(ModelPart root, boolean slim) {
		super(root, slim);
	}

	@Override
	public void setupAnim(AvatarRenderState state) {
		if (!(state instanceof FallenBodyOverlayState overlayState)) {
			super.setupAnim(state);
			return;
		}

		this.resetPose();
		this.hat.visible = true;
		this.jacket.visible = true;
		this.leftPants.visible = true;
		this.rightPants.visible = true;
		this.leftSleeve.visible = true;
		this.rightSleeve.visible = true;
		FallenBodyPose.apply(this, overlayState.bodyPhase, overlayState.ageInTicks);
		inflateOverlayParts();
	}

	private void inflateOverlayParts() {
		float scale = 1.02f;
		this.hat.xScale = this.hat.yScale = this.hat.zScale = scale;
		this.jacket.xScale = this.jacket.yScale = this.jacket.zScale = scale;
		this.leftSleeve.xScale = this.leftSleeve.yScale = this.leftSleeve.zScale = scale;
		this.rightSleeve.xScale = this.rightSleeve.yScale = this.rightSleeve.zScale = scale;
		this.leftPants.xScale = this.leftPants.yScale = this.leftPants.zScale = scale;
		this.rightPants.xScale = this.rightPants.yScale = this.rightPants.zScale = scale;
	}
}
