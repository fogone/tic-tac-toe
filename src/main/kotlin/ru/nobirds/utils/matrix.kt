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

inline fun <T> Matrix<T>.forEach(block: (T) -> Unit) {
    forEachIndexed { _, _, it -> block(it) }
}

inline fun <T> Matrix<T>.forEachIndexed(block: (Int, Int, T) -> Unit) {
    xIndices.forEach { x ->
        yIndices.forEach { y ->
            block(x, y, get(x, y))
        }
    }
}

inline fun <T, reified R> Matrix<T>.map(transfer: (T) -> R): Matrix<R> =
    mapIndexed { _, _, it -> transfer(it) }

inline fun <T, reified R> Matrix<T>.mapIndexed(transfer: (Int, Int, T) -> R): Matrix<R> =
        mutableMatrixOf(size.x, size.y) { x, y -> transfer(x, y, get(x, y)) }

inline fun <T> Matrix<T>.count(condition: (T) -> Boolean): Long = sumBy { if(condition(it)) 1 else 0 }

inline fun <T> Matrix<T>.sumBy(accessor: (T) -> Int): Long {
    var result = 0L
    forEach {
        result += accessor(it)
    }
    return result
}

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

fun <T> MutableMatrix<T>.setEach(provider: (Int, Int) -> T) {
    forEachIndexed { x, y, _ ->
        set(x, y, provider(x, y))
    }
}

fun <T> Matrix<T>.filter(condition: (T) -> Boolean): List<T> {
    val result = mutableListOf<T>()

    forEach {
        if(condition(it))
            result.add(it)
    }

    return result
}

fun <T> Matrix<T>.around(point: Point): List<Point> = listOf(
        -1 x -1, -1 x 0, -1 x 1,
        0 x -1,          0 x 1,
        1 x -1,  1 x 0,  1 x 1
).map { point + it }.filter { it in this }

fun <T> Matrix<T>.aroundValues(point: Point): List<T> = around(point).map { get(it) }
