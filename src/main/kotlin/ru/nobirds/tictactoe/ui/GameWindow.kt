package ru.nobirds.tictactoe.ui

import ru.nobirds.tictactoe.*
import ru.nobirds.utils.ObservableMatrixWrapper
import ru.nobirds.utils.mutableMatrixOf
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Container
import javax.swing.*

enum class PlayerType {
    HUMAN, AI
}

class NewGameWindow(private val newGameListener: (Int, Int, PlayerType, PlayerType) -> Unit) : JFrame("New game") {

    init {
        contentPane.initializeComponents()
        pack()
    }

    private fun Container.initializeComponents() {
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
            this@NewGameWindow.isVisible = false
            newGameListener(size.value, inRow.value,
                    firstPlayer.selectedItem as PlayerType, secondPlayer.selectedItem as PlayerType)
        }
    }

}

class GameWindow() : JFrame("Tic Tac Toe") {

    private val gameFieldPanel = GameFieldPanel()

    private val newGameWindow = NewGameWindow { size, inRow, firstPlayer, secondPlayer ->
        gameFieldPanel.newGame(generateGame(size, inRow, firstPlayer, secondPlayer)) { last ->
            updateComponentsToGameOver(last)
        }
    }

    private fun updateComponentsToGameOver(last: Turn) = with(startButton) {
        if (last.winner) {
            text = "${last.cellType.symbol} is winner! Start new?"
            foreground = Color.RED
        } else {
            text = "Game Over. Start new?"
        }
    }

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
                    newGameWindow.isVisible = true
                }
            }
        }
    }

    private fun generateGame(
            size: Int,
            inRow: Int,
            firstPlayer: PlayerType,
            secondPlayer: PlayerType
    ): TicTacToeGame {
        return createDefaultGame(size, inRow) {
            createPlayerByType(it, when (it) {
                CellType.CROSS -> firstPlayer
                CellType.ZERO -> secondPlayer
                else -> error("No empty player")
            })
        }
    }

    private fun createPlayerByType(cellType: CellType, type: PlayerType) = when (type) {
        PlayerType.AI -> createDefaultAiPlayer(cellType)
        PlayerType.HUMAN -> createHumanPlayer(cellType)
    }

    private fun createHumanPlayer(cellType: CellType): Player {
        return SuspendablePlayer(cellType, gameFieldPanel.turnWaiter)
    }

}
