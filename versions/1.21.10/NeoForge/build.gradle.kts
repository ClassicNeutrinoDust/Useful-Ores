plugins {
    java
    id("net.neoforged.moddev") version "2.0.+"
}

val modId = "useful_ores"
val modVersion = project.findProperty("mod_version") as String? ?: "2.1.7+mc1.21.10"
val neoForgeVersion = project.findProperty("neoforge_version") as String? ?: "21.10.64"
val geckolibVersion = project.findProperty("geckolib_version") as String? ?: "5.3-alpha-3"

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
}

val localGeckoLib = fileTree("libs") {
    include("geckolib*.jar")
}
if (localGeckoLib.isEmpty) {
    throw GradleException("Missing GeckoLib JAR in NeoForge-1.21.10/libs. Put the 1.21.10 NeoForge GeckoLib JAR there.")
}

dependencies {
    implementation(localGeckoLib)
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

