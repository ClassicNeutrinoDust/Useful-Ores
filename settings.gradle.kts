pluginManagement {
    repositories {
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.neoforged.net/releases") }
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "useful_ores_26_3_workspace"

includeBuild("fabric/26.3") {
    name = "fabric-26.3"
}

includeBuild("neoforge/26.3") {
    name = "neoforge-26.3"
}
