# V7 – NeoForge 1.21.7 diagnostic

The supplied 1.21.7 crash stops in NeoForge's `ClientHooks.createGpuDevice` while the
Minecraft window is not initialized, and the crash report's Mod List contains only
Minecraft and NeoForge. Useful Ores is therefore not reached by class initialization.
This is not a mod-source exception. Keep the universal version range for the mod, but
run NeoForge 1.21.7 with Java 21 (the supported runtime for Minecraft 1.21.x).
