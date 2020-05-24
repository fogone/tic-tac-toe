package ru.nobirds.tictactoe.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import ru.nobirds.tictactoe.*
import ru.nobirds.utils.*
import javax.swing.JPanel

class GameFieldPanel() : JPanel() {

    val turnWaiter = TurnEventWaiter()

    private var endGameListener: (Turn) -> Unit = {}

    private var cells: Matrix<CellLabel>? = null

    fun newGame(game: TicTacToeGame, endGameListener: (Turn) -> Unit) {
        this.endGameListener = endGameListener
        updateComponents(game)
    }

    private fun updateComponents(value: TicTacToeGame) {
        updateCellComponents(value.field)
        GlobalScope.launch {
            val last = value.run().onEach { turn(it) }.toList().last()
            endGameListener(last)
        }
    }

    private fun turn(turn: Turn) {
        cells?.get(turn.point)?.cellType = turn.cellType
    }

    private fun updateCellComponents(gameField: GameField) {
        replaceComponents {
            gridLayout(gameField.size.x, gameField.size.y)

            cells = gameField.mapIndexed { x, y, cellType ->
                createCellLabel(Point(x, y), cellType)
            }

            cells?.forEachIndexed { x, y, cell -> add(cell) }
        }
    }

    private fun createCellLabel(position: Point, it: CellType): CellLabel {
        return CellLabel(position).apply {
            cellType = it
            selectHandler = { point ->
                GlobalScope.launch(Dispatchers.Swing) {
                    turnWaiter.channel.send(point)
                }
            }
        }
    }

    class TurnEventWaiter : EventWaiter {
        val channel = Channel<Point>()
        override suspend fun waitForPoint(): Point {
            return channel.receive()
        }
    }

}
