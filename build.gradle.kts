plugins {
    kotlin("jvm") version "2.0.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "kr.kjh9211"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    implementation(kotlin("stdlib"))
    implementation("org.xerial:sqlite-jdbc:3.47.1.0")
}

kotlin {
    jvmToolchain(17)
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        // sqlite 클래스를 다른 플러그인과 충돌하지 않도록 relocate
        relocate("org.sqlite", "kr.kjh9211.money.libs.sqlite")
    }
    build {
        dependsOn(shadowJar)
    }
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
}
