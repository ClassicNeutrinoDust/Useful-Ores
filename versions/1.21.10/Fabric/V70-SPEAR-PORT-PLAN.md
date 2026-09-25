# Useful Ores Fabric 1.21.10 — V70 Spear Compatibility Pass

This pass ports the missing 1.21.11 render-state infrastructure rather than changing animation constants.

## Changes
- Capture raw main/off-hand ItemStacks into the 1.21.10 ArmedEntityRenderState using a dedicated mixin state extension.
- Keep ItemInHandLayer on its real 1.21.10 method signature; do not use the 1.21.11 extra ItemStack parameter.
- Apply the third-person spear item transform immediately before ItemStackRenderState submission.
- Feed the same captured raw stacks into the HumanoidModel spear arm-pose compatibility layer.
- Fix the client initializer to call the actual SpearVisualState.initialize() method.
- Keep the dedicated first-person renderer branch and left-click spear visual trigger.

## Compatibility contract checked
- Minecraft 1.21.10 ItemInHandLayer: method_4192 does NOT take a raw ItemStack.
- Minecraft 1.21.10 ArmedEntityRenderState: raw hand stacks are backported as extension state.
- Minecraft 1.21.10 Minecraft.startAttack(): returns boolean, therefore CallbackInfoReturnable<Boolean>.
- No 1.21.11-only raw ItemStack argument is injected into method_4192.
