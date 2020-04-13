package ru.nobirds.tictactoe

typealias GameField = Matrix<CellType>
typealias MutableGameField = MutableMatrix<CellType>

typealias SearchAlgorithm = GameField.(Int, Sequence<Point>) -> List<SearchResult>

fun GameField.search(inRow: Int, algorithm: SearchAlgorithm): List<SearchResult> {
    return searchLinear(inRow, algorithm) + searchDiagonals(inRow, algorithm)
}

fun GameField.searchLinear(inRow: Int, algorithm: SearchAlgorithm): List<SearchResult> {
    return xIndices.flatMap { xIndex -> searchVertical(xIndex, inRow, algorithm) } +
        yIndices.flatMap { yIndex -> searchHorizontal(yIndex, inRow, algorithm) }
}

fun GameField.searchDiagonals(inRow: Int, algorithm: SearchAlgorithm): List<SearchResult> {
    val results1 = (0..size.x - inRow).flatMap { x ->
        searchDiagonalTopDown(inRow, x, algorithm = algorithm) +
                searchDiagonalBottomUp(inRow, x, y = size.y - 1, algorithm = algorithm)
    }

    val results2 = (1..size.y - inRow).flatMap { y ->
        searchDiagonalTopDown(inRow, y = y, algorithm = algorithm) +
                searchDiagonalBottomUp(inRow, y = size.y - 1 - y, algorithm = algorithm)
    }

    return results1 + results2
}

fun GameField.searchVertical(x: Int, inRow: Int, algorithm: SearchAlgorithm): List<SearchResult> =
    algorithm(inRow, columnPositions(x))

fun GameField.searchHorizontal(y: Int, inRow: Int, algorithm: SearchAlgorithm): List<SearchResult> =
    algorithm(inRow, rowPositions(y))

fun GameField.searchDiagonalTopDown(inRow: Int, x: Int = 0, y: Int = 0, algorithm: SearchAlgorithm): List<SearchResult> =
    algorithm(inRow, topDownDiagonalPositions(x, y))

fun GameField.searchDiagonalBottomUp(inRow: Int, x: Int = 0, y: Int = size.y - 1, algorithm: SearchAlgorithm): List<SearchResult> =
    algorithm(inRow, bottomUpDiagonalPositions(x, y))

