package ru.nobirds.minesweeper.swing

import ru.nobirds.minesweeper.GameModel
import ru.nobirds.minesweeper.GameState
import ru.nobirds.swing.cell
import ru.nobirds.swing.sequentialHorizontal
import ru.nobirds.swing.sequentialVertical
import java.awt.*
import javax.swing.*
import javax.swing.JFrame.EXIT_ON_CLOSE
import kotlin.system.exitProcess

class MinesweeperApplication() {

    private val timerView = TimerView()
    private val minesCountLabel = JLabel("0")
    private val fieldPanel = FieldView(minesCountLabel, GameModel(8, 8, 10, this::handleStateChange))

    private val frame = createGameWindow()
    private val configFrame = createConfigurationWindow()

    private fun handleStateChange(state: GameState) {
        when (state) {
            GameState.WINNER, GameState.LOOSER -> SwingUtilities.invokeLater { restartDialog(state) }
        }
    }

    private fun restartDialog(state: GameState) {
        timerView.stop()

        val result = when (state) {
            GameState.WINNER -> JOptionPane.showConfirmDialog(frame, "Еще разок?", "Вы победили!",
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)
            GameState.LOOSER -> JOptionPane.showConfirmDialog(frame, "Еще разок?", "Вы проиграли!",
                    JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE)
            else -> error("Can't be")
        }

        if (result == JOptionPane.YES_OPTION) {
            frame.isVisible = false
            configFrame.isVisible = true
        } else {
            frame.isVisible = false
            frame.dispose()
            exitProcess(0)
        }
    }

    private fun newGame(gameModel: GameModel) {
        fieldPanel.updateModel(gameModel)
        timerView.start()
        frame.pack()
    }

    fun start() {
        SwingUtilities.invokeLater {
            configFrame.isVisible = true
        }
    }

    private fun createGameWindow(): JFrame = JFrame("Minesweeper").apply {
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = GridBagLayout()

        cell(minesCountLabel) {
            weightx = 0.3
        }
        cell(JButton("Заново").apply {
            addActionListener {
                configFrame.isVisible = true
            }
        }) {
            weightx = 0.3
        }
        cell(timerView.timerLabel) {
            weightx = 0.3
        }
        cell(fieldPanel.panel) {
            gridx = 0
            gridy = 1
            gridwidth = 3
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.BOTH
        }
        pack()
    }

    private fun createConfigurationWindow(): JFrame = JFrame("New Game").apply {
        val configFrame = this
        defaultCloseOperation = EXIT_ON_CLOSE
        val layout = GroupLayout(contentPane)
        contentPane.layout = layout

        val widthLabel = JLabel("Высота")
        val widthSlider = JSlider(JSlider.HORIZONTAL, 7, 50, 9).apply {
            majorTickSpacing = 5
            minorTickSpacing = 1
            paintLabels = true
            paintTicks = true
            paintTrack = true
        }
        val heightLabel = JLabel("Ширина")
        val heightSlider = JSlider(JSlider.HORIZONTAL, 7, 50, 9).apply {
            majorTickSpacing = 5
            minorTickSpacing = 1
            paintLabels = true
            paintTicks = true
            paintTrack = true
        }
        val minesNumberLabel = JLabel("Количетво мин")
        val minesNumberSpinner = JSpinner(SpinnerNumberModel(10, 5, 1000, 1))

        fun setValues(width: Int, height: Int, mines: Int) {
            widthSlider.value = width
            heightSlider.value = height
            minesNumberSpinner.value = mines
        }

        fun switchToGame() {
            configFrame.isVisible = false
            frame.isVisible = true
        }

        val beginnerButton = JButton("Бегинер").apply {
            addActionListener { setValues(9, 9, 10) }
        }
        val intermediateButton = JButton("Интермедиейт").apply {
            addActionListener { setValues(16, 16, 40) }
        }
        val expertButton = JButton("Эксперт").apply {
            addActionListener { setValues(16, 31, 99) }
        }

        val startButton = JButton("Старт").apply {
            addActionListener {
                newGame(GameModel(widthSlider.value, heightSlider.value,
                        minesNumberSpinner.value as Int, this@MinesweeperApplication::handleStateChange))
                switchToGame()
            }
        }
        val cancelButton = JButton("Отмена").apply {
            addActionListener {
                switchToGame()
            }
        }

        layout.sequentialVertical {
            parallel {
                component(GroupLayout.Alignment.CENTER, beginnerButton)
                component(GroupLayout.Alignment.CENTER, intermediateButton)
                component(GroupLayout.Alignment.CENTER, expertButton)
            }
            parallel(GroupLayout.Alignment.BASELINE) {
                component(widthLabel)
                component(widthSlider)
                component(startButton)
            }
            parallel {
                component(heightLabel)
                component(heightSlider)
                component(cancelButton)
            }
            parallel {
                component(minesNumberLabel)
                component(minesNumberSpinner)
            }
        }

        layout.sequentialHorizontal {
            parallel {
                component(GroupLayout.Alignment.CENTER, beginnerButton)
                component(widthLabel)
                component(heightLabel)
                component(minesNumberLabel)
            }
            parallel {
                component(GroupLayout.Alignment.CENTER, intermediateButton)
                component(heightSlider)
                component(widthSlider)
                component(minesNumberSpinner)
            }
            parallel {
                component(GroupLayout.Alignment.CENTER, expertButton)
                component(startButton)
                component(cancelButton)
            }
        }

        pack()
    }

}
