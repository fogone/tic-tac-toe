package ru.nobirds.minesweeper.fx

import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

fun main(args: Array<String>) {
    launch<GameApplication>(args)
}

class GameApplication() : App(GameConfigurationView::class, CommonStylesheet::class)

class CommonStylesheet() : Stylesheet() {
    companion object {
        val cell by cssclass("cell")
        val cellChecked by cssclass("cell-checked")
        val cellOpened by cssclass("cell-opened")

        val openedColor = c("#eeeeee")
        val closedColor = c("#dddddd")
        val checkColor = c("#660000")
    }

    init {
        cell {
            fontFamily = "Monospace"
            borderColor += box(Color.WHITE)
            borderWidth += box(1.px)
            fontSize = 15.px
            backgroundColor += closedColor
            effect = DropShadow(1.0, Color.BLACK)
            fontWeight = FontWeight.BOLD
        }
        cellChecked {
            textFill = checkColor
        }
        cellOpened {
            backgroundColor += openedColor
        }
    }
}
