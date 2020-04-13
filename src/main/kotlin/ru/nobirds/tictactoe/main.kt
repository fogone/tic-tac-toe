package ru.nobirds.tictactoe

fun main() {
    val game = createDefaultGame(5, 4)
    val winner = game.run()
    println("Winner: ${game.inRow}/${game.field.size.x} in row $winner")
}

fun createDefaultGame(fieldSize: Int, inRow: Int,
                      playerFactory: (CellType) -> Player = { createDefaultAiPlayer(it) }): TicTacToeGame =
    TicTacToeGame(
        mutableMatrixOf(fieldSize, fieldSize) { _, _ -> CellType.EMPTY },
        inRow,
        playerFactory(CellType.CROSS),
        playerFactory(CellType.ZERO),
        SimpleWinnerAlgorithm()
    )

fun createDefaultAiPlayer(cellType: CellType): AiPlayer {
    return AiPlayer(
        cellType,
        NaiveAlertSearchStrategy,
        FirstChooseAlertStrategy,
        RandomPreventAlertStrategy,
        RandomAttackStrategy
    )
}