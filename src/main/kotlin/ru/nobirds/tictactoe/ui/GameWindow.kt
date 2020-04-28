package ru.nobirds.tictactoe.ui

import ru.nobirds.tictactoe.*
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.*

enum class PlayerType {
    HUMAN, AI
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

    private val startButton: JButton = JButton("New Game").apply {
        addActionListener {
            newGameWindow.isVisible = true
        }
    }

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
                startButton.addTo(this)
            }
        }
    }

    private fun generateGame(size: Int, inRow: Int, firstPlayer: PlayerType, secondPlayer: PlayerType): TicTacToeGame {
        return createDefaultGame(size, inRow) {
            createPlayerByType(it, it.toPlayerType(firstPlayer, secondPlayer))
        }
    }

    private fun CellType.toPlayerType(firstPlayer: PlayerType, secondPlayer: PlayerType): PlayerType {
        return when (this) {
            CellType.CROSS -> firstPlayer
            CellType.ZERO -> secondPlayer
            else -> error("No empty player")
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
