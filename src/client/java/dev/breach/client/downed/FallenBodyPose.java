package dev.breach.client.downed;

import dev.breach.gameplay.downed.FallenBodyPhase;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;

public final class FallenBodyPose {
	private FallenBodyPose() {
	}

	public static void apply(HumanoidModel<?> model, FallenBodyPhase phase, float ageInTicks) {
		resetLimbs(model);
		if (phase == FallenBodyPhase.CARRIED) {
			applyCarriedPose(model, ageInTicks);
		} else {
			applyPronePose(model, ageInTicks);
		}
	}

	private static void resetLimbs(HumanoidModel<?> model) {
		model.head.xRot = 0.0f;
		model.head.yRot = 0.0f;
		model.head.zRot = 0.0f;
		model.body.xRot = 0.0f;
		model.body.yRot = 0.0f;
		model.body.zRot = 0.0f;
		model.leftArm.xRot = 0.0f;
		model.leftArm.yRot = 0.0f;
		model.leftArm.zRot = 0.0f;
		model.rightArm.xRot = 0.0f;
		model.rightArm.yRot = 0.0f;
		model.rightArm.zRot = 0.0f;
		model.leftLeg.xRot = 0.0f;
		model.leftLeg.yRot = 0.0f;
		model.leftLeg.zRot = 0.0f;
		model.rightLeg.xRot = 0.0f;
		model.rightLeg.yRot = 0.0f;
		model.rightLeg.zRot = 0.0f;
	}

	private static void applyPronePose(HumanoidModel<?> model, float ageInTicks) {
		float breath = Mth.sin(ageInTicks * 0.08f) * 0.03f;
		model.body.xRot = (float) Math.PI / 2.0f + breath;
		model.head.xRot = -0.25f;
		model.head.yRot = 0.0f;
		model.leftArm.xRot = -0.15f;
		model.leftArm.zRot = -0.55f;
		model.rightArm.xRot = -0.15f;
		model.rightArm.zRot = 0.55f;
		model.leftLeg.xRot = 0.05f;
		model.rightLeg.xRot = 0.05f;
		model.leftLeg.zRot = -0.08f;
		model.rightLeg.zRot = 0.08f;
	}

	private static void applyCarriedPose(HumanoidModel<?> model, float ageInTicks) {
		float sway = Mth.sin(ageInTicks * 0.12f) * 0.04f;
		model.body.xRot = 0.35f + sway;
		model.body.zRot = sway * 0.5f;
		model.head.xRot = -0.45f;
		model.leftArm.xRot = 0.55f;
		model.rightArm.xRot = 0.55f;
		model.leftArm.zRot = -0.15f;
		model.rightArm.zRot = 0.15f;
		model.leftLeg.xRot = 0.65f;
		model.rightLeg.xRot = 0.65f;
	}
}
