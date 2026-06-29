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
		this.hat.visible = overlayState.showHat;
		this.jacket.visible = overlayState.showJacket;
		this.leftPants.visible = overlayState.showLeftPants;
		this.rightPants.visible = overlayState.showRightPants;
		this.leftSleeve.visible = overlayState.showLeftSleeve;
		this.rightSleeve.visible = overlayState.showRightSleeve;
		hideBaseLayer();
		FallenBodyPose.apply(this, overlayState.bodyPhase, overlayState.ageInTicks);
	}

	private void hideBaseLayer() {
		this.head.skipDraw = true;
		this.body.skipDraw = true;
		this.leftArm.skipDraw = true;
		this.rightArm.skipDraw = true;
		this.leftLeg.skipDraw = true;
		this.rightLeg.skipDraw = true;
		this.hat.skipDraw = false;
		this.jacket.skipDraw = false;
		this.leftSleeve.skipDraw = false;
		this.rightSleeve.skipDraw = false;
		this.leftPants.skipDraw = false;
		this.rightPants.skipDraw = false;
	}
}
