# Useful Ores 1.21.4 Backport V32

Gameplay/runtime compatibility fixes:

1. Spear charging movement: restored the 1.21.4-safe `LocalPlayer.modifyInput(Vec2)` replacement used by the reference implementation. Only the generic 0.2x use-item slowdown is skipped while the main-hand item is a spear; vanilla input scaling, sneak/slow movement scaling and directional normalization are preserved.

2. Weapon special effects: moved Useful Ores special-hit callbacks from `postHurtEnemy` to the 1.21.4 `hurtEnemy` boolean hook. Effects are applied only when the hook reports success, preserving the intended attacker/target recipient logic and covering attributed Subspace weapons and ore weapons such as Nyxium.

3. Both Fabric and NeoForge are updated together and versioned as `2.1.7+mc1.21.4-backport-v32`.
