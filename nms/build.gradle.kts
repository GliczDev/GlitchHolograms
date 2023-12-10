plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.5.8" apply false
}

subprojects {
    plugins.apply("java")
    plugins.apply("io.papermc.paperweight.userdev")

    repositories {
        mavenCentral()
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")
    compileOnly(project(":api"))
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    subprojects.forEach { compileOnly(it) }
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
}