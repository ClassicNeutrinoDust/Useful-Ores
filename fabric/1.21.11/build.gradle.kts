plugins {
    id("net.fabricmc.fabric-loom-remap") version "1.15.4"
    id("maven-publish")
    id("java")
}

val modId = "useful_ores"
val modVersion = project.findProperty("mod_version") as String? ?: "2.1.7+mc1.21.11"
val minecraftVersion = project.findProperty("minecraft_version") as String? ?: "1.21.11"
val loaderVersion = project.findProperty("loader_version") as String? ?: "0.18.4"
val fabricApiVersion = project.findProperty("fabric_api_version") as String? ?: "0.141.5+1.21.11"

group = "com.neutrinodust"
version = modVersion
base { archivesName.set("$modId-fabric") }

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

repositories {
    mavenCentral()
    maven { name = "CurseMaven"; url = uri("https://cursemaven.com") }
}

val localGeckoLib = fileTree("libs") {
    include("geckolib*.jar")
}
if (localGeckoLib.isEmpty) {
    throw GradleException("Missing GeckoLib JAR in Fabric-1.21.11/libs. Put the 1.21.11 Fabric GeckoLib JAR there.")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    modImplementation(localGeckoLib)
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
