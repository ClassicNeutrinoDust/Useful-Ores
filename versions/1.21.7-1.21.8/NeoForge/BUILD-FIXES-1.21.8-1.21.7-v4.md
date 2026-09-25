# Useful Ores — 1.21.8/1.21.7 Backport Build-Fix Revision v4

This revision fixes the compiler errors reported by the 1.21.8 Fabric and NeoForge builds.

## Fixed API migrations
- Fabric WorldRenderContext/WorldRenderEvents restored to the classic `net.fabricmc.fabric.api.client.rendering.v1` package.
- Fabric world overlays moved off the 1.21.9+ submit/state API and back to the classic `WorldRenderEvents.AFTER_TRANSLUCENT` path.
- Classic 1.21.8 particle API restored: `ParticleRenderType`, `RisingParticle` six-coordinate constructor, `setSprite`, `SpriteSet.get(age,lifetime)`, and the seven-argument `ParticleProvider#createParticle` signature.
- GUI mouse handlers restored to `mouseClicked(double,double,int)`.
- KeyMapping categories restored to the classic String category API.
- Block `noCollision()` calls restored to the 1.21.8 `noCollission()` API.
- `BlockBehaviour.entityInside` restored to the five-argument 1.21.8 signature.
- Argentite filter analog-output signature restored to the 1.21.8 three-argument form.
- BlockEntityRenderer implementations restored to the classic `(T,float,PoseStack,MultiBufferSource,int,int,Vec3)` render signature.
- BeaconRenderer construction restored to accept the renderer context.
- Painite armor mixin redirected to the 1.21.8 `EquipmentLayerRenderer.renderLayers` signature and captures the enclosing HumanoidRenderState from `HumanoidArmorLayer.render`.
- NeoForge development-environment check restored to `FMLEnvironment.production`.
- NeoForge Solarite Furnace capability registration restored to `Capabilities.ItemHandler.BLOCK` + `SidedInvWrapper` so sided automation semantics are preserved.

## Resource integrity
No PNG/JPG/JPEG/WebP/OGG/WAV/MP3 asset bytes are modified by this revision. JSON resources are syntactically valid.

## Build limitation
The supplied environment used for this source revision does not contain a Gradle installation or project Gradle wrapper, so this revision cannot be truthfully marked as Gradle-built here. Run `gradle clean build` locally against the supplied 1.21.8 toolchain for final compilation/runtime verification.
