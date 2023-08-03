plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    implementation(project(":api"))
    implementation(project(":nms", "shadow"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
        dependsOn(clean)
    }

    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set(rootProject.name)
    }
}

bukkit {
    main = "me.glicz.holograms.GlitchHolograms"
    name = rootProject.name
    author = "Glicz"
    apiVersion = "1.20"
}