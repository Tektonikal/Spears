package tektonikal.spears;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagManagerLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class SpearItem extends ToolItem {
    public Vec3d prevPos;

    public SpearItem(Settings settings, ToolMaterial material) {
        super(material, settings.component(DataComponentTypes.TOOL, createToolComponent()));
    }

    public float chargedTicks = 0;

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        prevPos = user.getPos();
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        ((PlayerEntity) user).getItemCooldownManager().set(this, 15);
        chargedTicks = 0;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ((PlayerEntity) user).getItemCooldownManager().set(this, 15);
        chargedTicks = 0;
        return super.finishUsing(stack, world, user);
    }

    public static AttributeModifiersComponent createAttributeModifiers(ToolMaterial material) {
        return AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, material.getAttackDamage(), EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -3.3F, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                )
                .build();
    }

    public static ToolComponent createToolComponent() {
        return new ToolComponent(List.of(), 1.0F, 2);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, EquipmentSlot.MAINHAND);
    }

//    @Override
//    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
//
//        return true;
//    }


    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 100;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClient) {
            chargedTicks = getMaxUseTime(stack, user) - remainingUseTicks;
            if (user.getPos().subtract(prevPos).horizontalLengthSquared() >= 0.25 && chargedTicks >= 10) {
                world.getOtherEntities(user, new Box(user.raycast(3, 1, true).getPos(), user.raycast(3, 1, true).getPos()).expand(1)).forEach(entity -> {
                    if(user.hasVehicle() && user.getVehicle().equals(entity)) {
                        return;
                    }
                    entity.damage(user.getWorld().getDamageSources().trident(user, user), (float) (this.getMaterial().getAttackDamage() + (user.getPos().subtract(prevPos).horizontalLengthSquared() * 2 * (2 + this.getMaterial().getAttackDamage()))));
                if (chargedTicks <= 50) {
                    if(entity instanceof LivingEntity) {
                    ((LivingEntity) entity).takeKnockback(0.25, MathHelper.sin(user.getYaw() * (float) (Math.PI / 180.0)), -MathHelper.cos(user.getYaw() * (float) (Math.PI / 180.0)));
                    }
                }
                });
            }
            prevPos = user.getPos();
        }
    }
}
