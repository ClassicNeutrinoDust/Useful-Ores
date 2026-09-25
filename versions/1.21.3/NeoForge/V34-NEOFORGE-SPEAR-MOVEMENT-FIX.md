# V35 — NeoForge 1.21.4 spear movement fix

Fabric V33 already has working spear movement and is intentionally unchanged.

For NeoForge 1.21.4, the generic held-item slowdown is known from the actual
1.21.4 `LocalPlayer.aiStep` bytecode: `ClientInput.forwardImpulse` and
`ClientInput.leftImpulse` are each multiplied by `0.2F` while the player is
using an item.

The V33 NeoForge `@ModifyConstant` hook was not reliably taking effect after
NeoForge's transformed-client pipeline, so V35 replaces only that NeoForge
hook with a deterministic `aiStep` HEAD hook. When the active main-hand item is
a Useful Ores spear, both input impulses are multiplied by `5.0F` before vanilla
reaches its `0.2F` held-item factor. The resulting movement input is therefore
unchanged (`5.0F * 0.2F = 1.0F`), while all ordinary 1.21.4 movement, collision,
friction and sprint logic remains vanilla.

The existing `canStartSprinting()` correction is retained.
