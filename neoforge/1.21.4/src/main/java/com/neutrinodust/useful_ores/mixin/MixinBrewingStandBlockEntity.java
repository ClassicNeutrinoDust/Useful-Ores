package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.brewing.SperryliteCatalyticVialBrewing;
import com.neutrinodust.useful_ores.brewing.SperryliteVialFuel;
import com.neutrinodust.useful_ores.brewing.VialConversionBrewing;
import com.neutrinodust.useful_ores.init.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandBlockEntity.class)
public class MixinBrewingStandBlockEntity {

    @Shadow private int brewTime;
    @Shadow private int fuel;

    @Inject(method = "serverTick", at = @At("HEAD"), cancellable = true)
    private static void usefulOres$catalyticVialBrew(
        Level level, BlockPos pos, BlockState state, BrewingStandBlockEntity blockEntity, CallbackInfo ci
    ) {
        MixinBrewingStandBlockEntity self = (MixinBrewingStandBlockEntity) (Object) blockEntity;

        
        
        if (SperryliteCatalyticVialBrewing.matchesItems(blockEntity)
                && SperryliteVialFuel.charges(blockEntity) <= 0) {
            if (self.fuel > 0) SperryliteVialFuel.loadCharges(blockEntity, self.fuel);
            else SperryliteVialFuel.primeCharge(blockEntity);
        }

        if (SperryliteCatalyticVialBrewing.tick(level, blockEntity)) {
            int remaining = SperryliteCatalyticVialBrewing.remainingTicks(blockEntity);

            self.brewTime = Math.max(0, remaining);
            self.fuel = Math.max(0, SperryliteCatalyticVialBrewing.fuelCharges(blockEntity));
            blockEntity.setChanged();

            level.sendBlockUpdated(pos, state, state, 3);
            ci.cancel();
            return;
        }

        if (VialConversionBrewing.matchesRecipe(blockEntity)
                && SperryliteVialFuel.charges(blockEntity) <= 0) {
            if (self.fuel > 0) SperryliteVialFuel.loadCharges(blockEntity, self.fuel);
            else SperryliteVialFuel.primeCharge(blockEntity);
        }

        if (VialConversionBrewing.tick(level, blockEntity)) {
            int remaining = VialConversionBrewing.remainingTicks(blockEntity);
            self.brewTime = Math.max(0, remaining);
            self.fuel = Math.max(0, VialConversionBrewing.fuelCharges(blockEntity));
            blockEntity.setChanged();

            level.sendBlockUpdated(pos, state, state, 3);
            ci.cancel();
        }
    }

    @Inject(method = "canPlaceItem", at = @At("HEAD"), cancellable = true)
    private void usefulOres$allowVialAndNugget(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Container self = (Container) (Object) this;
        if (slot >= 0 && slot <= 2) {
            if ((stack.is(ModItems.SPERRYLITE_CATALYTIC_VIAL.get())
                    || stack.is(ModItems.SPERRYLITE_CATALYTIC_VIAL_SPLASH.get())
                    || stack.is(ModItems.SPERRYLITE_CATALYTIC_VIAL_LINGERING.get()))
                    && stack.getCount() == 1
                    && self.getItem(slot).isEmpty()) {
                cir.setReturnValue(true);
            }
        } else if (slot == 3) {
            if (stack.is(ModItems.SPERRYLITE_ITEMS.get(2).get())) {
                cir.setReturnValue(true);
            }
        }
    }
}

