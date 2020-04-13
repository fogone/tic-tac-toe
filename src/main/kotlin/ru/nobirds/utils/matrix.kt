package ru.nobirds.utils

interface Matrix<out T> {

    val size: Point

    operator fun get(x: Int, y: Int): T
}

val <T> Matrix<T>.xIndices: IntRange get() = 0 until size.x
val <T> Matrix<T>.yIndices: IntRange get() = 0 until size.y

operator fun <T> Matrix<T>.get(point: Point): T = get(point.x, point.y)

fun Matrix<*>.contains(x: Int, y: Int): Boolean = x >= 0 && y >= 0 && x < size.x && y < size.y

operator fun Matrix<*>.contains(point: Point): Boolean = contains(point.x, point.y)

fun Matrix<*>.checkInBounds(x: Int, y: Int) {
    check(contains(x, y)) { "Coordinates [$x,$y] not in bounds $size" }
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
