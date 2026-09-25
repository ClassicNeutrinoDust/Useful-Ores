# Useful Ores 1.21.8 / 1.21.7 Backport — V6

## Fixes
- Removed `on_shelf` from all Useful Ores item-model selectors. Minecraft 1.21.8/1.21.7 do not define `ItemDisplayContext.ON_SHELF`; it is introduced in 1.21.9. This was the direct cause of standard Useful Ores spear item definitions resolving to the missing model while the Painite definition continued to load.
- Kept the actual spear PNG files unchanged and verified all spear item-model references resolve to existing models/textures.
- Fixed custom BlockEntityRenderer vertices to use the active PoseStack pose and pose-aware normals. This keeps Solarite Furnace geometry at its block position instead of rendering at the camera/origin.
- Applied the same correction to Arcanite XP orb billboards so the XP orbs stay inside the jar.
- Applied the same correction to shared relay/clock glow geometry to prevent the same latent camera-origin rendering bug.
- Added the missing Fabric `MultiBufferSource` import that caused the reported 1-error Fabric build failure.

## Static checks
- 0 JSON parse failures.
- 0 remaining `on_shelf` references in Useful Ores item definitions.
- 0 unresolved spear model/texture references.
- 0 known 1.21.9-only render/input API symbols remaining in Java source.
- 732 media assets in each 1.21.10 source tree compared byte-for-byte against the backport: 0 changed/missing media files.
