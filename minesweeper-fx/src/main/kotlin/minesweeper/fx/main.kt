package ru.nobirds.minesweeper.fx

import tornadofx.*

fun main(args: Array<String>) {
    launch<GameApplication>(args)
}

class GameApplication() : App(GameConfigurationView::class, CommonStylesheet::class)

class CommonStylesheet() : Stylesheet() {
    val checked = cssclass("checked")
    val closed = cssclass("closed")
    val opened = cssclass("opened")

    init {

    }
}


