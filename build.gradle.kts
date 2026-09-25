// Useful Ores 26.3 workspace aggregator.
// The actual loader-specific projects remain in fabric/26.3 and neoforge/26.3.

tasks.register("buildFabric26_3") {
    group = "useful-ores"
    description = "Build the Fabric 26.3 project."
    dependsOn(gradle.includedBuild("fabric-26.3").task(":build"))
}

tasks.register("buildNeoForge26_3") {
    group = "useful-ores"
    description = "Build the NeoForge 26.3 project."
    dependsOn(gradle.includedBuild("neoforge-26.3").task(":build"))
}

tasks.register("buildAll26_3") {
    group = "useful-ores"
    description = "Build both Fabric and NeoForge 26.3 projects."
    dependsOn("buildFabric26_3", "buildNeoForge26_3")
}
