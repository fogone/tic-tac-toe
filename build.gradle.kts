plugins {
    kotlin("jvm") version "1.3.71"
}

group = "ru.nobirds"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.3.5")
    testImplementation("junit:junit:4.13")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
