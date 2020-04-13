package ru.nobirds.tictactoe

interface WinnerAlgorithm {

    fun GameField.findWinner(inRow: Int): List<SearchResult>

}

class SimpleWinnerAlgorithm() : WinnerAlgorithm {
    override fun GameField.findWinner(inRow: Int): List<SearchResult> {
        return search(inRow, GameField::searchWinner)
    }
}

fun GameField.searchWinner(inRow: Int, positions: Sequence<Point>): List<SearchResult> {
    val currentPositions = mutableListOf<Point>()
    var currentCellType = CellType.EMPTY

    val result = mutableListOf<SearchResult>()

    for (position in positions) {
        val cellType = get(position)

        if (cellType != currentCellType) {
            if(currentPositions.size >= inRow && currentCellType.isNotEmpty)
                result.add(SearchResult(currentCellType, GameRange(positions.toList())))
            currentCellType = cellType
            currentPositions.clear()
        }

        currentPositions.add(position)
    }

    if(currentPositions.size >= inRow && currentCellType.isNotEmpty)
        result.add(SearchResult(currentCellType, GameRange(currentPositions)))

    return result
}

