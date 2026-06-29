package dev.breach.client.downed;

import dev.breach.gameplay.downed.FallenBodyPhase;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

public class FallenBodyModel extends HumanoidModel<FallenBodyRenderState> {
	public FallenBodyModel(ModelPart root) {
		super(root);
	}

	@Override
	public void setupAnim(FallenBodyRenderState state) {
		super.setupAnim(state);
		FallenBodyPose.apply(this, state.bodyPhase, state.ageInTicks);
	}
}
