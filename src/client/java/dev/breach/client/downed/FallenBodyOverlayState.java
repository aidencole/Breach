package dev.breach.client.downed;

import dev.breach.gameplay.downed.FallenBodyPhase;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

public class FallenBodyOverlayState extends AvatarRenderState {
	public FallenBodyPhase bodyPhase = FallenBodyPhase.GROUND;

	public static FallenBodyOverlayState forPhase(FallenBodyPhase phase, float ageInTicks) {
		FallenBodyOverlayState state = new FallenBodyOverlayState();
		state.bodyPhase = phase;
		state.ageInTicks = ageInTicks;
		state.showHat = true;
		state.showJacket = true;
		state.showLeftPants = true;
		state.showRightPants = true;
		state.showLeftSleeve = true;
		state.showRightSleeve = true;
		return state;
	}
}
