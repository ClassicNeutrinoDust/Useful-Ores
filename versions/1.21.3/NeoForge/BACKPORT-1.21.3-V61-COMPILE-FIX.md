# Useful Ores 1.21.3 V61 — NeoForge renderer registration compile fix

V60 introduced the native NeoForge `IClientItemExtensions -> BlockEntityWithoutLevelRenderer` architecture, but its registration used `RegisterClientExtensionsEvent`, which is not exposed by the exact NeoForge 21.3.97 compile classpath used by this project.

V61 keeps the same renderer architecture but moves registration to the stable per-item `Item.initializeClient(Consumer<IClientItemExtensions>)` extension point.

Updated items:
- `BackportSpearItem`
- `ScheeliteChiselItem`
- `ArcaniteXpJarItem`

All three now return the singleton `ContextualClientItemExtensions`, which supplies `ContextualStaticItemRenderer`.

The broken `ContextualItemRendererRegistration` event subscriber was removed.

The contextual model-registration event remains on the MOD bus because `ModelEvent.RegisterAdditional` is the correct model-loading event for the extra baked models.
