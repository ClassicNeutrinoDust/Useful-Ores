# Useful Ores 1.21.4 Backport V32

## Runtime fix
- Removed the invalid 1.21.4 Fabric/NeoForge `LocalPlayer.modifyInput` injection.
- Replaced it with a 1.21.4-compatible `LocalPlayer.aiStep` `@ModifyConstant` for the generic 0.2 use-item speed factor.
- The modifier activates only while a Useful Ores spear is actively being used.
- Kept the spear sprint gate as a separate injection.
- This fixes the V31 startup crash caused by the nonexistent `modifyInput(Vec2)` method.
