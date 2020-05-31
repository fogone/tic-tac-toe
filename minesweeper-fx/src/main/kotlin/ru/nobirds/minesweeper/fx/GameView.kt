package ru.nobirds.minesweeper.fx

import javafx.application.Platform
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.stage.WindowEvent
import javafx.util.Duration
import ru.nobirds.utils.rows
import tornadofx.*

class GameView() : View() {

    private val model by inject<GameModel>()
    private val settings by inject<GameConfigurationModel>()

    private lateinit var fieldContainer: HBox

    init {
        model.gameFieldProperty.onNonNullChange { newValue ->
            newValue.initializeGameState()
        }

        primaryStage.sceneProperty().onNonNullChange {
            it.window.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST) {
                switchToPauseMode()
            }
        }
    }

    override val root: Parent = vbox(10) {
        hbox(10, Pos.CENTER) {
            label(model.gameFieldProperty.flatMap { minesLeftProperty }) {
                hgrow = Priority.ALWAYS
                alignment = Pos.CENTER
                addClass(Styles.labledText)
            }

            button("New Game") {
                action {
                    switchToSettings(false)
                }
            }

            label(model.timerProperty.stringBinding { "$it sec" }) {
                hgrow = Priority.ALWAYS
                alignment = Pos.CENTER
                addClass(Styles.labledText)
            }

            style {
                padding = box(horizontal = 10.px, vertical = 10.px)
            }
        }

        fieldContainer = hbox {
        }
    }

    private fun switchToSettings(gameOver: Boolean) {
        if (gameOver) {
            settings.started = false
        }

        switchToPauseMode()

        replaceWith<GameConfigurationView>(
            transition = ViewTransition.Flip(
                Duration.seconds(
                    1.0
                )
            ),
            sizeToScene = true,
            centerOnScreen = true
        )
    }

    private fun GameField.initializeGameState() {
        fieldContainer.createGameField(this)
        winnerProperty.onState {
            Platform.runLater {
                information(
                    "Game Over!", "You are winner.",
                    ButtonType.OK, owner = primaryStage, title = "Game Over"
                ) {
                    switchToSettings(true)
                }
            }
        }
        looserProperty.onState {
            Platform.runLater {
                tornadofx.error(
                    "Game Over!", "You are looser.",
                    ButtonType.OK, owner = primaryStage, title = "Game Over"
                ) {
                    switchToSettings(true)
                }
            }
        }
    }

    private fun EventTarget.createGameField(gameField: GameField) {
        replaceChildren {
            gridpane {
                vgrow = Priority.ALWAYS
                hgrow = Priority.ALWAYS

                for (row in gameField.field.rows) {
                    row {
                        for (cell in row) {
                            button {
                                initialize(cell)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun Button.initialize(cell: FieldCell) {
        addClass(Styles.cell)

        textProperty().bind(stringBinding(cell, model.gameField!!.gameOverProperty) {
            when {
                checked || (model.gameField?.gameOver == true && mine) || (opened && mine) -> "*"
                opened -> if (minesAroundNumber > 0) minesAroundNumber.toString() else " "
                else -> " "
            }
        })

        cell.checkedProperty.onChange { newValue ->
            if (newValue == true) addClass(Styles.cellChecked)
            else removeClass(Styles.cellChecked)
        }

        cell.openedProperty.onChange { newValue ->
            if (newValue == true) {
                addClass(Styles.cellOpened)
                style {
                    textFill = getTextColorByMinesAround(cell.minesAroundNumber)
                }
            } else {
                removeClass(Styles.cellOpened)
            }
        }

        setPrefSize(30.0, 30.0)

        addEventFilter(MouseEvent.MOUSE_CLICKED) {
            when (it.button) {
                MouseButton.PRIMARY -> if (cell.opened) {
                    model.gameField?.openUnchecked(cell.position)
                } else {
                    if (!cell.checked)
                        model.gameField?.open(cell.position)
                }
                MouseButton.SECONDARY -> model.gameField?.check(cell)
            }
        }
    }

    private fun getTextColorByMinesAround(minesAroundNumber: Int): Color {
        return when (minesAroundNumber) {
            0 -> Color.BLACK
            1 -> Color.BLUE
            2 -> Color.GREEN
            3 -> Color.RED
            4 -> color(0, 76, 153)
            5 -> color(102, 0, 0)
            6 -> color(255, 128, 0)
            7 -> color(0, 51, 25)
            8 -> color(132, 33, 86)
            else -> kotlin.error("Unsupported")
        }
    }

    override fun onUndock() {
        switchToPauseMode()
    }

    private fun switchToPauseMode() {
        if (model.gameField?.state == GameState.GAME)
            model.gameField?.state = GameState.PAUSE
    }

}
