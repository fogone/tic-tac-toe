package ru.nobirds.minesweeper.fx

import javafx.util.Duration
import tornadofx.*

class GameConfigurationModel() : ViewModel() {

    private val gameView = find<GameView>()
    private val game by inject<GameModel>()

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
        game.gameField = GameField(width, height, minesNumber)
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
        game.gameField?.state = GameState.GAME
    }

}

