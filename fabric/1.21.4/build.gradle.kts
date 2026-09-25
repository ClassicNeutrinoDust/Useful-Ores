plugins {
    id("fabric-loom") version "1.10.5"
    id("maven-publish")
    id("java")
}

val modId = "useful_ores"
val modVersion = project.findProperty("mod_version") as String? ?: "2.1.7+mc1.21.4-backport-v33"
val minecraftVersion = project.findProperty("minecraft_version") as String? ?: "1.21.4"
val loaderVersion = project.findProperty("loader_version") as String? ?: "0.16.10"
val fabricApiVersion = project.findProperty("fabric_api_version") as String? ?: "0.114.3+1.21.4"

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
    maven {
        name = "GeckoLib"
        url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    }
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
    // GeckoLib 4.8.5 is published for Minecraft 1.21.4.
    modImplementation("software.bernie.geckolib:geckolib-fabric-1.21.4:4.8.5")
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
