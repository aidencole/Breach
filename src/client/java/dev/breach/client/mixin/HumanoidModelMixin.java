package dev.breach.client.mixin;

import dev.breach.client.carry.CarryRenderKeys;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends HumanoidRenderState> {
	@Shadow
	public net.minecraft.client.model.geom.ModelPart leftArm;

	@Shadow
	public net.minecraft.client.model.geom.ModelPart rightArm;

	@Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At("TAIL"))
	private void breach$raiseArmsForCarry(T state, CallbackInfo ci) {
		if (state instanceof FabricRenderState fabric && fabric.getDataOrDefault(CarryRenderKeys.CARRYING, false)) {
			this.leftArm.xRot = -2.65f;
			this.rightArm.xRot = -2.65f;
			this.leftArm.yRot = 0.0f;
			this.rightArm.yRot = 0.0f;
			this.leftArm.zRot = -0.15f;
			this.rightArm.zRot = 0.15f;
		}
	}
}
