package ru.nobirds.minesweeper.fx

import javafx.beans.property.IntegerProperty
import javafx.beans.property.Property
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import ru.nobirds.minesweeper.GameModel
import ru.nobirds.minesweeper.GameState
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
                    button(" ") {
                        setPrefSize(30.0, 30.0)
                    }
                }
            }
        }
    }

}

class Game() : ViewModel() {
    private val gameConfigurationModel by inject<GameConfigurationModel>()

    val gameStateProperty = objectProperty(GameState.INIT)

    val gameModel: GameModel = GameModel(
        gameConfigurationModel.width,
        gameConfigurationModel.height,
        gameConfigurationModel.minesNumber,
        {
            gameStateProperty.set(it)
        },
        { position, old, new ->

        }
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

val <T> Matrix<T>.rows: Sequence<Sequence<T>>
    get() = sequence {
        xIndices.forEach { x ->
            yield(sequence {
                yIndices.forEach { y ->
                    yield(get(x, y))
                }
            })
        }
    }
