package ru.nobirds.tictactoe

import ru.nobirds.tictactoe.ui.GameWindow
import ru.nobirds.utils.mutableMatrixOf

fun main() {
    GameWindow().isVisible = true
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
