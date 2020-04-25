package ru.nobirds.tictactoe.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import ru.nobirds.tictactoe.CellType
import ru.nobirds.tictactoe.GameField
import ru.nobirds.tictactoe.Turn
import ru.nobirds.utils.*
import javax.swing.JPanel

class GameFieldPanel(private val turns: Channel<Point>) : JPanel() {

    var gameField: GameField? = null
        set(value) {
            field = value
            if (value != null) {
                updateComponents(value)
            } else {
                cleanComponents()
            }
        }

    private var cells: Matrix<CellLabel>? = null

    private fun cleanComponents() {
        replaceComponents {}
    }

    private fun updateComponents(gameField: GameField) {
        replaceComponents {
            gridLayout(gameField.size.x, gameField.size.y)
            cells = gameField.mapIndexed { x, y, cellType -> createCellLabel(
                Point(
                    x,
                    y
                ), cellType) }
            cells?.forEachIndexed { x, y, cell -> add(cell) }
        }
    }

    private fun createCellLabel(position: Point, it: CellType): CellLabel {
        return CellLabel(position).apply {
            cellType = it
            selectHandler = { point ->
                GlobalScope.launch(Dispatchers.Swing) { turns.send(point) }
            }
        }
    }

    fun turn(turn: Turn) {
        cells?.get(turn.point)?.cellType = turn.cellType
    }

}
