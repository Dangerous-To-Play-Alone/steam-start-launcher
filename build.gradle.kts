import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.serialization") version "1.5.21"
    id("org.jetbrains.compose") version "1.0.0-alpha3"
}

group = "me.evan"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("io.ktor:ktor-client-core:1.6.5")
    implementation("io.ktor:ktor-client-logging:1.6.5")
    implementation("io.ktor:ktor-client-java:1.6.5")
    implementation("io.ktor:ktor-client-serialization:1.6.5")
    implementation("io.ktor:ktor-client-json:1.6.5")
    implementation("com.alialbaali.kamel:kamel-image:0.2.2")

    implementation("com.github.vatbub:mslinks:1.0.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.1")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "steam-start-launcher"
            packageVersion = "1.0.0"
        }
    }

}