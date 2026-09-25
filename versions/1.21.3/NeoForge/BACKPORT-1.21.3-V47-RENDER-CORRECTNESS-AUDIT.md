# Useful Ores 1.21.3 v47 rendering correctness audit

Restored the authored 1.21.4 context behavior on 1.21.3:
- all 17 spear in-hand models use minecraft:item/spear_in_hand and their dedicated 32x32 textures;
- Meteor Staff and Nyxiumnite Staff use flat authored icons in GUI/ground/fixed and GeckoLib only in hand;
- Scheelite Chisel uses its authored 3D Blockbench model outside GUI and dedicated icon in GUI;
- Arcanite XP Jar uses the ground model on ground and its XP-dependent 2D icon in other contexts;
- Fulgurite Electric Trap and Solar Battery retain their authored generated item icons while their placed-block renderers remain separate.
