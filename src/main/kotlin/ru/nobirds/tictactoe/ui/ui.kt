package ru.nobirds.tictactoe.ui

import java.awt.*
import java.awt.event.ActionEvent
import javax.swing.*


inline fun <T : Container> T.replaceComponents(block: T.() -> Unit) {
    removeAll()
    block()
    revalidate()
    repaint()
}

fun frame(title: String, builder: JFrame.() -> Unit = {}): JFrame {
    return JFrame(title).apply(builder).apply {
        pack()
    }
}

fun JFrame.withExitOnClose() {
    defaultCloseOperation = JFrame.EXIT_ON_CLOSE
}

fun Container.gridLayout(builder: GridLayout.() -> Unit) {
    layout = GridLayout().apply(builder)
}

fun <T: LayoutManager> Container.layout(layout: T, builder: T.() -> Unit = {}) {
    this.layout = layout.apply(builder)
}

fun Container.flowLayout(align: Int) {
    layout(FlowLayout(align))
}

fun Container.borderLayout(builder: BorderLayout.() -> Unit = {}) = layout(BorderLayout(), builder)
fun Container.gridLayout(rows: Int, cols: Int, builder: GridLayout.() -> Unit = {}) = layout(GridLayout(rows, cols), builder)

fun Container.panel(constraints: Any? = null, builder: JPanel.() -> Unit = {}): JPanel {
    val jPanel = JPanel().apply(builder)
    if(constraints != null) add(jPanel, constraints) else add(jPanel)
    return jPanel
}

fun Container.label(text: String, builder: JLabel.() -> Unit = {}) {
    add(JLabel(text).apply(builder))
}

fun Container.button(title: String, onClick: (ActionEvent) -> Unit) = button {
    text = title
    addActionListener(onClick)
}

fun Container.button(builder: JButton.() -> Unit): JButton {
    return add(JButton().apply(builder)) as JButton
}
