package ru.nobirds.minesweeper.fx

import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyProperty
import tornadofx.*

class GameModel() : ViewModel() {

    val gameFieldProperty: ObjectProperty<GameField> = objectProperty<GameField>().apply {
        onChange {
            if (it != null) {
                secondsCounter.secondsProperty.value = 0
            }
        }
    }

    var gameField: GameField? by gameFieldProperty

    private val mutableTimerProperty = 0.toProperty()

    val secondsCounter = SecondsCounter().apply {
        mutableTimerProperty.bind(secondsProperty)
        startedProperty.bind(gameFieldProperty.flatMap { startedProperty })
    }

    val timerProperty: ReadOnlyProperty<Number> get() = mutableTimerProperty

}

