package com.neutrinodust.useful_ores.item;

import com.neutrinodust.useful_ores.entity.NyxiumniteCubeProjectile;
import com.neutrinodust.useful_ores.client.NyxiumniteStaffItemRenderer;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.GeckoLibUtil;
import com.neutrinodust.useful_ores.init.ModEntities;
import com.neutrinodust.useful_ores.pedestal.AncientPedestalBlock;
import com.neutrinodust.useful_ores.pedestal.ModPedestalComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class NyxiumniteStaffItem extends Item implements GeoItem {
   private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

   private static final int COOLDOWN_TICKS = 30;

   public NyxiumniteStaffItem(Properties props) {
      super(props);
   }

   @Override
   public InteractionResult useOn(UseOnContext context) {
      Level level = context.getLevel();
      if (context.getPlayer() == null) return InteractionResult.PASS;
      if (!(level.getBlockState(context.getClickedPos()).getBlock() instanceof AncientPedestalBlock)) {
         return super.useOn(context);
      }

      ItemStack stack = context.getItemInHand();
      if (!level.isClientSide()) {
         GlobalPos linked = GlobalPos.of(level.dimension(), context.getClickedPos());
         stack.set(ModPedestalComponents.LINKED_PEDESTAL, linked);
         context.getPlayer().sendSystemMessage(
               Component.translatable("message.useful_ores.nyxiumnite_staff.linked").withStyle(ChatFormatting.LIGHT_PURPLE));
         level.playSound(null, context.getClickedPos(), SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 0.6F, 1.4F);
      }
      return InteractionResult.SUCCESS;
   }

   @Override
   public InteractionResult use(Level level, net.minecraft.world.entity.player.Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);

      if (player.getCooldowns().isOnCooldown(stack)) {
         return InteractionResult.FAIL;
      }

      GlobalPos linked = stack.get(ModPedestalComponents.LINKED_PEDESTAL);
      if (linked == null) {
         if (!level.isClientSide()) {
            player.sendSystemMessage(
                  Component.translatable("message.useful_ores.nyxiumnite_staff.unlinked").withStyle(ChatFormatting.RED));
         }
         return InteractionResult.FAIL;
      }

      if (!level.isClientSide() && !pedestalStillExists(level, linked)) {

         stack.remove(ModPedestalComponents.LINKED_PEDESTAL);
         player.sendSystemMessage(
               Component.translatable("message.useful_ores.nyxiumnite_staff.pedestal_gone").withStyle(ChatFormatting.RED));
         return InteractionResult.FAIL;
      }

      if (!level.isClientSide()) {
         NyxiumniteCubeProjectile projectile = new NyxiumniteCubeProjectile(
               level, player, ModEntities.NYXIUMNITE_CUBE_PROJECTILE, linked);
         projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.6F, 0.3F);
         level.addFreshEntity(projectile);
      }

      player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);
      level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 0.7F);

      return level.isClientSide() ? InteractionResult.CONSUME : InteractionResult.SUCCESS;
   }

   private static boolean pedestalStillExists(Level level, GlobalPos linked) {
      if (!(level instanceof net.minecraft.server.level.ServerLevel serverLevel)) return true;
      net.minecraft.server.level.ServerLevel targetLevel =
            serverLevel.getServer().getLevel(linked.dimension());
      if (targetLevel == null) return false;
      return targetLevel.getBlockState(linked.pos()).getBlock() instanceof AncientPedestalBlock;
   }

   @Override
   public void createGeoRenderer(java.util.function.Consumer<GeoRenderProvider> consumer) {
      consumer.accept(new GeoRenderProvider() {
         private GeoItemRenderer<NyxiumniteStaffItem> renderer;

         @Override
         public GeoItemRenderer<NyxiumniteStaffItem> getGeoItemRenderer() {
            if (this.renderer == null) this.renderer = new NyxiumniteStaffItemRenderer();
            return this.renderer;
         }
      });
   }

   @Override
   public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

   }

   @Override
   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.animatableInstanceCache;
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
      GlobalPos linked = stack.get(ModPedestalComponents.LINKED_PEDESTAL);
      if (linked != null) {
         tooltip.accept(Component.translatable("item.useful_ores.nyxiumnite_staff.linked_tooltip",
               linked.pos().getX(), linked.pos().getY(), linked.pos().getZ()).withStyle(ChatFormatting.DARK_PURPLE));
      } else {
         tooltip.accept(Component.translatable("item.useful_ores.nyxiumnite_staff.unlinked_tooltip").withStyle(ChatFormatting.GRAY));
      }
   }
}

