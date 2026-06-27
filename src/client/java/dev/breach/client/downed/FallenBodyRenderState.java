package dev.breach.client.downed;

import dev.breach.gameplay.downed.FallenBodyEntity;
import dev.breach.gameplay.downed.FallenBodyPhase;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

import java.util.UUID;

public class FallenBodyRenderState extends HumanoidRenderState {
	public UUID ownerUuid = UUID.randomUUID();
	public String ownerName = "Unknown";
	public FallenBodyPhase bodyPhase = FallenBodyPhase.GROUND;
}
