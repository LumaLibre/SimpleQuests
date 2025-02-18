import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
    kotlin("jvm") version "2.0.0"
    id("com.gradleup.shadow") version "8.3.5"
}

group = "dev.jsinco.simplequests"
version = "1.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.jsinco.dev/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("org.black_ixx:playerpoints:3.2.5")
    compileOnly("me.clip:placeholderapi:2.11.6")
    implementation("dev.jsinco.abstractjavafilelib:AbstractJavaFileLib:2.4")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains:annotations:24.0.0")
}

kotlin {
    jvmToolchain(21)
}
java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
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
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
        }
        archiveClassifier.set("")
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