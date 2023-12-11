import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    maven("https://libraries.minecraft.net")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

val nms = project(":nms")

val reobf: Configuration by configurations.creating
val mojMap: Configuration by configurations.creating

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")
    compileOnly("com.mojang:brigadier:1.1.8")
    compileOnly("me.clip:placeholderapi:2.11.3")
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    implementation(project(":api"))
    implementation("dev.jorel:commandapi-bukkit-shade:9.3.0")

    implementation(nms)
    nms.subprojects.forEach {
        reobf(project(":nms:${it.name}", "reobf"))
        mojMap(it)
    }
}

tasks {
    withType<ShadowJar> {
        group = "shadow"

        relocate("dev.jorel.commandapi", "me.glicz.holograms.libs.commandapi")

        from(sourceSets.main.get().output)
        from(sourceSets.main.get().runtimeClasspath)

        archiveBaseName = rootProject.name
    }
}

task<ShadowJar>("shadowJarReobf") {
    from(reobf)

    archiveClassifier = null
}

task<ShadowJar>("shadowJarMojMap") {
    from(mojMap)

    archiveClassifier = "mojmap"
}

bukkit {
    main = "me.glicz.holograms.GlitchHolograms"
    name = rootProject.name
    author = "Glicz"
    apiVersion = "1.20"
    softDepend = listOf("PlaceholderAPI")
}