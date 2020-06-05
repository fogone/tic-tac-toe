package ru.nobirds.minesweeper.fx

import javafx.collections.ObservableList
import tornadofx.*
import java.sql.*

class GameScoreModel() : ViewModel() {

    private val repository = ScoreRecordRepository()

    val cellsNumberProperty = 0.toProperty()
    var cellsNumber by cellsNumberProperty

    val minesNumberProperty = 0.toProperty()
    var minesNumber by minesNumberProperty

    private val fullScore = findScoreRecords().apply {
        onChange { change ->
            while (change.next()) {
                if (change.wasAdded()) {
                    change.addedSubList.forEach {
                        repository.save(it)
                    }
                }
            }
        }
    }

    val score: ObservableList<ScoreRecord> = fullScore
        .filtered(minesNumberProperty, cellsNumberProperty) {
            (minesNumber == 0 || it.mines == minesNumber) &&
                    (cellsNumber == 0 || it.square == cellsNumber)
        }
        .sorted(compareByDescending<ScoreRecord> { it.square }.thenByDescending { it.mines }.thenBy { it.time })

    fun save(scoreRecord: ScoreRecord) {
        minesNumber = scoreRecord.mines
        cellsNumber = scoreRecord.square
        fullScore.add(scoreRecord)
    }

    private fun findScoreRecords(): ObservableList<ScoreRecord> {
        return observableListOf(repository.findAll())
    }

}

fun <T> ResultSet.toList(mapper: ResultSet.() -> T): List<T> {
    val result = mutableListOf<T>()
    while (next()) {
        result.add(mapper())
    }
    return result
}

fun Connection.execute(sql: String, handle: PreparedStatement.() -> Unit = {}): Int {
    prepareStatement(sql).use {
        handle(it)
        return it.executeUpdate()
    }
}

fun <R> Connection.executeQuery(sql: String, handle: ResultSet.() -> R): R {
    return prepareStatement(sql).use {
        it.executeQuery().use {
            handle(it)
        }
    }
}

fun <R> Connection.executeForList(sql: String, mapper: ResultSet.() -> R): List<R> = executeQuery(sql) { toList(mapper) }
