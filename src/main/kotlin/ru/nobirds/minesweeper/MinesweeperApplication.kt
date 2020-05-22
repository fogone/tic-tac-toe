package ru.nobirds.minesweeper

import ru.nobirds.utils.*
import ru.nobirds.utils.Point
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.Border
import javax.swing.border.EmptyBorder
import kotlin.system.exitProcess

class MinesweeperApplication() {

    private val frame = JFrame()
    private val minesCountLabel = JLabel()
    private val fieldPanel = FieldView(minesCountLabel, FieldModel(8, 8, 10)) {
        restartDialog(it)
    }

    private fun restartDialog(winner: Boolean) {
        val result = if (winner) {
            JOptionPane.showConfirmDialog(frame, "Еще разок?", "Вы победили!",
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)
        } else {
            JOptionPane.showConfirmDialog(frame, "Еще разок?", "Вы проиграли!",
                    JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE)
        }

        if (result == JOptionPane.YES_OPTION) {
            newGame()
        } else {
            frame.isVisible = false
            frame.dispose()
            exitProcess(0)
        }
    }

    private fun newGame() {
        fieldPanel.updateModel(FieldModel(8, 8, 10))
    }

    fun start() {
        SwingUtilities.invokeLater {
            createGameWindow().isVisible = true
        }
    }

    private fun createGameWindow(): JFrame = frame.apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        layout = GridBagLayout()
        cell(minesCountLabel) {
            weightx = 0.3
        }
        cell(JButton("New Game").apply {
            addActionListener { newGame() }
        }) {
            weightx = 0.3
        }
        cell(JLabel("00")) {
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

}

class FieldView(private val minesCountLabel: JLabel,
                private var model: FieldModel,
                private val gameOverListener: (Boolean) -> Unit) {

    private data class Cell(val button: JButton, val fieldCell: FieldCell)

    private var buttons = model.field.map { Cell(createDefaultButton(), it) }

    private var gameOver = false

    val panel = JPanel()

    init {
        createFieldPanel(buttons)
        updateView()
    }

    fun updateModel(model: FieldModel) {
        this.buttons.forEach { panel.remove(it.button) }
        this.model = model
        this.buttons = model.field.map { Cell(createDefaultButton(), it) }
        this.createFieldPanel(buttons)
        this.gameOver = false
        updateView()
    }

    private fun createDefaultButton(): JButton {
        return JButton().apply {
            font = Font(Font.DIALOG, Font.BOLD, 15)
        }
    }

    private fun createFieldPanel(model: Matrix<Cell>) {
        panel.apply {
            background = Color.LIGHT_GRAY
            preferredSize = Dimension(500, 500)
            layout = GridLayout(model.size.x, model.size.y)

            model.forEachIndexed { x, y, it ->
                add(it.button.initialize(x x y, it.fieldCell))
            }
        }
    }

    private fun JButton.initialize(point: Point, cell: FieldCell): JButton = apply {
        addMouseListener(ExtendedMouseListener(object : ExtendedMouseHandler {
            override fun onRightButton() {
                handleCheckMine(point, cell)
            }
            override fun onLeftButton() {
                handleOpen(point, cell)
            }
        }))
    }

    private fun handleOpen(point: Point, cell: FieldCell) {
        if (cell.opened) {
            model.openUnchecked(point)
        } else {
            model.open(point)
        }
        updateView()
        checkGameOver()
    }

    private fun handleCheckMine(point: Point, cell: FieldCell) {
        if(!cell.opened) {
            cell.checked = !cell.checked
        }
        updateView()
    }

    private fun updateView() {
        minesCountLabel.text = (model.minesNumber - buttons.count { it.fieldCell.checked }).toString()

        buttons.forEach {
            if (it.fieldCell.checked) {
                it.button.text = "X"
            }
            if (it.fieldCell.opened || gameOver) {
                if (it.fieldCell.mine) {
                    it.button.text = "X"
                    it.button.background = Color.LIGHT_GRAY
                } else {
                    it.button.text = if(it.fieldCell.minesAroundNumber > 0) it.fieldCell.minesAroundNumber.toString() else ""
                    it.button.foreground = Color.BLUE
                }
                it.button.border = EmptyBorder(0, 0, 0, 0)
            } else {
                it.button.background = UIManager.getDefaults()["Button.background"] as Color
                it.button.foreground = UIManager.getDefaults()["Button.foreground"] as Color
                it.button.border = UIManager.getDefaults()["Button.border"] as Border
            }
        }

        panel.invalidate()
        panel.repaint()
    }

    private fun checkGameOver() {
        if (model.checkLooser()) {
            gameOver = true
            updateView()
            SwingUtilities.invokeLater {
                gameOverListener(false)
            }
            return
        }

        if (model.checkWinner()) {
            gameOver = true
            updateView()
            SwingUtilities.invokeLater {
                gameOverListener(true)
            }
            return
        }
    }

}

class ExtendedMouseListener(private val handler: ExtendedMouseHandler) : MouseAdapter() {

    override fun mouseReleased(e: MouseEvent) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            handler.onLeftButton()
        }
        if (SwingUtilities.isRightMouseButton(e)) {
            handler.onRightButton()
        }
    }
}

interface ExtendedMouseHandler {
    fun onRightButton()
    fun onLeftButton()
}

fun Container.cell(component: Component, constraints: GridBagConstraints.() -> Unit) {
    add(component, constraints(constraints))
}

fun constraints(builder: GridBagConstraints.() -> Unit): GridBagConstraints {
    return GridBagConstraints().apply(builder)
}
