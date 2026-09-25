plugins {
    java
    id("net.neoforged.moddev") version "2.0.+"
}

val modId = "useful_ores"
val modVersion = project.findProperty("mod_version") as String? ?: "2.1.7+mc1.21.4-backport-v35"
val neoForgeVersion = project.findProperty("neoforge_version") as String? ?: "21.4.140"
val geckolibVersion = project.findProperty("geckolib_version") as String? ?: "4.8.5"

group = "com.neutrinodust"
version = modVersion

base {
    archivesName.set("$modId-neoforge")
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

neoForge {
    version = neoForgeVersion



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
        name = "NeoForged"
        url = uri("https://maven.neoforged.net/releases")
    }
    maven {
        name = "CurseMaven"
        url = uri("https://cursemaven.com")
    }
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
    // GeckoLib 4.8.5 is published for Minecraft 1.21.4.
    implementation("software.bernie.geckolib:geckolib-neoforge-1.21.4:4.8.5")
    compileOnly("org.jspecify:jspecify:1.0.0")
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

