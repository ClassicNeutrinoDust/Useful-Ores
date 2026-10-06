plugins {
    id("net.fabricmc.fabric-loom") version "1.17.+"
    id("java")
}

val modId = "useful_ores"
val modVersion = project.findProperty("mod_version") as String? ?: "2.1.8"
val minecraftVersion = project.findProperty("minecraft_version") as String? ?: "26.3"
val fabricLoaderVersion = project.findProperty("fabric_loader_version") as String? ?: "0.19.5"
val fabricApiVersion = project.findProperty("fabric_api_version") as String? ?: "0.161.0+26.3"

group = "com.neutrinodust"
version = modVersion

base {
    archivesName.set("$modId-fabric-mc$minecraftVersion")
}

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

repositories {
    mavenCentral()
    maven {
        name = "CurseMaven"
        url = uri("https://www.cursemaven.com")
    }
}

                                                                                    
                                                                                     
                                          
                                                                                      
                                                                                        
                                                                                        
                                                                                                    
                                                                                         
                                                                                        
                                  
dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    implementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    implementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")

                                                                                                          
    implementation(fileTree("libs") { include("*.jar") })
                                                                                                                           
    implementation("curse.maven:jei-238222:9068072")

                                                                                     
                                                                                      
                                                                             
                                                                                    
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand(mutableMapOf("version" to project.version))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(25)
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}
