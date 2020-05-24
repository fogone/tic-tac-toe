package ru.nobirds.minesweeper.swing

import javax.swing.JLabel
import javax.swing.Timer

class TimerView {

    var seconds = 0
        private set

    val timerLabel = JLabel("0 сек")

    private val timer = Timer(1000) {
        seconds++
        timerLabel.text = "$seconds сек"
    }

    fun start() {
        seconds = 0
        if(!timer.isRunning)
            timer.start()
    }

    fun stop() {
        timer.stop()
    }
}
