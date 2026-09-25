package com.neutrinodust.useful_ores.item;

import com.neutrinodust.useful_ores.client.MeteorStaffItemRenderer;
import com.neutrinodust.useful_ores.entity.MeteoriteProjectile;
import com.neutrinodust.useful_ores.init.ModEntities;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class MeteorStaffItem extends Item implements GeoItem {

   public static final int COOLDOWN_TICKS = 100;

   private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

   public MeteorStaffItem(Properties props) {
      super(props);
   }

   @Override
   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      consumer.accept(new GeoRenderProvider() {
         private GeoItemRenderer<MeteorStaffItem> renderer;

         @Override
         public GeoItemRenderer<MeteorStaffItem> getGeoItemRenderer() {
            if (this.renderer == null) {
               this.renderer = new MeteorStaffItemRenderer();
            }
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
   public InteractionResult use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);

      if (player.getCooldowns().isOnCooldown(stack)) {
         return InteractionResult.FAIL;
      }

      if (!level.isClientSide()) {
         MeteoriteProjectile projectile = new MeteoriteProjectile(level, player, ModEntities.METEORITE_PROJECTILE);
         projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.8F, 0.5F);
         level.addFreshEntity(projectile);

         EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
         stack.hurtAndBreak(1, player, slot);
      }

      player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);
      level.playSound(null, player.getX(), player.getY(), player.getZ(),
         SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, 0.8F);

      return level.isClientSide() ? InteractionResult.CONSUME : InteractionResult.SUCCESS;
   }
}

