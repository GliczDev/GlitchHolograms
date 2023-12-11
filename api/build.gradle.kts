plugins {
    id("maven-publish")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("publishMaven") {
            from(components["java"])
        }
    }
}