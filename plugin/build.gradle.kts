plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    implementation(project(":api"))
    implementation(project(":nms", "shadow"))
    implementation("dev.jorel:commandapi-bukkit-shade:9.0.4-SNAPSHOT")
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
        relocate("dev.jorel.commandapi", "me.glicz.holograms.libs.commandapi")

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