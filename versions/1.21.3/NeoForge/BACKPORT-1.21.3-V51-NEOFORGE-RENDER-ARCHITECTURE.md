# Useful Ores 1.21.3 NeoForge — V51 Render Architecture Rewrite

## Why V50 was ineffective

V50 did not replace the NeoForge rendering architecture. The NeoForge branch still depended on the old `ContextualItemRendering` + `MixinItemRendererContextModels` HEAD interception, while the meaningful new changes were applied to the Fabric branch. That is why building NeoForge reproduced the same invisible/wrong 3D models.

V51 removes that interception entirely from NeoForge.

## New architecture

### 1. NeoForge custom-renderer boundary

NeoForge 1.21.3 already has the correct native hook for this job:

`minecraft:builtin/entity` -> `BakedModel#isCustomRenderer()` -> `IClientItemExtensions#getCustomRenderer()` -> `BlockEntityWithoutLevelRenderer#renderByItem(...)`

V51 registers one singleton-style `IClientItemExtensions` and one `UsefulOresContextualItemRenderer` for:

- Scheelite Chisel
- Arcanite XP Jar
- all 17 Useful Ores spears

No `ItemRenderer#render` HEAD interception remains.

### 2. Explicit model selection, then vanilla rendering

The BEWLR selects an ordinary baked model and calls `ItemRenderer.render(..., selectedModel)` with that model explicitly.

This is important: the selected model is not resolved from the ItemStack again, so there is no recursive custom-renderer loop.

The selected normal baked model is responsible for its own vanilla display transform.

### 3. GeckoLib staffs use GeckoLib's renderer path

Meteor Staff and Nyxiumnite Staff already have `builtin/entity` carrier models and GeckoLib `GeoRenderProvider`s. V51 lets that path run normally.

Their renderer classes only override GUI/GROUND/FIXED to draw the authored flat icon. FIRST/THIRD person stays in the actual GeckoLib `GeoItemRenderer`.

Reflection adapters and render guards were removed.

### 4. Contextual static-model mapping

The 1.21.4 item-definition behavior is reproduced explicitly:

- Scheelite Chisel: GUI -> icon, otherwise -> original Blockbench 3D model.
- Arcanite XP Jar: GROUND -> jar ground/block model, otherwise -> XP level icon 0..7 derived from the jar's stored XP component.
- Spears: GUI/GROUND/FIXED -> inventory/generated model, FIRST/THIRD person -> exact existing `*_in_hand` model.
- Painite spear: GUI and hand contexts honor the fury component; GROUND/FIXED retain the normal Painite spear model, matching the supplied 1.21.4 item definition.

### 5. Spear third-person pose injection moved to the real 1.21.3 callsite

Minecraft 1.21.3's `ItemInHandLayer#renderArmWithItem` still calls:

`ItemRenderer.render(ItemStack, ItemDisplayContext, boolean, PoseStack, MultiBufferSource, int, int, BakedModel)`

V51 injects immediately before that invocation and applies the existing `VanillaSpearAnimations.thirdPersonAttackItem(...)` / `thirdPersonUseItem(...)` math.

This replaces the incorrect 1.21.4 `ItemStackRenderState` injection target and is specifically intended to fix the third-person spear orientation shown in the report screenshot.

## Resource architecture

The old authored static models were preserved byte-for-byte in dedicated files:

- `*_spear_inventory.json`
- `painite_spear_fury_inventory.json`
- `scheelite_chisel_3d.json`

The item-facing `*_spear.json`, `painite_spear_fury.json`, `scheelite_chisel.json`, and `arcanite_xp_jar.json` are now inert `minecraft:builtin/entity` carrier models.

The existing 1.21.3 spear hand models were already byte-for-byte identical to the supplied 1.21.4 reference; V51 therefore uses them rather than rewriting their transforms.

## Validation

- 3260 JSON resources parse successfully.
- 19 custom-renderer carrier models are present: 17 spear roots + Scheelite Chisel + Arcanite XP Jar.
- All 17 spear inventory models exist.
- All spear `*_in_hand` variants exist, including both Painite Fury and normal variants.
- 1.21.3 mapping confirms the exact `ItemRenderer.render(...)` signature used by the new third-person mixin.
- 1.21.3 mapping confirms `ItemInHandLayer#renderArmWithItem(...)` takes `LivingEntityRenderState, BakedModel, ItemStack, ItemDisplayContext, HumanoidArm, PoseStack, MultiBufferSource, int`.
- Old `ContextualItemRendering` and `MixinItemRendererContextModels` source references are absent.

## Build

The environment used for this patch has no Gradle executable and no network access for dependency resolution, so a real NeoForge Gradle build could not be executed here.

Build the `neoforge-v21` project locally with:

```bat
gradle clean build
```

The intended test matrix is:

1. Meteor Staff — GUI, ground, fixed, first-person, third-person.
2. Nyxiumnite Staff — same contexts.
3. Scheelite Chisel — GUI versus held/ground/fixed 3D.
4. Empty and partially/full Arcanite XP Jar — GUI/hand/ground.
5. Every spear — GUI/GROUND/FIXED versus FIRST/THIRD person.
6. Painite spear — normal and `painite_fury_tool` component variants.
7. Spear attack jab and charged-use animation in both first- and third-person.
