package ru.nobirds.utils

data class Point(val x: Int, val y: Int)

infix fun Int.x(value: Int): Point =
    Point(this, value)

interface Matrix<out T> {

    val size: Point

    operator fun get(x: Int, y: Int): T
}

interface MutableMatrix<T> : Matrix<T> {

    operator fun set(x: Int, y: Int, value: T)
}

val <T> Matrix<T>.xIndices: IntRange get() = 0 until size.x
val <T> Matrix<T>.yIndices: IntRange get() = 0 until size.y

operator fun <T> Matrix<T>.get(point: Point): T = get(point.x, point.y)
operator fun <T> MutableMatrix<T>.set(point: Point, value: T) = set(point.x, point.y, value)

inline fun <reified T> mutableMatrixOf(sizeX: Int, sizeY: Int, factory: (Int, Int) -> T): MutableMatrix<T> =
    ArrayMatrix(Array(sizeX) { x -> Array(sizeY) { y -> factory(x, y) } })

inline fun <reified T> mutableMatrixOf(sizeY: Int, vararg values: T): MutableMatrix<T> =
    ArrayMatrix(values.toList().chunked(sizeY).map { it.toTypedArray() }
        .toTypedArray())

class ArrayMatrix<T>(private val array: Array<Array<T>>) : MutableMatrix<T> {

    override val size: Point =
        Point(array.size, array[0].size)

    override fun get(x: Int, y: Int): T {
        checkPositionInBounds(x, y)
        return array[x][y]
    }

    override fun set(x: Int, y: Int, value: T) {
        checkPositionInBounds(x, y)
        array[x][y] = value
    }

    override fun toString(): String = formatToString()

}

fun Matrix<*>.contains(x: Int, y: Int): Boolean = x >= 0 && y >= 0 && x < size.x && y < size.y

operator fun Matrix<*>.contains(point: Point): Boolean = contains(point.x, point.y)

fun Matrix<*>.checkPositionInBounds(x: Int, y: Int) {
    check(contains(x, y)) { "Coordinates [$x,$y] not in bounds $size" }
}

class MatrixView<T>(private val matrix: Matrix<T>,
                    private val viewPosition: Point,
                    private val viewSize: Point
) : Matrix<T> {

    override val size: Point
        get() = viewSize

    override fun get(x: Int, y: Int): T {
        checkPositionInBounds(x, y)
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

inline fun <T, reified R> Matrix<T>.map(transfer: (T) -> R): Matrix<R> =
    mutableMatrixOf(size.x, size.y) { x, y -> transfer(get(x, y)) }

fun Matrix<*>.rowPositions(y: Int): Sequence<Point> = sequence {
    for (xIndex in xIndices) {
        yield(Point(xIndex, y))
    }
}

fun Matrix<*>.columnPositions(x: Int): Sequence<Point> = sequence {
    for (yIndex in yIndices) {
        yield(Point(x, yIndex))
    }
}

fun Matrix<*>.topDownDiagonalPositions(x: Int = 0, y: Int = 0): Sequence<Point> = sequence {
    processByIterators(this@topDownDiagonalPositions, (x..size.x).iterator(), (y..size.y).iterator())
}

fun Matrix<*>.bottomUpDiagonalPositions(x: Int = 0, y: Int = size.y - 1): Sequence<Point> = sequence {
    processByIterators(this@bottomUpDiagonalPositions, (x..size.x).iterator(), (y downTo 0).iterator())
}

private suspend fun SequenceScope<Point>.processByIterators(
    matrix: Matrix<*>,
    xIterator: IntIterator,
    yIterator: IntIterator) {

    while (xIterator.hasNext() && yIterator.hasNext()) {
        val x = xIterator.nextInt()
        val y = yIterator.nextInt()

        if (matrix.contains(x, y))
            yield(Point(x, y))
        else break
    }
}


fun <T> Matrix<T>.formatToString(formatter: (T) -> String = { it.toString() }): String = buildString {
    appendln()
    for (y in yIndices) {
        append("|")
        for (x in xIndices) {
            append(formatter(get(x, y)))
            append("|")
        }
        appendln()
    }
}

fun <T> Matrix<T>.positions(): Sequence<Point> = sequence {
    for (xIndex in xIndices) {
        for (yIndex in yIndices) {
            yield(Point(xIndex, yIndex))
        }
    }
}
