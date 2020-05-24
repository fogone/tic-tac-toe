package ru.nobirds.utils

interface MutableMatrix<T> : Matrix<T> {

    operator fun set(x: Int, y: Int, value: T)
}

operator fun <T> MutableMatrix<T>.set(point: Point, value: T) = set(point.x, point.y, value)
inline fun <reified T> mutableMatrixOf(sizeX: Int, sizeY: Int, factory: (Int, Int) -> T): MutableMatrix<T> =
    ArrayMatrix(Array(sizeX) { x -> Array(sizeY) { y -> factory(x, y) } })

inline fun <reified T> mutableMatrixOf(sizeY: Int, vararg values: T): MutableMatrix<T> =
    ArrayMatrix(values.toList().chunked(sizeY).map { it.toTypedArray() }
        .toTypedArray())