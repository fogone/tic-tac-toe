package ru.nobirds.tictactoe

import ru.nobirds.utils.Point

enum class CellType(val symbol: String) {
    CROSS("X"),
    ZERO("O"),
    EMPTY("-");

    override fun toString(): String = symbol
}

val CellType.isEmpty: Boolean get() = this == CellType.EMPTY
val CellType.isNotEmpty: Boolean get() = !isEmpty


data class SearchResult(val cellType: CellType, val range: GameRange)
data class GameRange(val positions: List<Point>)

val SearchResult.isEmpty: Boolean get() = range.positions.isEmpty()

fun CellType.inverse(): CellType = when (this) {
    CellType.ZERO -> CellType.CROSS
    CellType.CROSS -> CellType.ZERO
    CellType.EMPTY -> CellType.EMPTY
}
