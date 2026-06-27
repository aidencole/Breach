package dev.breach.client.mixin;

import dev.breach.client.carry.CarryRenderKeys;
import dev.breach.gameplay.carry.CarryAttachment;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Avatar;
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

	@Shadow
	public net.minecraft.client.model.geom.ModelPart body;

	@Shadow
	public net.minecraft.client.model.geom.ModelPart head;

	@Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At("TAIL"))
	private void breach$raiseArmsForCarry(T state, CallbackInfo ci) {
		if (state instanceof FabricRenderState fabric && fabric.getDataOrDefault(CarryRenderKeys.CARRYING, false)) {
			this.leftArm.xRot = -2.75f;
			this.rightArm.xRot = -2.75f;
			this.leftArm.yRot = -0.12f;
			this.rightArm.yRot = 0.12f;
			this.leftArm.zRot = -0.08f;
			this.rightArm.zRot = 0.08f;
			this.body.xRot = -0.08f;
			this.head.xRot = 0.12f;
		}
	}
}
