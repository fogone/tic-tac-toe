package ru.nobirds.tictactoe

import ru.nobirds.tictactoe.CellType.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

data class AlertCase(val matrix: GameField, val inRow: Int, val type: CellType, val result: GameRange)

fun AlertCase.inverse(): AlertCase = AlertCase(matrix.map { it.inverse() }, inRow, type.inverse(), result)

@RunWith(Parameterized::class)
class AlertSearchTest(private val case: AlertCase) {

    companion object {

        private val crossCases = listOf(
            AlertCase(
                mutableMatrixOf(
                    3,
                    CROSS, EMPTY, EMPTY,
                    EMPTY, ZERO, EMPTY,
                    CROSS, EMPTY, ZERO
                ),
                3,
                CROSS,
                range(0 x 0, 1 x 0, 2 x 0)
            ),
            AlertCase(
                mutableMatrixOf(
                    3,
                    CROSS, ZERO, EMPTY,
                    EMPTY, CROSS, EMPTY,
                    EMPTY, ZERO, EMPTY
                ),
                3,
                CROSS,
                range(0 x 0, 1 x 1, 2 x 2)
            ),
            AlertCase(
                mutableMatrixOf(
                    5,
                    CROSS, EMPTY, EMPTY, EMPTY, EMPTY,
                    EMPTY, ZERO, CROSS, EMPTY, EMPTY,
                    CROSS, EMPTY, ZERO, EMPTY, EMPTY,
                    EMPTY, EMPTY, ZERO, EMPTY, EMPTY,
                    CROSS, EMPTY, ZERO, EMPTY, EMPTY
                ),
                5,
                CROSS,
                range(0 x 0, 1 x 0, 2 x 0, 3 x 0, 4 x 0)
            ),
            AlertCase(
                mutableMatrixOf(
                    5,
                    EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                    EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                    EMPTY, EMPTY, CROSS, EMPTY, EMPTY,
                    EMPTY, CROSS, EMPTY, EMPTY, EMPTY,
                    ZERO, EMPTY, EMPTY, EMPTY, EMPTY
                ),
                3,
                CROSS,
                range(1 x 3, 2 x 2, 3 x 1)
            )
        )

        private fun range(vararg points: Point): GameRange {
            return GameRange(points.toList())
        }

        private val zeroCases = crossCases.map { it.inverse() }

        private val allCases = crossCases + zeroCases

        @JvmStatic
        @Parameterized.Parameters
        fun cases(): List<AlertCase> = allCases
    }

    @Test
    fun `naive alert test`() {
        with(NaiveAlertSearchStrategy) {
            assertThat(
                case.matrix.toString(),
                case.matrix.findAlerts(case.inRow, case.type),
                equalTo(listOf(case.result))
            )
        }
    }

}
