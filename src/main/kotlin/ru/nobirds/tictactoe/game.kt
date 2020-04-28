package ru.nobirds.tictactoe

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import ru.nobirds.utils.*

data class Turn(val cellType: CellType, val point: Point, val winner: Boolean)

class TicTacToeGame(
        private val gameField: MutableGameField,
        val inRow: Int,
        private val player1: Player,
        private val player2: Player,
        private val winnerAlgorithm: WinnerAlgorithm
) {

    val field: GameField get() = gameField

    private var gameOver = false
    private val winner = mutableListOf<SearchResult>()
    private var nextPlayer = player1

    fun reset() {
        winner.clear()
        nextPlayer = player1
        gameOver = false
        gameField.setEach { _, _ -> CellType.EMPTY }
    }

    fun run() = flow<Turn> {
        do {
            processTurn()
        } while (!gameOver)

        if (winner.isNotEmpty()) {
            println("The winner is: ${winner.map { it.cellType }}")
        }
    }

    private suspend fun FlowCollector<Turn>.processTurn() {
        with(nextPlayer) {
            val turn = processTurn()
            gameField[turn] = cellType
            val isWinner = checkWinner()
            emit(Turn(nextPlayer.cellType, turn, isWinner))
        }

        checkEmptyCells()
        swapPlayers()

        println("$gameField")
    }

    private fun checkEmptyCells() {
        if(gameField.positions().filter { gameField.get(it).isEmpty }.none()) {
            gameOver()
        }
    }

    private fun gameOver() {
        gameOver = true
    }

    private fun checkWinner(): Boolean {
        with(winnerAlgorithm) {
            val winner = gameField.findWinner(inRow)
            if (winner.isNotEmpty()) {
                gameOver()
                this@TicTacToeGame.winner.addAll(winner)
                return true
            }
        }
        return false
    }

    private suspend fun Player.processTurn(): Point {
        return flow {
            emit(gameField.nextTurn(inRow))
        }.first { gameField.checkTurn(it) }
    }

    private fun <T> Sequence<T>.takeWhileIncluding(condition: (T) -> Boolean): Sequence<T> = sequence {
        for (item in this@takeWhileIncluding) {
            yield(item)
            if(!condition(item))
                break
        }
    }

    private fun Matrix<CellType>.checkTurn(point: Point): Boolean {
        return point in this && get(point).isEmpty
    }

    private fun swapPlayers() {
        nextPlayer = if(nextPlayer === player1) player2 else player1
    }

}


fun createDefaultGame(size: Int, inRow: Int,
                      playerFactory: (CellType) -> Player = { createDefaultAiPlayer(it) }): TicTacToeGame =
        TicTacToeGame(
                mutableMatrixOf(size, size) { _, _ -> CellType.EMPTY },
                inRow,
                playerFactory(CellType.CROSS),
                playerFactory(CellType.ZERO),
                SimpleWinnerAlgorithm()
        )

fun createDefaultAiPlayer(cellType: CellType): AiPlayer {
    return AiPlayer(
            cellType,
            NaiveAlertSearchStrategy,
            FirstChooseAlertStrategy,
            RandomPreventAlertStrategy,
            RandomAttackStrategy
    )
}
