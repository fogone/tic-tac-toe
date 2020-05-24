package ru.nobirds.minesweeper.swing

import ru.nobirds.minesweeper.FieldCell
import ru.nobirds.minesweeper.GameModel
import ru.nobirds.minesweeper.GameState
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
                private var model: GameModel) {

    private val defaultFont = Font(Font.MONOSPACED, Font.BOLD, 15)
    private val mineFont = Font(Font.MONOSPACED, Font.BOLD, 25)

    private var buttons = createButtons(model)

    val panel = JPanel()

    init {
        initialize(model)
        updateView()
    }

    fun updateModel(model: GameModel) {
        removeAllCells()
        initialize(model)
        updateView()
    }

    private fun removeAllCells() {
        this.buttons.forEach { panel.remove(it.value) }
    }

    private fun createButtons(gameModel: GameModel) =
            gameModel.field.mapValues { it.position to createDefaultButton().initialize(it) }.toMap()

    private fun createDefaultButton(): JButton {
        return JButton().apply {
            font = Font(Font.DIALOG, Font.BOLD, 15)
            preferredSize = Dimension(30, 30)
            size = preferredSize
        }
    }

    private fun initialize(model: GameModel) {
        this.model = model
        this.buttons = createButtons(model)
        this.panel.apply {
            background = Color.LIGHT_GRAY
            layout = GridLayout(model.field.size.x, model.field.size.y)
            buttons.values.forEach { add(it) }
        }
        this.model.state = GameState.GAME
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
    }

    private fun handleCheckMine(cell: FieldCell) {
        model.check(cell)
        updateView()
    }

    private fun updateView() {
        minesCountLabel.text = model.minesLeft.toString()

        buttons.forEach { (position, button) ->
            val fieldCell = model.field[position]

            if (fieldCell.checked) {
                button.checkedCell()
            } else {
                button.text = ""
            }

            if (fieldCell.opened || model.state == GameState.LOOSER) {
                if (fieldCell.mine) {
                    button.openedMineCell()
                } else {
                    button.openedWithoutMineCell(fieldCell)
                }
            } else {
                button.defaultCell()
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
