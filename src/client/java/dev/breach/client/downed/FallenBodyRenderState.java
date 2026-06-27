package dev.breach.client.downed;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

import java.util.UUID;

public class FallenBodyRenderState extends HumanoidRenderState {
	public UUID ownerUuid = UUID.randomUUID();
	public boolean carried;
}
