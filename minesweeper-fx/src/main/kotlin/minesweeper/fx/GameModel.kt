package ru.nobirds.minesweeper.fx

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.binding.LongBinding
import javafx.beans.property.IntegerProperty
import javafx.beans.property.Property
import javafx.beans.property.ReadOnlyProperty
import ru.nobirds.utils.*
import tornadofx.*

class FieldCell(position: Point,
                mine: Boolean = false,
                minesAroundNumber: Int = 0,
                opened: Boolean = false,
                checked: Boolean = false) : Observable {

    val positionProperty: ReadOnlyProperty<Point> = position.toProperty()
    val position by positionProperty

    val mineProperty: Property<Boolean> = mine.toProperty()
    var mine by mineProperty

    val openedProperty: Property<Boolean> = opened.toProperty()
    var opened by openedProperty

    val checkedProperty: Property<Boolean> = checked.toProperty()
    var checked by checkedProperty

    val minesAroundNumberProperty: IntegerProperty = minesAroundNumber.toProperty()
    var minesAroundNumber by minesAroundNumberProperty

    private val observableSupport by lazy { support(mineProperty, openedProperty, checkedProperty, minesAroundNumberProperty) }
    override fun removeListener(listener: InvalidationListener) = observableSupport.removeListener(listener)
    override fun addListener(listener: InvalidationListener) = observableSupport.addListener(listener)

    override fun toString(): String = buildString {
        if(opened) append("[") else append("{")
        if(mine) append("*") else "$minesAroundNumber"
        if(opened) append("]") else append("}")
    }
}

enum class GameState {
    PAUSE, GAME, WINNER, LOOSER
}

val FieldCell.empty: Boolean get() = !mine && minesAroundNumber == 0

class GameModel(width: Int, height: Int, private val minesNumber: Int) : Observable {

    private val mutableField =
        mutableMatrixOf(width, height) { x, y -> FieldCell(x x y) }.apply {
        setMines()
        updateMineNumbers()
    }.asObservable()

    val field: ObservableMatrix<FieldCell>
        get() = mutableField

    val stateProperty = GameState.PAUSE.toProperty()
    var state by stateProperty

    val startedProperty = stateProperty.booleanBinding { it == GameState.GAME }

    val winnerProperty = field
        .countBinding { !it.mine && !it.opened }
        .booleanBinding { it?.toLong() ?: 0 == 0L }
        .apply {
            onState {
                state = GameState.WINNER
            }
        }

    val looserProperty = field
        .countBinding { it.opened && it.mine }
        .booleanBinding { it?.toLong() ?: 0 > 0 }
        .apply {
            onState {
                state = GameState.LOOSER
            }
        }

    val gameOverProperty = winnerProperty.or(looserProperty)
    val gameOver by gameOverProperty

    val minesLeftProperty: LongBinding = field.countBinding { it.checked }
        .longBinding { checked -> checked?.toLong()?.let { minesNumber - it } ?: minesNumber.toLong() }

    val minesLeft: Long by minesLeftProperty

    private val observableSupport = support(field, stateProperty, minesLeftProperty)

    override fun removeListener(listener: InvalidationListener) = observableSupport.removeListener(listener)
    override fun addListener(listener: InvalidationListener) = observableSupport.addListener(listener)

    private fun MutableMatrix<FieldCell>.setMines() {
        generateSequence { size.random() }
                .distinct()
                .take(minesNumber)
                .map { get(it) }
                .forEach { it.mine = true }
    }

    private fun MutableMatrix<FieldCell>.updateMineNumbers() {
        forEachIndexed { x, y, it ->
            it.minesAroundNumber = findMinesNumberAround(x x y)
        }
    }

    private fun Matrix<FieldCell>.findMinesNumberAround(point: Point): Int {
        return aroundValues(point).count { it.mine }
    }

    fun open(point: Point) {
        val fieldCell = field[point]

        if(fieldCell.opened) return

        fieldCell.opened = true

        if(fieldCell.empty) {
            for (cell in field.aroundValues(point).filter { !it.opened }) {
                open(cell.position)
            }
        }
    }

    fun openUnchecked(point: Point) {
        val cell = field[point]
        if(cell.minesAroundNumber == field.aroundValues(point).count { it.checked }) {
            field.aroundValues(point).filter { !it.checked && !it.opened }.forEach {
                open(it.position)
            }
        }
    }

    private fun checkWinner(): Boolean {
        return field.count { !it.mine && !it.opened } == 0L
    }

    private fun checkLooser(): Boolean {
        return field.count { it.opened && it.mine } > 0
    }

    fun check(cell: FieldCell) {
        if(!cell.opened) {
            cell.checked = !cell.checked
        }
    }

}
