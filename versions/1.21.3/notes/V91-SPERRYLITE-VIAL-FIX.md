# Useful Ores 1.21.3 V91 — Sperrylite Catalytic Vial parity fix

## Root cause
The 1.21.4 client item definitions tint the vial's generated liquid overlay with the `minecraft:potion` tint source. Minecraft 1.21.3 does not have the 1.21.4 client-item tint-source system, so the legacy generated models need an `ItemColorProvider`/NeoForge item color handler for tint index 0.

Without this provider, the vial model can select its filled variant, but the grayscale liquid overlay remains untinted (white). This also affects splash/lingering vial variants and the Compendium, because the Compendium renders ordinary `ItemStack`s through the same item-color pipeline.

## V91 changes

### Fabric 1.21.3
`LegacyItemProperties.register()` now also registers a `ColorProviderRegistry.ITEM` provider for:
- sperrylite_catalytic_vial
- sperrylite_catalytic_vial_splash
- sperrylite_catalytic_vial_lingering

Tint index 0 reads `DataComponents.POTION_CONTENTS#getColor()` and returns -1 for other layers.

### NeoForge 1.21.3
`LegacyItemProperties` now subscribes explicitly to the MOD event bus and registers the same tint provider through `RegisterColorHandlersEvent.Item`.

The existing custom `useful_ores:potion_contents` property registration remains, so the 1.21.3 legacy override still switches the base vial to its filled model.

## Expected result
- Empty vial remains uncolored.
- Filling from water creates one filled vial with water-blue liquid tint.
- A stacked set of empty vials is split by the existing server-side `ItemStack#split(1)` logic; only one vessel is filled.
- Brewing two potions creates a `PotionContents` carrying the combined effect list. `getColor()` supplies the averaged effect color, matching the 1.21.4 `minecraft:potion` tint-source semantics.
- Splash and lingering vials tint their overlay as well.
- Compendium example vial stacks use the same `PotionContents` component and therefore receive the same color provider automatically.
- The existing use-on-dirt-to-mud implementation is already structurally correct and was not changed: it requires the clicked block to be `#minecraft:convertable_to_mud`, rejects the bottom face, requires a water `PotionContents`, converts the clicked block to MUD, emits the vanilla-style splash/fluid-place behavior, and replaces one filled vial with one empty vial.

## No rendering architecture changes
This patch does not modify item transforms, model selection architecture, GeckoLib rendering, GUI lighting, PNG textures, or the spear systems.
