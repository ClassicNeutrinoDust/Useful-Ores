plugins {
    id("fabric-loom") version "1.10.5"
    id("maven-publish")
    id("java")
}

val modId = "useful_ores"
val modVersion = project.findProperty("mod_version") as String? ?: "2.1.7+mc1.21.5-backport-v15"
val minecraftVersion = project.findProperty("minecraft_version") as String? ?: "1.21.5"
val loaderVersion = project.findProperty("loader_version") as String? ?: "0.16.10"
val fabricApiVersion = project.findProperty("fabric_api_version") as String? ?: "0.119.5+1.21.5"

group = "com.neutrinodust"
version = modVersion
base { archivesName.set("$modId-fabric") }

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

loom {
    // Generate and embed a Mixin refmap. This is required for production/dev-client
    // runs that load intermediary Minecraft names while the source uses Mojang mappings.
    mixin {
        useLegacyMixinAp = true
        defaultRefmapName = "useful_ores.refmap.json"
    }
}

repositories {
    mavenCentral()
    maven { name = "CurseMaven"; url = uri("https://cursemaven.com") }
    exclusiveContent {
        forRepository {
            maven { name = "Modrinth"; url = uri("https://api.modrinth.com/maven") }
        }
        filter { includeGroup("maven.modrinth") }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    // GeckoLib 5.1.0 is published for Minecraft 1.21.5.
    modImplementation("maven.modrinth:8BmcQJ2H:AyXpbm2h")
    compileOnly("org.jspecify:jspecify:1.0.0")
}

fabricApi { configureDataGeneration() }

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") { expand(mutableMapOf("version" to project.version)) }
}

tasks.withType<JavaCompile>().configureEach { options.release.set(21) }

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
