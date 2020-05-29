plugins {
    `kotlin-module`
    id("application")
}

application {
    mainClassName = "ru.nobirds.minesweeper.swing.MainKt"
}

dependencies {
    implementation(project(":utils"))
}

