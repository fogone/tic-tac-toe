package ru.nobirds.minesweeper

import ru.nobirds.utils.*

data class FieldCell(var mine: Boolean = false,
                     var minesAroundNumber: Int = 0,
                     var opened: Boolean = false,
                     var checked: Boolean = false)

val FieldCell.empty: Boolean get() = !mine && minesAroundNumber == 0

class FieldModel(val width: Int, val height: Int, val minesNumber: Int) {

    private val mutableField = mutableMatrixOf(width, height) { _, _ ->
        FieldCell()
    }.apply {
        setMines()
        updateMineNumbers()
    }

    val field: Matrix<FieldCell>
        get() = mutableField

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
        return around(point).map { get(it) }.count { it.mine }
    }

    fun open(point: Point) {
        val fieldCell = field[point]

        if(fieldCell.opened) return

        fieldCell.opened = true

        if(fieldCell.empty) {
            for (point in field.around(point).filter { !field[it].opened }) {
                open(point)
            }
        }
    }

    fun openUnchecked(point: Point) {
        val cell = field[point]
        if(cell.minesAroundNumber == field.aroundValues(point).count { it.checked }) {
            field.around(point).filter { field[it].let { !it.checked && !it.opened } }.forEach {
                open(it)
            }
        }
    }

    fun checkWinner(): Boolean {
        return field.count { !it.mine && !it.opened } == 0L
    }

    fun checkLooser(): Boolean {
        return field.count { it.opened && it.mine } > 0
    }

}


