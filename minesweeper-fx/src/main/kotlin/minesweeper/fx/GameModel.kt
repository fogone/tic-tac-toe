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

}

enum class GameState {
    INIT, GAME, WINNER, LOOSER
}

val FieldCell.empty: Boolean get() = !mine && minesAroundNumber == 0

class GameModel(width: Int, height: Int, private val minesNumber: Int) {

    private val mutableField =
        mutableMatrixOf(width, height) { x, y -> FieldCell(x x y) }.apply {
        setMines()
        updateMineNumbers()
    }.asObservable()

    val field: ObservableMatrix<FieldCell>
        get() = mutableField

    val stateProperty = objectProperty(GameState.INIT)
    var state by stateProperty

    val minesLeftProperty: LongBinding = field.countBinding { it.checked }
        .longBinding { checked -> checked?.toLong()?.let { minesNumber - it } ?: minesNumber.toLong() }

    val minesLeft: Long by minesLeftProperty

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

        checkGameOver()
    }

    fun openUnchecked(point: Point) {
        val cell = field[point]
        if(cell.minesAroundNumber == field.aroundValues(point).count { it.checked }) {
            field.aroundValues(point).filter { !it.checked && !it.opened }.forEach {
                open(it.position)
            }
        }
        checkGameOver()
    }

    private fun checkWinner(): Boolean {
        return field.count { !it.mine && !it.opened } == 0L
    }

    private fun checkLooser(): Boolean {
        return field.count { it.opened && it.mine } > 0
    }

    private fun checkGameOver() {
        if (checkLooser()) {
            state = GameState.LOOSER
            return
        }

        if (checkWinner()) {
            state = GameState.WINNER
            return
        }
    }

    fun check(cell: FieldCell) {
        if(!cell.opened) {
            cell.checked = !cell.checked
        }
    }

}
