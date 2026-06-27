package dev.breach.gameplay.downed;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class CarryAnchor {
	private static final double FORWARD_OFFSET = 0.15;
	private static final double VERTICAL_OFFSET = 0.05;

	private CarryAnchor() {
	}

	public static Vec3 worldPosition(Player carrier) {
		float yaw = carrier.getYRot() * Mth.DEG_TO_RAD;
		double up = carrier.getBbHeight() + 0.55;
		double forwardX = -Mth.sin(yaw) * FORWARD_OFFSET;
		double forwardZ = Mth.cos(yaw) * FORWARD_OFFSET;
		return new Vec3(
				carrier.getX() + forwardX,
				carrier.getY() + up + VERTICAL_OFFSET,
				carrier.getZ() + forwardZ
		);
	}
}
