# V7 – Fabric 1.21.8/1.21.7 runtime mixin fix

The 1.21.8 runtime crash was caused by `MixinVanillaSpearFirstPerson` using a named
`renderFirstPersonItem` target while the runtime class is in intermediary namespace
and the deployed configuration had no usable refmap. The injector is now anchored to
Minecraft's stable intermediary method `method_3228`, and the build explicitly generates
and embeds `useful_ores.refmap.json` for the remaining Mojang-mapped mixins.

The NeoForge 1.21.7 crash supplied with V6 is different: its crash occurs in
NeoForge `ClientHooks.createGpuDevice` before Useful Ores appears in the crash report's
mod list. That is loader startup, not a Useful Ores class/mixin failure. See the main
conversation for the evidence and Java 21 recommendation.
