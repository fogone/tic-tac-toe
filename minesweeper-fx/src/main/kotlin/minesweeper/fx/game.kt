package ru.nobirds.minesweeper.fx

import javafx.application.Platform
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.WindowEvent
import javafx.util.Duration
import ru.nobirds.utils.rows
import tornadofx.*
import java.util.*

class GameView() : View() {

    private val model by inject<Game>()
    private val settings by inject<GameConfigurationModel>()

    private lateinit var fieldContainer: HBox

    init {
        model.gameModelProperty.onNonNullChange { newValue ->
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
            hgrow = Priority.ALWAYS

            label(model.gameModelProperty.flatMap { minesLeftProperty })

            button("New Game") {
                action {
                    switchToSettings(false)
                }
            }

            label(model.timerProperty.stringBinding { "$it sec" })
        }

        fieldContainer = hbox {
        }
    }

    private fun switchToSettings(gameOver: Boolean) {
        if (gameOver) {
            settings.started = false
        } else {
            model.gameModel.state = GameState.PAUSE
        }

        replaceWith<GameConfigurationView>(
            transition = ViewTransition.Flip(Duration.seconds(1.0)),
            sizeToScene = true,
            centerOnScreen = true
        )
    }

    private fun GameModel.initializeGameState() {
        fieldContainer.createGameField(this)
        winnerProperty.onState {
            Platform.runLater {
                information("Game Over!", "You are winner.",
                    ButtonType.OK, owner = primaryStage, title = "Game Over") {
                    switchToSettings(true)
                }
            }
        }
        looserProperty.onState {
            Platform.runLater {
                error("Game Over!", "You are looser.",
                    ButtonType.OK, owner = primaryStage, title = "Game Over") {
                    switchToSettings(true)
                }
            }
        }
    }

    private fun EventTarget.createGameField(model: GameModel) {
        replaceChildren {
            gridpane {
                vgrow = Priority.ALWAYS
                hgrow = Priority.ALWAYS

                for (row in model.field.rows) {
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
        addClass(CommonStylesheet.cell)

        textProperty().bind(stringBinding(cell, model.gameModel.gameOverProperty) {
            when {
                checked || (model.gameModel.gameOver && mine) || (opened && mine) -> "*"
                opened -> if (minesAroundNumber > 0) minesAroundNumber.toString() else " "
                else -> " "
            }
        })

        cell.checkedProperty.onChange { newValue ->
            if (newValue == true) addClass(CommonStylesheet.cellChecked)
            else removeClass(CommonStylesheet.cellChecked)
        }

        cell.openedProperty.onChange { newValue ->
            if (newValue == true) {
                addClass(CommonStylesheet.cellOpened)
                style {
                    textFill = getTextColorByMinesAround(cell.minesAroundNumber)
                }
            } else {
                removeClass(CommonStylesheet.cellOpened)
            }
        }

        setPrefSize(30.0, 30.0)

        addEventFilter(MouseEvent.MOUSE_CLICKED) {
            when (it.button) {
                MouseButton.PRIMARY -> if (cell.opened) {
                    model.gameModel.openUnchecked(cell.position)
                } else {
                    if (!cell.checked)
                        model.gameModel.open(cell.position)
                }
                MouseButton.SECONDARY -> model.gameModel.check(cell)
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
        if (model.gameModel.state == GameState.GAME)
            model.gameModel.state = GameState.PAUSE
    }

}

class Game() : ViewModel() {

    val gameModelProperty: ObjectProperty<GameModel> = objectProperty<GameModel>().apply {
        onChange {
            if (it != null) {
                secondsCounter.property.value = 0
            }
        }
    }

    var gameModel: GameModel by gameModelProperty

    private val mutableTimerProperty = 0.toProperty()

    val secondsCounter = SecondsCounter().apply {
        mutableTimerProperty.bind(property)
        startedProperty.bind(gameModelProperty.flatMap { startedProperty })
    }

    val timerProperty: ReadOnlyProperty<Number> get() = mutableTimerProperty

}

class SecondsCounter() {

    val property: IntegerProperty = 0.toProperty()

    val startedProperty = false.toProperty {
        onChange {
            if (it) {
                start()
            } else {
                stop()
            }
        }
    }

    var started by startedProperty

    private var timerTask: FXTimerTask? = null

    private fun createTask(): FXTimerTask {
        return FXTimerTask({
            property.value += 1
        }, Timer()).apply {
            timer.schedule(this, 0L, 1000L)
        }
    }

    private fun stop() {
        timerTask?.timer?.cancel()
        timerTask = null
    }

    private fun start() {
        timerTask = createTask()
    }

}
