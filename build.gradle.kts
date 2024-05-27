import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
    kotlin("jvm") version "2.0.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.jsinco.simplequests"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    implementation("com.github.Jsinco:AbstractJavaFileLib:1.4")
    implementation(kotlin("stdlib-jdk8"))
}
kotlin {
    jvmToolchain(17)
}

tasks {
    processResources {
        outputs.upToDateWhen { false }
        filter<ReplaceTokens>(mapOf(
            "tokens" to mapOf("version" to project.version.toString()),
            "beginToken" to "\${",
            "endToken" to "}"
        ))
    }

    shadowJar {
        dependencies {
            include(dependency("com.github.Jsinco:AbstractJavaFileLib"))
        }
        //archiveClassifier.set("")
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    jar {
        enabled = false
    }

    build {
        dependsOn(shadowJar)
    }
}