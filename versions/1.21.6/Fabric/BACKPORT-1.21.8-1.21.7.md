# Useful Ores 2.1.7 — Minecraft 1.21.8 / 1.21.7 Backport

Target: Minecraft 1.21.8 and 1.21.7, Java 21.

## Porting scope

This source tree was reverse-migrated from the supplied 1.21.10 project while preserving the existing gameplay/resource implementation. Version-sensitive work was limited to:

- Fabric/Minecraft dependency targets.
- GeckoLib dependency moved from the supplied newer local build to GeckoLib 5.2.1, which is the common 1.21.7–1.21.8 line.
- 1.21.9+ rendering submission/state bridge code removed and replaced with the classic 1.21.8 render APIs.
- Projectile block rendering restored to `BlockRenderDispatcher.renderSingleBlock`.
- Minecart texture redirect restored to the classic `AbstractMinecartRenderer.render` path.
- Spear first-person/third-person rendering restored to the 1.21.8 classic item-render path while retaining the supplied spear animation/combat logic.
- Painite armor animation redirect restored to the classic `EquipmentLayerRenderer.renderLayers` path.
- Piston-head redirects restored to the classic `PistonHeadRenderer.render` path.
- The 1.21.11-only `ItemUseAnimation.SPEAR` value is represented by the existing custom spear renderer/use logic with the supported BOW use-animation token; spear rendering itself is intercepted by the mod's dedicated mixins.
- All binary media assets were retained unchanged.

## Validation performed in this environment

- Every JSON resource file parses successfully.
- All model `format_version` values that were source-version-specific were converted from `1.21.10` to `1.21.8` (50 files).
- PNG/JPG/JPEG/WebP/OGG/WAV/MP3 assets are byte-for-byte identical to the supplied source project.
- The existing `minecraft:item/spears` tag is present and still contains the Useful Ores spear IDs.
- No active Java/config source contains stale `SubmitNodeCollector`, `CameraRenderState`, `PistonHeadRenderState`, `ModelFeatureRenderer`, `ItemUseAnimation.SPEAR`, or intermediary spear selectors that were removed for this target.
- Changed Java files pass a parser/syntax smoke test: no syntax-like diagnostics were emitted by `javac`; dependency-resolution diagnostics are expected because this environment does not contain the Minecraft/Fabric/GeckoLib classpath.

## Build limitation

A full Gradle/Minecraft compile and runtime test could not be executed here because the supplied project has no Gradle wrapper and this execution environment has neither a Gradle executable nor the required Minecraft/Fabric dependency cache. Run `gradle build` in a normal networked development environment, then launch client instances on 1.21.8 and 1.21.7 for final runtime verification.

## Intentional historical files

The project retains older Vxx diagnostic notes from the source tree for provenance. They are documentation/history only and are not active build or runtime inputs.
