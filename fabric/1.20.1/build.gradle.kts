plugins {
    id("fabric-loom") version "1.10-SNAPSHOT"
    id("maven-publish")
    id("java")
}

val modId = "useful_ores"
val modVersion = project.findProperty("mod_version") as String? ?: "2.2.0"
val minecraftVersion = project.findProperty("minecraft_version") as String? ?: "1.20.1"
val minecraftVersionRange = project.findProperty("minecraft_version_range") as String? ?: "~1.20.1"
val loaderVersion = project.findProperty("loader_version") as String? ?: "0.17.2"
val fabricApiVersion = project.findProperty("fabric_api_version") as String? ?: "0.92.12+1.20.1"
val geckolibVersion = project.findProperty("geckolib_version") as String? ?: "4.8.4"

group = "com.neutrinodust"
version = "$modVersion-1.20.1"
base { archivesName.set("$modId-fabric") }

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

loom {
    mixin {
        useLegacyMixinAp = true
        defaultRefmapName = "useful_ores.refmap.json"
    }
}

repositories {
    mavenCentral()
    maven { name = "CurseMaven"; url = uri("https://cursemaven.com") }
    maven { name = "GeckoLib"; url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/") }
    exclusiveContent {
        forRepository { maven { name = "Modrinth"; url = uri("https://api.modrinth.com/maven") } }
        filter { includeGroup("maven.modrinth") }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    modImplementation("software.bernie.geckolib:geckolib-fabric-1.20.1:$geckolibVersion")
    compileOnly("org.jspecify:jspecify:1.0.0")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand(mutableMapOf("version" to project.version, "minecraft_dependency" to minecraftVersionRange))
    }
}

tasks.withType<JavaCompile>().configureEach { options.release.set(17) }

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
