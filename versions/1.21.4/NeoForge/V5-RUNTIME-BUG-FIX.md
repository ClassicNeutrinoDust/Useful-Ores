# Useful Ores 1.21.8/1.21.7 Backport — V5 Runtime Bug Fixes

Fixes in this revision:

- Removed `on_shelf` from item-model selectors. `ItemDisplayContext.ON_SHELF` is a 1.21.9 addition; its presence made the standard spear item-model definitions invalid on 1.21.8/1.21.7.
- Fixed custom BlockEntityRenderer vertex emission to use the active PoseStack pose. Previously raw `addVertex(x,y,z)` emitted the geometry at camera/world origin, which caused the Solarite Furnace geometry and Arcanite XP orb to appear detached/floating at the player camera.
- Applied pose-aware normals to custom BE geometry.
- Applied the same transform correction to the shared glow-cube renderer used by powered relay/clock glow effects.
- Added the missing Fabric `MultiBufferSource` import in the Phosgene overlay renderer.
- Removed unsupported `on_shelf` values across the backported item resources.

Media assets are not regenerated or recolored in this revision.
