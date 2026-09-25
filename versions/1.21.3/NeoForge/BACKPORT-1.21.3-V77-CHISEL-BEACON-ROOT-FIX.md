# Useful Ores 1.21.3 V78 — Chisel + Super Beacon Root Rendering Fix

## Scope
V78 starts from the V76 baseline because V75/V76 had already corrected the main item-model graph and V76 had confirmed the enlarged block-entity frustum bounds. This revision changes only the two remaining NeoForge rendering defects reported in runtime testing and mirrors the relevant common/resource fixes into Fabric.

## Scheelite Chisel
The 1.21.4 reference item definition selects `scheelite_chisel_icon` for GUI and `scheelite_chisel` for other contexts. The 1.21.3 backport already had the correct geometry and texture, but hand rendering remained invisible. V78 adds a dedicated `scheelite_chisel_hand.json` compatibility model: identical authored elements/texture, standard `item/generated` parent, numeric texture slot normalized from `#2` to `#0`, and explicit 1.21.4 handheld transforms. The model is used only for first/third-person hand contexts. GUI continues to use the existing icon; ground/fixed retain the canonical 3D model.

The reason for the dedicated hand model is to remove two fragile inheritance variables from the old 1.21.3 path at once: Blockbench texture-slot naming and parent-provided hand transforms. No geometry was redesigned.

## Super Beacon
The previous implementation used `Heightmap.Types.WORLD_SURFACE` as the upper scan bound. That is the terrain surface, not the world build ceiling. A beacon placed at the surface therefore scanned only its own block/roughly one block above it, yielding the reported beam truncation. V78 changes the scan ceiling to `level.getMaxBuildHeight() - 1`, matching the requirement that the beam continue through unobstructed sky.

The renderer now also derives its relative maximum beam height from `level.getMaxBuildHeight() - beaconY`, rather than an arbitrary fixed 2048 ceiling. The V76 enlarged render bounding box behavior is retained, with the upper bound made dynamic to the actual world build ceiling.

## Research basis
- The supplied 1.21.4 `handheld.json` uses the standard first/third-person transforms; V78 makes those transforms explicit in the 1.21.3 compatibility model.
- Fabric API 1.21 model loading exposes `ModelLoadingPlugin.Context.addModels(Identifier...)`, with extra models retrieved through the baked-model manager; this is the API shape used by the V76 Fabric baseline.
- Minecraft 1.21.3 `BeaconRenderer` exposes `renderBeaconBeam(..., yOffset, maxY, ...)`, and both 1.21.3 and 1.21.4 retain the same beam renderer concept. The backport defect was the custom scan limit, not the existence of the vanilla beam primitive.

## Validation performed
- JSON parse validation for all resource JSON files.
- Checked that `scheelite_chisel_hand.json` contains no unresolved `#2` texture references.
- Confirmed no `WORLD_SURFACE` reference remains in `SuperBeaconBlockEntity`.
- Confirmed V76 culling hooks remain present.
- Confirmed V75 item-model graph files remain otherwise unchanged.
- Archive integrity checked after packaging.

## Runtime status
Windows NeoForge/Fabric runtime launch is not available in the build environment; final runtime confirmation must be performed against the user's 1.21.3 instances.
