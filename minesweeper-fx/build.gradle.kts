plugins {
    `kotlin-module`
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.8"
}

javafx {
    version = "11.0.2"
    modules("javafx.controls", "javafx.fxml")
}

application {
    mainClassName = "ru.nobirds.minesweeper.fx.MainKt"
}

dependencies {
    implementation("no.tornado:tornadofx:1.7.20")
    implementation("org.xerial:sqlite-jdbc:3.31.1")
    implementation(project(":utils"))
}

