package ru.nobirds.tictactoe

import ru.nobirds.tictactoe.CellType.*
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import ru.nobirds.utils.Matrix
import ru.nobirds.utils.map
import ru.nobirds.utils.mutableMatrixOf

data class TicTacToeWinnerCase(val matrix: Matrix<CellType>, val inRow: Int, val result: CellType)

fun TicTacToeWinnerCase.inverse(): TicTacToeWinnerCase = TicTacToeWinnerCase(matrix.map { it.inverse() }, inRow, result.inverse())

@RunWith(Parameterized::class)
class WinnerTest(private val case: TicTacToeWinnerCase) {

    companion object {

        private val emptyCases = listOf(
            TicTacToeWinnerCase(
                mutableMatrixOf(
                    3,
                    CROSS, EMPTY, EMPTY,
                    ZERO, ZERO, EMPTY,
                    CROSS, EMPTY, ZERO
                ),
                3,
                EMPTY
            ),
            TicTacToeWinnerCase(
                mutableMatrixOf(
                    5,
                    CROSS, EMPTY, EMPTY, EMPTY, EMPTY,
                    ZERO, ZERO, EMPTY, EMPTY, EMPTY,
                    CROSS, EMPTY, ZERO, EMPTY, EMPTY,
                    CROSS, EMPTY, ZERO, EMPTY, EMPTY,
                    CROSS, EMPTY, ZERO, EMPTY, EMPTY
                ),
                5,
                EMPTY
            )
        )

        private val crossCases = listOf(
            TicTacToeWinnerCase(
                mutableMatrixOf(
                    3,
                    CROSS, EMPTY, EMPTY,
                    CROSS, ZERO, EMPTY,
                    CROSS, EMPTY, ZERO
                ), 3, CROSS
            ),
            TicTacToeWinnerCase(
                mutableMatrixOf(
                    3,
                    CROSS, CROSS, CROSS,
                    ZERO, ZERO, EMPTY,
                    CROSS, EMPTY, ZERO
                ), 3, CROSS
            ),
            TicTacToeWinnerCase(
                mutableMatrixOf(
                    3,
                    ZERO, ZERO, EMPTY,
                    CROSS, CROSS, CROSS,
                    CROSS, EMPTY, ZERO
                ), 3, CROSS
            ),
            TicTacToeWinnerCase(
                mutableMatrixOf(
                    3,
                    CROSS, ZERO, EMPTY,
                    ZERO, CROSS, EMPTY,
                    CROSS, EMPTY, CROSS
                ), 3, CROSS
            ),
            TicTacToeWinnerCase(
                mutableMatrixOf(
                    3,
                    ZERO, ZERO, CROSS,
                    ZERO, CROSS, EMPTY,
                    CROSS, EMPTY, EMPTY
                ), 3, CROSS
            ),
            TicTacToeWinnerCase(
                mutableMatrixOf(
                    5,
                    CROSS, EMPTY, EMPTY, EMPTY, EMPTY,
                    CROSS, ZERO, EMPTY, EMPTY, EMPTY,
                    CROSS, EMPTY, ZERO, EMPTY, EMPTY,
                    CROSS, EMPTY, ZERO, EMPTY, EMPTY,
                    CROSS, EMPTY, ZERO, EMPTY, EMPTY
                ),
                5,
                CROSS
            ),
            TicTacToeWinnerCase(
                mutableMatrixOf(
                    5,
                    EMPTY, EMPTY, EMPTY, EMPTY, CROSS,
                    EMPTY, ZERO, EMPTY, CROSS, EMPTY,
                    CROSS, EMPTY, CROSS, EMPTY, EMPTY,
                    CROSS, CROSS, ZERO, EMPTY, EMPTY,
                    CROSS, EMPTY, ZERO, EMPTY, EMPTY
                ),
                5,
                CROSS
            ),
            TicTacToeWinnerCase(
                mutableMatrixOf(
                    5,
                    CROSS, EMPTY, EMPTY, EMPTY, EMPTY,
                    ZERO, ZERO, EMPTY, EMPTY, EMPTY,
                    CROSS, EMPTY, EMPTY, EMPTY, EMPTY,
                    CROSS, EMPTY, ZERO, EMPTY, EMPTY,
                    CROSS, EMPTY, ZERO, EMPTY, EMPTY
                ),
                3,
                CROSS
            ),
            TicTacToeWinnerCase(
                mutableMatrixOf(
                    5,
                    CROSS, EMPTY, EMPTY, EMPTY, EMPTY,
                    ZERO, ZERO, CROSS, EMPTY, EMPTY,
                    ZERO, EMPTY, CROSS, EMPTY, EMPTY,
                    EMPTY, EMPTY, CROSS, EMPTY, EMPTY,
                    ZERO, EMPTY, ZERO, EMPTY, EMPTY
                ),
                3,
                CROSS
            )
        )

        private val zeroCases = crossCases.map { it.inverse() }

        private val allCases = emptyCases + crossCases + zeroCases

        @JvmStatic
        @Parameterized.Parameters
        fun cases(): List<TicTacToeWinnerCase> = allCases
    }

    @Test
    fun `simple winner test`() {
        findWinnerSimpleTest(SimpleWinnerAlgorithm(), case)
    }

    private fun findWinnerSimpleTest(algorithm: WinnerAlgorithm, case: TicTacToeWinnerCase) {
        with(algorithm) {
            val winner = case.matrix.findWinner(case.inRow)
            assertThat(
                case.matrix.toString(),
                winner.map { it.cellType }.takeIf { it.isNotEmpty() } ?: listOf(EMPTY),
                CoreMatchers.equalTo(listOf(case.result)))
        }
    }

}
