plugins {
    java
    id("net.neoforged.moddev") version "2.0.+"
}

val modId = "useful_ores"
val modVersion = project.findProperty("mod_version") as String? ?: "2.1.7"
val neoForgeVersion = project.findProperty("neoforge_version") as String? ?: "26.1.2.75"
val minecraftVersion = project.findProperty("minecraft_version") as String? ?: "26.1.2"

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
        // minecraftVersion = "26.1.2"
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
        name = "NeoForged"
        url = uri("https://maven.neoforged.net/releases")
    }
}

dependencies {
    // GeckoLib 5.5.2 built specifically for NeoForge 26.1.2 (package com.geckolib,
    // NOT software.bernie.geckolib — that Cloudsmith 5.4.2 build was for MC 1.21.11
    // and used the old package name, which is why the code didn't match it).
    // Loaded from the local jar dropped in mod/libs/ since this build isn't
    // published on a resolvable Maven feed yet.
    //
    // NOT bundled/shaded — players need to install GeckoLib as a separate mod,
    // same as with almost every other GeckoLib-using mod. Shading it in (shadowJar)
    // pulled in the mod's entire runtime classpath by default — not just GeckoLib —
    // which is what caused the 117MB jar and very likely the item disappearing
    // (a jar full of duplicate/conflicting NeoForge internals can silently break
    // mod loading). If you want it bundled again later, it needs a shadowJar
    // configuration scoped to ONLY this dependency, not runtimeClasspath.
    implementation(files("libs/geckolib-neoforge-26_1_2-5_5_2.jar"))

    // Sodium compat mixin only needs Sodium's classes at COMPILE time (to
    // reference BlockRenderer/MutableQuadViewImpl directly instead of via
    // reflection). This must be compileOnly, not implementation - Sodium is
    // an optional dependency and this jar must NOT be bundled or added to
    // the runtime classpath, or the game will load a second copy of Sodium
    // on top of whatever version the user actually has installed, which
    // will crash mod loading. The mixin itself is also guarded at runtime
    // by SodiumMixinPlugin (checks Class.forName before Mixin ever tries to
    // apply), so this compiles clean whether or not Sodium is present in a
    // player's actual mod folder.
    // Note: this is the INNER jar unpacked from Sodium's own jar-in-jar
    // wrapper (META-INF/jarjar/...) - the outer sodium-neoforge-*.jar only
    // contains a manifest + the nested jar, not the actual classes, so
    // compileOnly against the outer jar would fail to resolve any of
    // Sodium's real classes.
    compileOnly(files("libs/sodium-inner-neoforge-0_8_12_2Bmc26_1_2.jar"))
}
