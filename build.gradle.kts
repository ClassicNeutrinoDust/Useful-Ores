import org.gradle.api.GradleException
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.GradleBuild

data class Target(
    val loader: String,
    val minecraft: String,
    val dir: java.io.File
)

val targets = listOf(
    Target("fabric", "1.21.3", file("fabric/1.21.3")),
    Target("fabric", "1.21.4", file("fabric/1.21.4")),
    Target("fabric", "1.21.5", file("fabric/1.21.5")),
    Target("fabric", "1.21.6", file("fabric/1.21.6")),
    Target("fabric", "1.21.7-1.21.8", file("fabric/1.21.7-1.21.8")),
    Target("fabric", "1.21.10", file("fabric/1.21.10")),
    Target("fabric", "1.21.11", file("fabric/1.21.11")),
    Target("fabric", "26.1.2", file("fabric/26.1.2")),
    Target("fabric", "26.2", file("fabric/26.2")),
    Target("fabric", "26.3", file("fabric/26.3")),
    Target("neoforge", "1.21.3", file("neoforge/1.21.3")),
    Target("neoforge", "1.21.4", file("neoforge/1.21.4")),
    Target("neoforge", "1.21.5", file("neoforge/1.21.5")),
    Target("neoforge", "1.21.6", file("neoforge/1.21.6")),
    Target("neoforge", "1.21.7-1.21.8", file("neoforge/1.21.7-1.21.8")),
    Target("neoforge", "1.21.10", file("neoforge/1.21.10")),
    Target("neoforge", "1.21.11", file("neoforge/1.21.11")),
    Target("neoforge", "26.1.2", file("neoforge/26.1.2")),
    Target("neoforge", "26.2", file("neoforge/26.2")),
    Target("neoforge", "26.3", file("neoforge/26.3"))
)

fun loaderLabel(loader: String): String =
    when (loader.lowercase()) {
        "fabric" -> "Fabric"
        "neoforge" -> "NeoForge"
        else -> loader
    }

fun taskName(loader: String, minecraft: String): String =
    "build" + loaderLabel(loader).replace(" ", "") +
        minecraft.replace(Regex("[^A-Za-z0-9]"), "")

fun printTarget(target: Target) {
    println()
    println("==============================================")
    println("Useful Ores build")
    println("Minecraft version : ${target.minecraft}")
    println("Loader            : ${loaderLabel(target.loader)}")
    println("Project directory : ${target.dir.relativeTo(project.projectDir)}")
    println("Gradle task       : build")
    println("==============================================")
    println()
}

tasks.register<DefaultTask>("listBuilds") {
    group = "Useful Ores"
    description = "Lists all available Useful Ores loader/version builds."
    doLast {
        println()
        println("Available Useful Ores builds:")
        println()
        targets.forEach {
            println("${taskName(it.loader, it.minecraft).padEnd(28)} -> ${loaderLabel(it.loader).padEnd(9)} Minecraft ${it.minecraft}")
        }
        println()
        println("Generic:")
        println("  gradle build -Ploader=fabric -PmcVersion=26.3")
        println("  gradle build -Ploader=neoforge -PmcVersion=26.3")
        println()
    }
}

val loaderProperty = providers.gradleProperty("loader").orNull?.trim()?.lowercase()
val minecraftProperty = providers.gradleProperty("mcVersion").orNull?.trim()

val selectedTarget = if (loaderProperty != null && minecraftProperty != null) {
    targets.firstOrNull {
        it.loader.lowercase() == loaderProperty && it.minecraft == minecraftProperty
    }
} else null

tasks.register<GradleBuild>("build") {
    group = "Useful Ores"
    description = "Build one selected Useful Ores loader/version project."

    if (selectedTarget != null) {
        dir = selectedTarget.dir
        tasks = listOf("build")
    } else {
        dir = project.projectDir
        tasks = listOf("help")
    }

    doFirst {
        if (loaderProperty == null || minecraftProperty == null) {
            throw GradleException(
                "Select a build with: gradle build -Ploader=fabric|neoforge -PmcVersion=<version>. Run gradle listBuilds first."
            )
        }
        if (selectedTarget == null) {
            throw GradleException(
                "Unknown target: loader=$loaderProperty, mcVersion=$minecraftProperty. Run gradle listBuilds."
            )
        }
        printTarget(selectedTarget!!)
    }

    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : ${selectedTarget!!.minecraft}")
        println("Loader            : ${loaderLabel(selectedTarget!!.loader)}")
        println()
    }
}

tasks.register<GradleBuild>("buildFabric1213") {
    group = "Useful Ores"
    description = "Build Useful Ores for Fabric Minecraft 1.21.3."
    dir = file("fabric/1.21.3")
    tasks = listOf("build")
    doFirst {
        printTarget(Target("fabric", "1.21.3", dir))
    }
    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : 1.21.3")
        println("Loader            : Fabric")
        println()
    }
}

tasks.register<GradleBuild>("buildFabric1214") {
    group = "Useful Ores"
    description = "Build Useful Ores for Fabric Minecraft 1.21.4."
    dir = file("fabric/1.21.4")
    tasks = listOf("build")
    doFirst {
        printTarget(Target("fabric", "1.21.4", dir))
    }
    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : 1.21.4")
        println("Loader            : Fabric")
        println()
    }
}

tasks.register<GradleBuild>("buildFabric1215") {
    group = "Useful Ores"
    description = "Build Useful Ores for Fabric Minecraft 1.21.5."
    dir = file("fabric/1.21.5")
    tasks = listOf("build")
    doFirst {
        printTarget(Target("fabric", "1.21.5", dir))
    }
    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : 1.21.5")
        println("Loader            : Fabric")
        println()
    }
}

tasks.register<GradleBuild>("buildFabric1216") {
    group = "Useful Ores"
    description = "Build Useful Ores for Fabric Minecraft 1.21.6."
    dir = file("fabric/1.21.6")
    tasks = listOf("build")
    doFirst {
        printTarget(Target("fabric", "1.21.6", dir))
    }
    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : 1.21.6")
        println("Loader            : Fabric")
        println()
    }
}

tasks.register<GradleBuild>("buildFabric12171218") {
    group = "Useful Ores"
    description = "Build Useful Ores for Fabric Minecraft 1.21.7-1.21.8."
    dir = file("fabric/1.21.7-1.21.8")
    tasks = listOf("build")
    doFirst {
        printTarget(Target("fabric", "1.21.7-1.21.8", dir))
    }
    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : 1.21.7-1.21.8")
        println("Loader            : Fabric")
        println()
    }
}

tasks.register<GradleBuild>("buildFabric12110") {
    group = "Useful Ores"
    description = "Build Useful Ores for Fabric Minecraft 1.21.10."
    dir = file("fabric/1.21.10")
    tasks = listOf("build")
    doFirst {
        printTarget(Target("fabric", "1.21.10", dir))
    }
    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : 1.21.10")
        println("Loader            : Fabric")
        println()
    }
}

tasks.register<GradleBuild>("buildFabric12111") {
    group = "Useful Ores"
    description = "Build Useful Ores for Fabric Minecraft 1.21.11."
    dir = file("fabric/1.21.11")
    tasks = listOf("build")
    doFirst {
        printTarget(Target("fabric", "1.21.11", dir))
    }
    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : 1.21.11")
        println("Loader            : Fabric")
        println()
    }
}

tasks.register<GradleBuild>("buildFabric2612") {
    group = "Useful Ores"
    description = "Build Useful Ores for Fabric Minecraft 26.1.2."
    dir = file("fabric/26.1.2")
    tasks = listOf("build")
    doFirst {
        printTarget(Target("fabric", "26.1.2", dir))
    }
    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : 26.1.2")
        println("Loader            : Fabric")
        println()
    }
}

tasks.register<GradleBuild>("buildFabric262") {
    group = "Useful Ores"
    description = "Build Useful Ores for Fabric Minecraft 26.2."
    dir = file("fabric/26.2")
    tasks = listOf("build")
    doFirst {
        printTarget(Target("fabric", "26.2", dir))
    }
    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : 26.2")
        println("Loader            : Fabric")
        println()
    }
}

tasks.register<GradleBuild>("buildFabric263") {
    group = "Useful Ores"
    description = "Build Useful Ores for Fabric Minecraft 26.3."
    dir = file("fabric/26.3")
    tasks = listOf("build")
    doFirst {
        printTarget(Target("fabric", "26.3", dir))
    }
    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : 26.3")
        println("Loader            : Fabric")
        println()
    }
}

tasks.register<GradleBuild>("buildNeoForge1213") {
    group = "Useful Ores"
    description = "Build Useful Ores for NeoForge Minecraft 1.21.3."
    dir = file("neoforge/1.21.3")
    tasks = listOf("build")
    doFirst {
        printTarget(Target("neoforge", "1.21.3", dir))
    }
    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : 1.21.3")
        println("Loader            : NeoForge")
        println()
    }
}

tasks.register<GradleBuild>("buildNeoForge1214") {
    group = "Useful Ores"
    description = "Build Useful Ores for NeoForge Minecraft 1.21.4."
    dir = file("neoforge/1.21.4")
    tasks = listOf("build")
    doFirst {
        printTarget(Target("neoforge", "1.21.4", dir))
    }
    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : 1.21.4")
        println("Loader            : NeoForge")
        println()
    }
}

tasks.register<GradleBuild>("buildNeoForge1215") {
    group = "Useful Ores"
    description = "Build Useful Ores for NeoForge Minecraft 1.21.5."
    dir = file("neoforge/1.21.5")
    tasks = listOf("build")
    doFirst {
        printTarget(Target("neoforge", "1.21.5", dir))
    }
    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : 1.21.5")
        println("Loader            : NeoForge")
        println()
    }
}

tasks.register<GradleBuild>("buildNeoForge1216") {
    group = "Useful Ores"
    description = "Build Useful Ores for NeoForge Minecraft 1.21.6."
    dir = file("neoforge/1.21.6")
    tasks = listOf("build")
    doFirst {
        printTarget(Target("neoforge", "1.21.6", dir))
    }
    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : 1.21.6")
        println("Loader            : NeoForge")
        println()
    }
}

tasks.register<GradleBuild>("buildNeoForge12171218") {
    group = "Useful Ores"
    description = "Build Useful Ores for NeoForge Minecraft 1.21.7-1.21.8."
    dir = file("neoforge/1.21.7-1.21.8")
    tasks = listOf("build")
    doFirst {
        printTarget(Target("neoforge", "1.21.7-1.21.8", dir))
    }
    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : 1.21.7-1.21.8")
        println("Loader            : NeoForge")
        println()
    }
}

tasks.register<GradleBuild>("buildNeoForge12110") {
    group = "Useful Ores"
    description = "Build Useful Ores for NeoForge Minecraft 1.21.10."
    dir = file("neoforge/1.21.10")
    tasks = listOf("build")
    doFirst {
        printTarget(Target("neoforge", "1.21.10", dir))
    }
    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : 1.21.10")
        println("Loader            : NeoForge")
        println()
    }
}

tasks.register<GradleBuild>("buildNeoForge12111") {
    group = "Useful Ores"
    description = "Build Useful Ores for NeoForge Minecraft 1.21.11."
    dir = file("neoforge/1.21.11")
    tasks = listOf("build")
    doFirst {
        printTarget(Target("neoforge", "1.21.11", dir))
    }
    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : 1.21.11")
        println("Loader            : NeoForge")
        println()
    }
}

tasks.register<GradleBuild>("buildNeoForge2612") {
    group = "Useful Ores"
    description = "Build Useful Ores for NeoForge Minecraft 26.1.2."
    dir = file("neoforge/26.1.2")
    tasks = listOf("build")
    doFirst {
        printTarget(Target("neoforge", "26.1.2", dir))
    }
    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : 26.1.2")
        println("Loader            : NeoForge")
        println()
    }
}

tasks.register<GradleBuild>("buildNeoForge262") {
    group = "Useful Ores"
    description = "Build Useful Ores for NeoForge Minecraft 26.2."
    dir = file("neoforge/26.2")
    tasks = listOf("build")
    doFirst {
        printTarget(Target("neoforge", "26.2", dir))
    }
    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : 26.2")
        println("Loader            : NeoForge")
        println()
    }
}

tasks.register<GradleBuild>("buildNeoForge263") {
    group = "Useful Ores"
    description = "Build Useful Ores for NeoForge Minecraft 26.3."
    dir = file("neoforge/26.3")
    tasks = listOf("build")
    doFirst {
        printTarget(Target("neoforge", "26.3", dir))
    }
    doLast {
        println()
        println("BUILD FINISHED")
        println("Minecraft version : 26.3")
        println("Loader            : NeoForge")
        println()
    }
}
