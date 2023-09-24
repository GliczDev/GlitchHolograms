dependencies {
    paperweight.paperDevBundle("1.20-R0.1-SNAPSHOT")
    compileOnly(project(":api"))
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