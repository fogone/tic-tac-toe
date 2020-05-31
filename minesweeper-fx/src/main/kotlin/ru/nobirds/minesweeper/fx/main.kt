package ru.nobirds.minesweeper.fx

import tornadofx.App
import tornadofx.launch

class GameApplication() : App(GameConfigurationView::class, Styles::class)

fun main(args: Array<String>) {
    launch<GameApplication>(args)
}
