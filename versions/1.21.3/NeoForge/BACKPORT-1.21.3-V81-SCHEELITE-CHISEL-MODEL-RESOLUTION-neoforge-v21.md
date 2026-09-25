# V81 Scheelite Chisel model-resolution rebuild (neoforge-v21)

V79/V80 custom renderer approaches are not used. The V78 working overall architecture is preserved.

The user-supplied model contains eight authored cuboids, an 8x8 texture, and explicit first/third-person transforms. Its geometry is moved behind `models/block/scheelite_chisel_geometry.json`, while `models/item/scheelite_chisel_hand.json` is a thin item shell carrying those transforms.

NeoForge additionally resolves the side-loaded hand model at the `ItemRenderer.getModel(ItemStack, Level, LivingEntity, int)` boundary. The existing late context bridge may still choose the GUI icon and ground/fixed canonical model, but hand rendering reaches the chisel hand model before the actual render method.

No PoseStack offsets, BEWLR, or custom chisel renderer are introduced.
