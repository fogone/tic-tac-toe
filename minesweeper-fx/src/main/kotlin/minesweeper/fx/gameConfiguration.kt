package ru.nobirds.minesweeper.fx

import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.util.Duration
import tornadofx.*

class GameConfigurationModel() : ViewModel() {

    private val gameView = find<GameView>()
    private val game by inject<Game>()

    val widthProperty = 9.toProperty()
    val heightProperty = 9.toProperty()
    val minesNumberProperty = 10.toProperty()

    var width by widthProperty
    var height by heightProperty
    var minesNumber by minesNumberProperty

    val startedProperty = false.toProperty()

    var started by startedProperty

    fun setValues(width: Int, height: Int, mines: Int) {
        this.width = width
        this.height = height
        this.minesNumber = mines
    }

    fun startNewGame(view: View) {
        game.gameModel = GameModel(width, height, minesNumber)
        view.showGame()
        started = true
    }

    fun backToGame(view: View) {
        view.showGame()
    }

    private fun View.showGame() {
        replaceWith(
            transition = ViewTransition.Flip(Duration.seconds(1.0)),
            sizeToScene = true,
            centerOnScreen = true,
            replacement = gameView
        )
        game.gameModel.state = GameState.GAME
    }

}

class GameConfigurationView() : View() {

    private val model by inject<GameConfigurationModel>()

    override val root: Parent = form {
        buttonbar {
            button("Beginner") {
                action {
                    model.setValues(9, 9, 10)
                }
                style {
                    backgroundColor = multi(Color.GREEN)
                    textFill = Color.WHITE
                }
            }
            button("Intermediate") {
                action {
                    model.setValues(16, 16, 40)
                }
                style {
                    backgroundColor = multi(Color.BLUE)
                    textFill = Color.WHITE
                }
            }
            button("Expert") {
                action {
                    model.setValues(16, 31, 99)
                }
                style {
                    backgroundColor = multi(Color.RED)
                    textFill = Color.WHITE
                }
            }
        }

        fieldset("Configuration") {
            field("Width") {
                slider(7, 50, 9, Orientation.HORIZONTAL) {
                    hgrow = Priority.ALWAYS
                    valueProperty().bindBidirectional(model.widthProperty)
                    isShowTickLabels = true
                    isShowTickMarks = true
                }
                label(model.widthProperty)
            }
            field("Height") {
                slider(7, 50, 9, Orientation.HORIZONTAL) {
                    hgrow = Priority.ALWAYS
                    valueProperty().bindBidirectional(model.heightProperty)
                    isShowTickLabels = true
                    isShowTickMarks = true
                }
                label(model.heightProperty)
            }
            field("Mines number") {
                spinner(true, model.minesNumberProperty, true) {
                    hgrow = Priority.ALWAYS
                    valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(
                        7,
                        1000,
                        10
                    ) as SpinnerValueFactory<Number>
                }
            }
        }

        buttonbar {
            button("Back to game") {
                disableProperty().bind(model.startedProperty.not())
                action {
                    model.backToGame(this@GameConfigurationView)
                }
            }
            button("Start") {
                action {
                    model.startNewGame(this@GameConfigurationView)
                }
            }
        }
    }
}
