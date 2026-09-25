# V8 — Spear TPV render-order + NeoForge 1.21.7 metadata fix

## Primary TPV fix
The spear item transform injection is placed immediately before `ItemStackRenderState.render(...)` in `ItemInHandLayer.renderArmWithItem`, after Vanilla's hand translation/orientation and item-layer setup. The prior V7 HEAD injection transformed the pose too early, so the later Vanilla hand transforms distorted/overrode the custom spear charge/jab item motion.

This matches the 1.21.8 classic render pipeline: `renderArmWithItem` establishes the hand/item pose, then spear-specific item transforms are applied immediately before the actual item render.

## Preserved
- First-person spear implementation unchanged.
- Third-person custom arm math unchanged for this revision; only item-transform timing is corrected first so the TPV spear follows the hand correctly.
- All textures and resource assets unchanged.
- No gameplay/combat values changed.

## NeoForge 1.21.7
The NeoForge dependency metadata now permits `[21.7.0,21.9.0)` instead of rejecting 21.7 before pre-loading. This removes the hard dependency failure seen in the user's 1.21.7 log; runtime API compatibility still must be tested with the 1.21.7 client.
