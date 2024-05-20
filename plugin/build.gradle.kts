plugins {
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("io.github.goooler.shadow") version "8.1.7"
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
    compileOnly("org.spongepowered:configurate-yaml:4.2.0-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.3")
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    implementation(project(":api"))
    implementation(project(":nms"))
}

tasks {
    shadowJar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang+yarn"
        }

        archiveBaseName = rootProject.name
        archiveClassifier = null
    }
}

bukkit {
    main = "me.glicz.holograms.GlitchHolograms"
    name = rootProject.name
    author = "Glicz"
    apiVersion = "1.20"
    softDepend = listOf("PlaceholderAPI")
}