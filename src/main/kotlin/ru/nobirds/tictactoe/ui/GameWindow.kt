package ru.nobirds.tictactoe.ui

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import ru.nobirds.tictactoe.*
import ru.nobirds.utils.Point
import java.awt.BorderLayout
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

class GameWindow() : JFrame("Tic Tac Toe") {

    private var game: TicTacToeGame? = null
        set(value) {
            field = value
            updateComponents(value)
        }

    private val turnWaiter = TurnEventWaiter()
    private val gameFieldPanel = GameFieldPanel(turnWaiter.channel)
    private lateinit var startButton: JButton

    init {
        initializeComponents()
    }

    private fun initializeComponents() {
        withExitOnClose()
        setSize(500, 500)
        contentPane.apply {
            borderLayout()
            add(gameFieldPanel)
            panel(BorderLayout.SOUTH) {
                startButton = button("New Game") {
                    openNewGameDialog()
                }
            }
        }
    }

    private fun updateComponents(value: TicTacToeGame?) {
        gameFieldPanel.gameField = value?.field
        if (value != null) {
            GlobalScope.launch {
                val last = value.run().onEach {
                    gameFieldPanel.turn(it)
                }.toList().last()

                startButton.apply {
                    if(last.winner) {
                        text = "${last.cellType.symbol} is winner! Start new?"
                        foreground = Color.RED
                    } else {
                        text = "Game Over. Start new?"
                    }
                }
            }
        }
    }

    enum class PlayerType {
        HUMAN, AI
    }

    private fun openNewGameDialog() {
        createNewGameDialog { size, inRow, firstPlayer, secondPlayer ->
            game = generateGame(size, inRow, firstPlayer, secondPlayer)
        }
    }

    private fun createNewGameDialog(block: (Int, Int, PlayerType, PlayerType) -> Unit) {
        frame("New game") {
            val mainFrame = this
            contentPane.apply {
                flowLayout(SwingConstants.LEADING)
                label("Field size")
                val size = add(
                    JSlider(
                        SwingConstants.HORIZONTAL,
                        3,
                        10,
                        5
                    ).apply {
                        paintTicks = true
                        paintTrack = true
                        snapToTicks = true
                    }) as JSlider
                label("In row")
                val inRow = add(
                    JSlider(
                        SwingConstants.HORIZONTAL,
                        3,
                        10,
                        4
                    ).apply {
                        paintTicks = true
                        paintTrack = true
                        snapToTicks = true
                    }) as JSlider
                label("First player")
                val firstPlayer =
                    add(JComboBox(PlayerType.values())) as JComboBox<PlayerType>
                label("Second player")
                val secondPlayer =
                    add(JComboBox(PlayerType.values())) as JComboBox<PlayerType>
                button("Start") {
                    mainFrame.isVisible = false
                    block(size.value, inRow.value,
                        firstPlayer.selectedItem as PlayerType, secondPlayer.selectedItem as PlayerType)
                }
            }
            isVisible = true
        }
    }

    private fun generateGame(
        size: Int,
        inRow: Int,
        firstPlayer: PlayerType,
        secondPlayer: PlayerType
    ): TicTacToeGame {
        return createDefaultGame(size, inRow) {
            createPlayerByType(
                it, when (it) {
                    CellType.CROSS -> firstPlayer
                    CellType.ZERO -> secondPlayer
                    else -> error("No empty player")
                }
            )
        }
    }

    private fun createPlayerByType(cellType: CellType, type: PlayerType) = when(type) {
        PlayerType.AI -> createDefaultAiPlayer(cellType)
        PlayerType.HUMAN -> createHumanPlayer(cellType)
    }

    private fun createHumanPlayer(cellType: CellType): Player {
        return SuspendablePlayer(cellType, turnWaiter)
    }

    class TurnEventWaiter() : EventWaiter {
        val channel =
            Channel<Point>()
        override suspend fun waitForPoint(): Point {
            return channel.receive()
        }
    }
}
