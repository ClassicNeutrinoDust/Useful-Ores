plugins {
    java
    id("net.neoforged.moddev") version "2.0.+"
}

val modId = "useful_ores"
val modVersion = project.findProperty("mod_version") as String? ?: "2.1.8"
val neoForgeVersion = project.findProperty("neoforge_version") as String? ?: "26.3.0.51-beta"
val minecraftVersion = project.findProperty("minecraft_version") as String? ?: "26.3"

group = "com.neutrinodust"
version = modVersion

base {
    archivesName.set("$modId-neoforge-mc$minecraftVersion")
}

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

neoForge {
    version = neoForgeVersion

    parchment {
                                                                                      
                                                                   
                                    
                                                           
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
                                                                                          
                                                                                         
                                                                                         
                                                                                        
      
                                                                               
                                                                                    
                                                                                    
                                                                               
                                                                                 
                                                                             
                                                                          
    implementation(fileTree("libs") { include("geckolib-neoforge-*.jar") })

                                                                          
                                                                          
                                                                            
                                                                          
                                                                           
                                                                        
                                                                          
                                                                            
                                                                           
                                  
                                                                        
                                                                           
                                                                       
                                                                     
                                                                             
                                                                         
                                                       
    compileOnly(files("libs/sodium-inner-neoforge-0_8_12_2Bmc26_1_2.jar"))
}
