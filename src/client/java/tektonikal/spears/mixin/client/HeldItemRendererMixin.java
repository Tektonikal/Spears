package tektonikal.spears.mixin.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tektonikal.spears.Items;
import tektonikal.spears.SpearItem;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
	@Shadow public abstract void renderItem(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light);

	@Shadow protected abstract void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress);

	@Shadow public abstract void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);

	@Inject(at = @At("HEAD"), method = "renderFirstPersonItem", cancellable = true)
	private void init(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if(player.isUsingItem() && player.getActiveItem().isOf(Items.IRON_SPEAR)){
			boolean bl = hand == Hand.MAIN_HAND;
			boolean bl2 = CrossbowItem.isCharged(item);
			int l = bl2 ? 1 : -1;
			Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
			matrices.push();
			int i = arm == Arm.RIGHT ? 1 : -1;
			matrices.translate(i * 0.56F, -0.52F + 0 * -0.6F, -0.72F);
			matrices.translate(0, 0.35F, 0.1F);
//			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(25.0F));
//			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(l * 35.3F));
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(l * -90));
			float m = item.getMaxUseTime(player) - (player.getItemUseTimeLeft() - tickDelta + 1.0F);
			float fx = m / 10.0F;
			if (fx > 1.0F) {
				fx = 1.0F;
			}

//			if (fx > 0.1F) {
//				float gx = MathHelper.sin((m - 0.1F) * 1.3F);
//				float h = fx - 0.1F;
//				float j = gx * h;
//				matrices.translate(j * 0.0F, 0, j * 0.0F);
//			}
//			matrices.translate(0.0F, 0.0F, fx * 0.2F);
			matrices.scale(1.0F, 1.0F, 1.0F + fx * 0.2F);
//			matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(l * 45.0F));
			this.renderItem(
					player,
					item,
					bl2 ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
					!bl2,
					matrices,
					vertexConsumers,
					light
			);
			matrices.pop();
			ci.cancel();
		}
	}
}