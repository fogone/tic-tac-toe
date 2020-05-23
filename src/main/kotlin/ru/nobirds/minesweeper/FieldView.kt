package ru.nobirds.minesweeper

import ru.nobirds.utils.*
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.GridLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.Border
import javax.swing.border.EmptyBorder

class FieldView(private val minesCountLabel: JLabel,
                private var model: FieldModel,
                private val gameStateListener: (GameState) -> Unit) {

    private data class Cell(val button: JButton, val fieldCell: FieldCell)

    private val defaultFont = Font(Font.MONOSPACED, Font.BOLD, 15)
    private val mineFont = Font(Font.MONOSPACED, Font.BOLD, 25)

    private var buttons = model.field.map { Cell(createDefaultButton(), it) }

    private var state = GameState.INIT
        set(value) {
            if (value != field) {
                gameStateListener(value)
            }
            field = value
        }

    val panel = JPanel()

    init {
        createFieldPanel(model)
        updateView()
    }

    fun updateModel(model: FieldModel) {
        removeAllCells()

        this.createFieldPanel(model)
        this.state = GameState.GAME

        updateView()
    }

    private fun removeAllCells() {
        this.buttons.forEach { panel.remove(it.button) }
    }

    private fun createDefaultButton(): JButton {
        return JButton().apply {
            font = Font(Font.DIALOG, Font.BOLD, 15)
        }
    }

    private fun createFieldPanel(model: FieldModel) {
        this.model = model
        this.buttons = model.field.map { Cell(createDefaultButton(), it) }
        this.panel.apply {
            background = Color.LIGHT_GRAY
            preferredSize = Dimension(500, 500)
            layout = GridLayout(buttons.size.x, buttons.size.y)
            buttons.forEachIndexed { _, _, it ->
                add(it.button.initialize(it.fieldCell))
            }
        }
    }

    private fun JButton.initialize(cell: FieldCell): JButton = apply {
        addMouseListener(ExtendedMouseListener(object : ExtendedMouseHandler {
            override fun onRightButton() {
                handleCheckMine(cell)
            }
            override fun onLeftButton() {
                handleOpen(cell)
            }
        }))
    }

    private fun handleOpen(cell: FieldCell) {
        if (cell.opened) {
            model.openUnchecked(cell.position)
        } else {
            model.open(cell.position)
        }
        updateView()
        checkGameOver()
    }

    private fun handleCheckMine(cell: FieldCell) {
        if(!cell.opened) {
            cell.checked = !cell.checked
        }
        updateView()
    }

    private fun updateView() {
        checkGameOver()

        minesCountLabel.text = getMinesLeft().toString()

        buttons.forEach {
            if (it.fieldCell.checked) {
                it.button.checkedCell()
            }
            if (it.fieldCell.opened || state == GameState.LOOSER) {
                if (it.fieldCell.mine) {
                    it.button.openedMineCell()
                } else {
                    it.button.openedWithoutMineCell(it.fieldCell)
                }
            } else {
                it.button.defaultCell()
            }
        }

        panel.invalidate()
        panel.repaint()
    }

    private fun JButton.openedWithoutMineCell(fieldCell: FieldCell) {
        text = if (fieldCell.minesAroundNumber > 0) fieldCell.minesAroundNumber.toString() else ""
        font = defaultFont
        foreground = getColorForCell(fieldCell.minesAroundNumber)
        border = EmptyBorder(0, 0, 0, 0)
    }

    private fun getColorForCell(minesAroundNumber: Int) = when(minesAroundNumber) {
        0 -> Color.BLACK
        1 -> Color.BLUE
        2 -> Color.GREEN
        3 -> Color.RED
        4 -> Color(0, 76, 153)
        5 -> Color(102, 0, 0)
        6 -> Color(255, 128, 0)
        7 -> Color(0, 51, 25)
        8 -> Color(132, 33, 86)
        else -> error("Unsupported")
    }

    private fun JButton.openedMineCell() {
        text = "*"
        font = mineFont
        foreground = Color.RED
        border = EmptyBorder(0, 0, 0, 0)
    }

    private fun JButton.checkedCell() {
        text = "*"
        font = mineFont
        foreground = Color.ORANGE
    }

    private fun JButton.defaultCell() {
        val defaults = UIManager.getDefaults()
        background = defaults["Button.background"] as Color
        foreground = defaults["Button.foreground"] as Color
        border = defaults["Button.border"] as Border
        font = defaultFont
    }

    private fun getMinesLeft() = model.minesNumber - buttons.count { it.fieldCell.checked }

    private fun checkGameOver() {
        if (model.checkLooser()) {
            state = GameState.LOOSER
            return
        }

        if (model.checkWinner()) {
            state = GameState.WINNER
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