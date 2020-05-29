package ru.nobirds.minesweeper.fx

import javafx.beans.property.IntegerProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import ru.nobirds.utils.Matrix
import ru.nobirds.utils.xIndices
import ru.nobirds.utils.yIndices
import tornadofx.*
import java.util.*

class GameView() : View() {

    private val model by inject<Game>()

    override val root: Parent = vbox(10) {
        hbox(10, Pos.CENTER) {
            hgrow = Priority.ALWAYS

            label(model.minesLeftProperty)

            button("New Game") {
                action {
                    // todo
                }
            }

            label(model.timerProperty.stringBinding { "$it sec" })
        }

        hbox {
            createGameField().apply {
                vgrow = Priority.ALWAYS
                hgrow = Priority.ALWAYS
            }
        }
    }

    private fun EventTarget.createGameField(): GridPane = gridpane {
        for (row in model.gameModel.field.rows) {
            row {
                for (cell in row) {
                    button {
                        initialize(cell)
                    }
                }
            }
        }
    }

    private fun Button.initialize(cell: FieldCell) {
        textProperty().bind(stringBinding(cell.openedProperty, cell.checkedProperty) {
            when {
                cell.opened -> if (cell.minesAroundNumber > 0) cell.minesAroundNumber.toString() else ""
                cell.checked -> "*"
                else -> " "
            }
        })

        backgroundProperty().bind(cell.openedProperty.map {
            if (it) Background(BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY))
            else Background(BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY))
        })

        border = Border(BorderStroke(Color.WHITE, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, BorderWidths.EMPTY))

        setPrefSize(30.0, 30.0)

        addEventFilter(MouseEvent.MOUSE_CLICKED) {
            when (it.button) {
                MouseButton.PRIMARY -> if (cell.opened)
                    model.gameModel.openUnchecked(cell.position) else model.gameModel.open(cell.position)
                MouseButton.SECONDARY -> model.gameModel.check(cell)
            }
        }
    }

}

class Game() : ViewModel() {
    private val gameConfigurationModel by inject<GameConfigurationModel>()

    val gameStateProperty = GameState.INIT.toProperty()

    val gameModel: GameModel = GameModel(
        gameConfigurationModel.width,
        gameConfigurationModel.height,
        gameConfigurationModel.minesNumber
    )

    private val mutableTimerProperty = intProperty()

    private val secondsCounter = SecondsCounter(mutableTimerProperty)
    val timerProperty: ReadOnlyProperty<Number> get() = mutableTimerProperty

    val gameOverProperty = gameStateProperty.booleanBinding {
        it == GameState.LOOSER || it == GameState.WINNER
    }

    val minesLeftProperty = observable(gameModel, GameModel::minesLeft)

    fun dispose() {
        secondsCounter.stop()
    }

}

class SecondsCounter(private val property: IntegerProperty) {

    private val timerTask = FXTimerTask({
        property.value += 1
    }, Timer()).apply {
        timer.schedule(this, 0L, 1000L)
    }

    fun stop() {
        timerTask.timer.cancel()
    }

}
