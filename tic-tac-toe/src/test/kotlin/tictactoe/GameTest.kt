package ru.nobirds.tictactoe

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import ru.nobirds.utils.mutableMatrixOf

class GameTest {

    @Test
    fun gameTest() {
        val game = TicTacToeGame(
            mutableMatrixOf(5, 5) { _, _ -> CellType.EMPTY },
            5,
            AiPlayer(
                CellType.CROSS,
                NaiveAlertSearchStrategy,
                FirstChooseAlertStrategy,
                RandomPreventAlertStrategy,
                RandomAttackStrategy
            ),
            AiPlayer(
                CellType.ZERO,
                NaiveAlertSearchStrategy,
                FirstChooseAlertStrategy,
                RandomPreventAlertStrategy,
                RandomAttackStrategy
            ),
            SimpleWinnerAlgorithm()
        )

        val winner = runBlocking {
            game.run().toList().last()
        }

        assertThat(winner.winner, equalTo(false))
    }
}
