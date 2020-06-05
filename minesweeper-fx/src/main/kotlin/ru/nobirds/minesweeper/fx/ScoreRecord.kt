package ru.nobirds.minesweeper.fx

import org.sqlite.SQLiteConfig
import org.sqlite.SQLiteDataSource
import org.sqlite.SQLiteOpenMode
import tornadofx.*

fun scoreRecord(name: String, time: Int, square: Int, mines: Int): ScoreRecord = ScoreRecord()
    .apply {
    this.name = name
    this.time = time
    this.square = square
    this.mines = mines
}

class ScoreRecord {
    val nameProperty = "".toProperty()
    var name by nameProperty

    val timeProperty = Int.MAX_VALUE.toProperty()
    var time by timeProperty

    val squareProperty = 0.toProperty()
    var square by squareProperty

    val minesProperty = 0.toProperty()
    var mines by minesProperty
}

class ScoreRecordRepository() {

    private val dataSource =
        SQLiteDataSource(SQLiteConfig().apply {
            setOpenMode(SQLiteOpenMode.EXCLUSIVE)
        }).apply {
        url = "jdbc:sqlite:score.db"
    }

    private val connection get() = dataSource.connection

    init {
        connection
            .execute("""
                create table if not exists score(
                    user text not null, 
                    time integer not null, 
                    square integer not null, 
                    mines integer not null
                );
            """.trimIndent())
    }

    fun findAll(): List<ScoreRecord> = connection
        .executeForList("select distinct user, time, square, mines from score") {
            scoreRecord(
                getString("user"),
                getInt("time"),
                getInt("square"),
                getInt("mines")
            )
        }

    fun save(scoreRecord: ScoreRecord) {
        connection.execute("insert into score(user, time, square, mines) values(?, ?, ?, ?)") {
            setString(1, scoreRecord.name)
            setInt(2, scoreRecord.time)
            setInt(3, scoreRecord.square)
            setInt(4, scoreRecord.mines)
        }
    }

}
