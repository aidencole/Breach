package dev.breach.client.downed;

import java.util.Set;

public final class FallenBodyLayerBones {
	private static final Set<String> OUTER_LAYER_BONES = Set.of(
			"hat",
			"jacket",
			"left_sleeve",
			"right_sleeve",
			"left_pants",
			"right_pants",
			"head_outer",
			"body_outer",
			"left_arm_outer",
			"right_arm_outer",
			"left_leg_outer",
			"right_leg_outer",
			"outer_head",
			"outer_body",
			"outer_left_arm",
			"outer_right_arm",
			"outer_left_leg",
			"outer_right_leg"
	);

	private FallenBodyLayerBones() {
	}

	public static boolean isOuterLayer(String boneName) {
		if (boneName == null || boneName.isEmpty()) {
			return false;
		}
		if (OUTER_LAYER_BONES.contains(boneName)) {
			return true;
		}
		return boneName.startsWith("outer_") || boneName.endsWith("_outer");
	}
}
