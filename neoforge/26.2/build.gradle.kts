plugins {
    java
    id("net.neoforged.moddev") version "2.0.+"
}

val modId = "useful_ores"
val modVersion = project.findProperty("mod_version") as String? ?: "2.1.8-26.2"
val neoForgeVersion = project.findProperty("neoforge_version") as String? ?: "26.2.0.88"
val minecraftVersion = project.findProperty("minecraft_version") as String? ?: "26.2"

group = "com.neutrinodust"
version = modVersion

base {
    archivesName.set("$modId-neoforge")
}

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

neoForge {
    version = neoForgeVersion

    parchment {
        // Fill in a parchment mappings version for your Minecraft version if you want
        // parameter names / javadocs in the dev environment, e.g.:
        // minecraftVersion = "26.2"
        // mappingsVersion = "<latest for this MC version>"
    }

    runs {
        create("client") {
            client()
        }
        create("server") {
            server()
        }
        create("data") {
            data()
        }
        configureEach {
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}

repositories {
    mavenCentral()
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
    }
    maven {
        name = "NeoForged"
        url = uri("https://maven.neoforged.net/releases")
    }
}

dependencies {
    // GeckoLib 5.5.6 for Minecraft 26.2, supplied locally.
    implementation(files("libs/geckolib-neoforge-26.2-5.5.6.jar"))

}
