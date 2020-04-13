package ru.nobirds.tictactoe

import ru.nobirds.utils.*

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

    fun run(): CellType {
        do {
            with(nextPlayer) {
                val turn = processTurn()
                gameField[turn] = cellType
            }

            checkWinner()

            if(!gameOver) {
                checkEmptyCells()
                swapPlayers()
            }

            println("$gameField")
        } while (!gameOver)

        if (winner.isNotEmpty()) {
            println("The winner is: ${winner.map { it.cellType }}")
            return winner.map { it.cellType }.first()
        }

        return CellType.EMPTY
    }

    private fun checkEmptyCells() {
        if(gameField.positions().filter { gameField.get(it).isEmpty }.none()) {
            gameOver()
        }
    }

    private fun gameOver() {
        gameOver = true
    }

    private fun checkWinner() {
        with(winnerAlgorithm) {
            val winner = gameField.findWinner(inRow)
            if (winner.isNotEmpty()) {
                gameOver()
                this@TicTacToeGame.winner.addAll(winner)
            }
        }
    }

    private fun Player.processTurn(): Point {
        return generateSequence { gameField.nextTurn(inRow) }
            .takeWhileIncluding { !gameField.checkTurn(it) }.first()
    }

    private fun <T> Sequence<T>.takeWhileIncluding(condition: (T) -> Boolean): Sequence<T> = sequence {
        for (item in this@takeWhileIncluding) {
            yield(item)
            if(!condition(item))
                break
        }
    }

    private fun GameField.checkTurn(point: Point): Boolean {
        return point in this && get(point).isEmpty
    }

    private fun swapPlayers() {
        nextPlayer = if(nextPlayer === player1) player2 else player1
    }

}
