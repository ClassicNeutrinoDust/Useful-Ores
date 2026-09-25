plugins {
    id("net.fabricmc.fabric-loom") version "1.15.+"
    id("java")
}

val modId = "useful_ores"
val modVersion = project.findProperty("mod_version") as String? ?: "2.1.7"
val minecraftVersion = project.findProperty("minecraft_version") as String? ?: "26.2"
val fabricLoaderVersion = project.findProperty("fabric_loader_version") as String? ?: "0.18.6"
val fabricApiVersion = project.findProperty("fabric_api_version") as String? ?: "0.152.2+26.2"

group = "com.neutrinodust"
version = modVersion

base {
    archivesName.set("$modId-fabric")
}

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

repositories {
    mavenCentral()
}

// Minecraft 26.1+ ships fully unobfuscated (Mojang's own names, no more obfuscation
// maps) - Loom 1.15's non-remapping "fabric-loom" plugin does NOT take a `mappings(...)`
// dependency at all (there is nothing left to map), and dependencies use the standard
// `implementation`/`compileOnly` configurations instead of the old `modImplementation`/
// `modCompileOnly` (those were for remapping mod jars, which no longer happens either).
// See https://fabricmc.net/2026/03/14/261.html - do not add mappings(loom.officialMojangMappings())
// back in, that call is what caused the earlier "Failed to find official mojang mappings
// for 26.1.2" build failure; there are no mappings published for 26.1+ because the game
// itself is no longer obfuscated.
dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    implementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    implementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")

    // GeckoLib remains available for the other mod content; the Solarite minecart itself does not use it.
    implementation(fileTree("libs") { include("*.jar") })

    // No Sodium dependency: Fabric users get real, official Sodium as a separate mod
    // install. The NeoForge build's Sodium compat mixin (compileOnly against Sodium's
    // inner jar) is NOT ported as-is - see MixinNotes.md for what to do with
    // SodiumMixinPlugin/its guarded mixin when you get to the mixin subsystem pass.
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand(mutableMapOf("version" to project.version))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(25)
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}
