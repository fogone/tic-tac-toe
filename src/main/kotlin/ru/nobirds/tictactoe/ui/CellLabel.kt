package ru.nobirds.tictactoe.ui

import ru.nobirds.tictactoe.CellType
import ru.nobirds.utils.Point
import java.awt.Color
import java.awt.Cursor
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JLabel
import javax.swing.SwingConstants
import javax.swing.border.LineBorder

class CellLabel(val position: Point) : JLabel() {

    var cellType: CellType? = null
        set(value) {
            field = value
            updateComponent(value)
        }

    var selectHandler: (Point) -> Unit = {}

    init {
        border = LineBorder(Color.DARK_GRAY)
        horizontalAlignment = SwingConstants.CENTER
        verticalAlignment = SwingConstants.CENTER
        size = Dimension(30, 30)
        font = font.deriveFont(32f)
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent?) {
                selectHandler(position)
            }
        })
    }

    private fun disableHandling() {
        selectHandler = {}
        cursor =
            Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
    }

    private fun updateComponent(value: CellType?) {
        when (value) {
            CellType.CROSS -> {
                text = "X"
                foreground = Color.BLUE
                disableHandling()
            }
            CellType.ZERO -> {
                text = "0"
                foreground = Color.ORANGE
                disableHandling()
            }
        }
    }
}
