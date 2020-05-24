package ru.nobirds.utils

class MatrixView<T>(private val matrix: Matrix<T>,
                    private val viewPosition: Point,
                    private val viewSize: Point
) : Matrix<T> {

    override val size: Point
        get() = viewSize

    override fun get(x: Int, y: Int): T {
        checkInBounds(x, y)
        return matrix[viewPosition.x + x, viewPosition.y + y]
    }

    override fun toString(): String = formatToString()
}

fun <T> Matrix<T>.window(position: Point, size: Point): Matrix<T> {
    return MatrixView(this, position, size)
}

inline fun <T, R> Matrix<T>.mapWindow(windowSize: Int, block: (Matrix<T>) -> R): List<R> {
    val result = mutableListOf<R>()
    forEachWindow(windowSize) {
        result.add(block(it))
    }
    return result
}

inline fun <T> Matrix<T>.forEachWindow(windowSize: Int, block: (Matrix<T>) -> Unit) {
    val windowSizePoint = Point(windowSize, windowSize)

    for (x in 0..size.x - windowSize) {
        for (y in 0..size.y - windowSize) {
            block(window(Point(x, y), windowSizePoint))
        }
    }
}