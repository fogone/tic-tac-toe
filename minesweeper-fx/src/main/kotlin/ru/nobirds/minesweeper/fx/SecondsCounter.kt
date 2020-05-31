package ru.nobirds.minesweeper.fx

import javafx.beans.property.IntegerProperty
import tornadofx.*
import java.util.*

class SecondsCounter {

    val secondsProperty: IntegerProperty = 0.toProperty()

    val startedProperty = false.toProperty {
        onChange {
            if (it) start() else stop()
        }
    }

    private var timerTask: FXTimerTask? = null

    private fun createTask(): FXTimerTask = FXTimerTask(this::tick, Timer()).apply {
        timer.schedule(this, 0L, 1000L)
    }

    private fun tick() {
        secondsProperty.value += 1
    }

    private fun stop() {
        timerTask?.timer?.cancel()
        timerTask = null
    }

    private fun start() {
        timerTask = createTask()
    }

}
