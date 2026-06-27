package dev.breach.client.downed;

import dev.breach.gameplay.downed.FallenBodyPhase;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class FallenBodyModel extends HumanoidModel<FallenBodyRenderState> {
	public FallenBodyModel(ModelPart root) {
		super(root);
	}

	@Override
	public void setupAnim(FallenBodyRenderState state) {
		super.setupAnim(state);
		resetLimbs();
		if (state.bodyPhase == FallenBodyPhase.CARRIED) {
			applyCarriedPose(state);
		} else {
			applyPronePose(state);
		}
	}

	private void resetLimbs() {
		this.head.xRot = 0.0f;
		this.head.yRot = 0.0f;
		this.head.zRot = 0.0f;
		this.body.xRot = 0.0f;
		this.body.yRot = 0.0f;
		this.body.zRot = 0.0f;
		this.leftArm.xRot = 0.0f;
		this.leftArm.yRot = 0.0f;
		this.leftArm.zRot = 0.0f;
		this.rightArm.xRot = 0.0f;
		this.rightArm.yRot = 0.0f;
		this.rightArm.zRot = 0.0f;
		this.leftLeg.xRot = 0.0f;
		this.leftLeg.yRot = 0.0f;
		this.leftLeg.zRot = 0.0f;
		this.rightLeg.xRot = 0.0f;
		this.rightLeg.yRot = 0.0f;
		this.rightLeg.zRot = 0.0f;
	}

	private void applyPronePose(FallenBodyRenderState state) {
		float breath = Mth.sin(state.ageInTicks * 0.08f) * 0.03f;
		this.body.xRot = (float) Math.PI / 2.0f + breath;
		this.head.xRot = -0.25f;
		this.head.yRot = 0.0f;
		this.leftArm.xRot = -0.15f;
		this.leftArm.zRot = -0.55f;
		this.rightArm.xRot = -0.15f;
		this.rightArm.zRot = 0.55f;
		this.leftLeg.xRot = 0.05f;
		this.rightLeg.xRot = 0.05f;
		this.leftLeg.zRot = -0.08f;
		this.rightLeg.zRot = 0.08f;
	}

	private void applyCarriedPose(FallenBodyRenderState state) {
		float sway = Mth.sin(state.ageInTicks * 0.12f) * 0.04f;
		this.body.xRot = 0.35f + sway;
		this.body.zRot = sway * 0.5f;
		this.head.xRot = -0.45f;
		this.leftArm.xRot = 0.55f;
		this.rightArm.xRot = 0.55f;
		this.leftArm.zRot = -0.15f;
		this.rightArm.zRot = 0.15f;
		this.leftLeg.xRot = 0.65f;
		this.rightLeg.xRot = 0.65f;
	}
}
