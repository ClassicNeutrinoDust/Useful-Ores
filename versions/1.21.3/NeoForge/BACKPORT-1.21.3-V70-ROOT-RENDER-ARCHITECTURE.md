# Useful Ores 1.21.3 V70 — root rendering architecture

V70 removes the brittle ItemRenderer contextual-model Mixin entirely.

NeoForge uses the native `Item#initializeClient` → singleton `IClientItemExtensions` → `BlockEntityWithoutLevelRenderer#renderByItem` path. Fabric uses `BuiltinItemRendererRegistry.DynamicItemRenderer`. Both select a baked model from one contextual selector.

Spear roots, Scheelite root, and Arcanite XP Jar root are now `minecraft:builtin/entity` carriers. Real inventory/in-hand/3D models are separate assets and are explicitly registered/baked. Additional model registration and lookup use the same `ModelResourceLocation.inventory(...)` identity.

Staffs stay on GeckoLib for hand contexts and route GUI/GROUND/FIXED to the exact 1.21.4 flat models.

Fabric `MixinFireBlock` is loader-specific: its 1.21.3 runtime signature has no age/Direction, as established by the supplied Fabric crash. NeoForge retains its six-argument signature.
