package ru.nobirds.tictactoe.ui

import java.awt.Component
import java.awt.Container
import javax.swing.JComboBox
import javax.swing.JFrame
import javax.swing.JSlider
import javax.swing.SwingConstants

class NewGameWindow(private val newGameListener: (Int, Int, PlayerType, PlayerType) -> Unit) : JFrame("New game") {

    init {
        contentPane.initializeComponents()
        pack()
    }

    private fun Container.initializeComponents() {
        flowLayout(SwingConstants.LEADING)

        label("Field size")

        val size = slider(5)

        label("In row")

        val inRow = slider(4)

        label("First player")

        val firstPlayer = JComboBox(PlayerType.values()).addTo(this)

        label("Second player")

        val secondPlayer = JComboBox(PlayerType.values()).addTo(this)

        button("Start") {
            this@NewGameWindow.isVisible = false
            newGameListener(size.value, inRow.value,
                    firstPlayer.selectedItem as PlayerType, secondPlayer.selectedItem as PlayerType)
        }
    }

    private fun Container.slider(value: Int): JSlider {
        return JSlider(SwingConstants.HORIZONTAL, 3, 10, value).apply {
            paintTicks = true
            paintTrack = true
            snapToTicks = true
        }.addTo(this)
    }

}

fun <T: Component> T.addTo(container: Container) = apply { container.add(this) }
