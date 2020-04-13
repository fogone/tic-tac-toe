package ru.nobirds.tictactoe

import ru.nobirds.utils.Point
import ru.nobirds.utils.formatToString
import ru.nobirds.utils.get
import ru.nobirds.utils.positions
import kotlin.random.Random

interface Player {

    val cellType: CellType

    fun GameField.nextTurn(inRow: Int): Point

}

class StdInputPlayer(override val cellType: CellType) : Player {

    override fun GameField.nextTurn(inRow: Int): Point {
        println("Current game field state: ${formatToString()}")
        println("Enter coordinates for your next turn:")
        val parts = readLine()?.split(" ")?.takeIf { it.size == 2 } ?: return Point(
            -1,
            -1
        )
        val (x, y) = parts.map { it.toIntOrNull() }.filterNotNull().takeIf { it.size == 2 } ?: return Point(
            -1,
            -1
        )
        return Point(x, y)
    }

}

typealias Alert = GameRange

interface AlertSearchStrategy {

    fun GameField.findAlerts(inRow: Int, cellType: CellType): List<Alert>

}

object NaiveAlertSearchStrategy : AlertSearchStrategy {
    override fun GameField.findAlerts(inRow: Int, cellType: CellType): List<Alert> {
        return search(inRow, GameField::searchAlerts).filter { it.cellType == cellType }.map { it.range }
    }
}

fun GameField.searchAlerts(inRow: Int, positions: Sequence<Point>): List<SearchResult> {
    val currentPositions = mutableListOf<Point>()
    var currentCellType = CellType.EMPTY

    val result = mutableListOf<SearchResult>()

    for (position in positions) {
        val cellType = get(position)

        if (cellType.isNotEmpty && cellType != currentCellType) {
            if(currentCellType.isNotEmpty)
                currentPositions.clear()
            currentCellType = cellType
        }

        currentPositions.add(position)

        if (currentPositions.size == inRow) {
            CellType.values().filter { it.isNotEmpty }.forEach {
                if(isAlert(it, inRow, currentPositions))
                    result.add(SearchResult(it, GameRange(currentPositions.toList())))
            }
            currentPositions.removeAt(0)
        }
    }

    return result
}

fun GameField.isAlert(cellType: CellType, inRow: Int, positions: List<Point>): Boolean {
    return positions.map { get(it) }.filter { it == cellType }.count() > (inRow/3)
}

interface ChooseAlertStrategy {

    fun GameField.chooseAlert(inRow: Int, cellType: CellType, alerts: List<Alert>): Alert?

}

object FirstChooseAlertStrategy : ChooseAlertStrategy {
    override fun GameField.chooseAlert(inRow: Int, cellType: CellType, alerts: List<Alert>): Alert? =
        alerts.maxBy { it.positions.filter { get(it) == cellType }.size }
}

interface PreventAlertStrategy {

    fun GameField.prevent(inRow: Int, cellType: CellType, alert: Alert): Point

}

object RandomPreventAlertStrategy : PreventAlertStrategy {
    override fun GameField.prevent(inRow: Int, cellType: CellType, alert: Alert): Point {
        val emptyCells = alert.positions.filter { get(it) == CellType.EMPTY }
        return emptyCells[Random.nextInt(emptyCells.size)]
    }
}

interface AttackStrategy {

    fun GameField.attack(inRow: Int, cellType: CellType): Point

}

object RandomAttackStrategy : AttackStrategy {

    override fun GameField.attack(inRow: Int, cellType: CellType): Point {
        val myAlerts = with(NaiveAlertSearchStrategy) { findAlerts(inRow, cellType) }
        val emptyPositions = if (myAlerts.isNotEmpty()) {
            val alert = myAlerts.maxBy { it.positions.map { get(it) }.filterNot { it.isEmpty }.size }!!
            alert.positions.filter { get(it).isEmpty }
        } else {
            positions().filter { get(it).isEmpty }.toList()
        }

        return emptyPositions[Random.nextInt(emptyPositions.size)]
    }
}

class AiPlayer(override val cellType: CellType,
               private val alertSearchStrategy: AlertSearchStrategy,
               private val chooseAlertStrategy: ChooseAlertStrategy,
               private val preventAlertStrategy: PreventAlertStrategy,
               private val attackStrategy: AttackStrategy) : Player {

    override fun GameField.nextTurn(inRow: Int): Point {
        val alerts = with(alertSearchStrategy) { findAlerts(inRow, cellType.inverse()) }
        val alert = with(chooseAlertStrategy) { chooseAlert(inRow, cellType.inverse(), alerts) }
        return if (alert != null) {
            with(preventAlertStrategy) { prevent(inRow, cellType, alert) }
        } else {
            with(attackStrategy) { attack(inRow, cellType) }
        }
    }

}
