package dev.breach.client.mixin;

import dev.breach.client.carry.CarryRenderKeys;
import dev.breach.gameplay.carry.CarryAttachment;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {
	@Inject(
			method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
			at = @At("TAIL")
	)
	private void breach$applyCarryState(Avatar entity, AvatarRenderState state, float partialTick, CallbackInfo ci) {
		if (CarryAttachment.isCarrying(entity)) {
			state.setData(CarryRenderKeys.CARRYING, true);
		}
	}
}
