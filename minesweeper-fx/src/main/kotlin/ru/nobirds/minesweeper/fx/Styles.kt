package ru.nobirds.minesweeper.fx

import javafx.scene.effect.DropShadow
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*
import java.net.URI

class Styles : Stylesheet() {

    companion object {
        val cell by cssclass("game-cell")
        val cellChecked by cssclass("cell-checked")
        val cellOpened by cssclass("cell-opened")
        val labledText by cssclass("labled-text")

        val openedColor = c("#eeeeee")
        val closedColor = c("#dddddd")
        val checkColor = c("#660000")
    }

    init {
        cell {
            fontFamily = "Monospace"
            borderColor += box(Color.WHITE)
            borderWidth += tornadofx.box(1.px)
            fontSize = 15.px
            backgroundColor += closedColor
            effect = DropShadow(
                1.0,
                Color.BLACK
            )
            fontWeight = FontWeight.BOLD
        }
        cellChecked {
            textFill = checkColor
        }
        cellOpened {
            backgroundColor += openedColor
            borderStyle += BorderStrokeStyle.NONE
            borderWidth += box(0.px)
            borderRadius += box(0.px)
        }
        labledText {
            borderWidth += box(1.px)
            borderRadius += box(3.px)
            borderColor += box(Color.BLACK)
            padding = box(4.px)
            fontSize = 17.px
        }
    }
}
