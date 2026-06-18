import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

group = "br.ufms"
version = "1.0-SNAPSHOT"

// O build roda e empacota com o JDK 26 (toolchain), mas o bytecode gerado
// tem alvo 24, que e o maior alvo suportado pelo compilador Kotlin atual.
// Java e Kotlin sao mantidos no mesmo alvo para evitar inconsistencia.
val jvmTargetVersion = 24

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    sourceCompatibility = JavaVersion.toVersion(jvmTargetVersion)
    targetCompatibility = JavaVersion.toVersion(jvmTargetVersion)
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(jvmTargetVersion.toString()))
    }
}

val launcher25 = javaToolchains.launcherFor {
    languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.withType<JavaExec>().configureEach {
    javaLauncher.set(launcher25)
}

tasks.withType<Test>().configureEach {
    javaLauncher.set(launcher25)
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    api(compose.materialIconsExtended)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "gerenciador-de-memoria"
            packageVersion = "1.0.0"
        }
    }
}