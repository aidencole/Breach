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
		this.body.visible = true;
		this.head.visible = true;
		this.leftArm.visible = true;
		this.rightArm.visible = true;
		this.leftLeg.visible = true;
		this.rightLeg.visible = true;
		this.hat.visible = true;
		this.jacket.visible = true;
		this.leftPants.visible = true;
		this.rightPants.visible = true;
		this.leftSleeve.visible = true;
		this.rightSleeve.visible = true;
		for (ModelPart part : this.allParts()) {
			part.skipDraw = false;
		}
		this.head.skipDraw = true;
		this.body.skipDraw = true;
		this.leftArm.skipDraw = true;
		this.rightArm.skipDraw = true;
		this.leftLeg.skipDraw = true;
		this.rightLeg.skipDraw = true;
		FallenBodyPose.apply(this, overlayState.bodyPhase, overlayState.ageInTicks);
	}
}
