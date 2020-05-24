package ru.nobirds.utils

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow

class ArrayMatrix<T>(private val array: Array<Array<T>>) : MutableMatrix<T> {

    override val size: Point =
        Point(array.size, array[0].size)

    override fun get(x: Int, y: Int): T {
        checkInBounds(x, y)
        return array[x][y]
    }

    override fun set(x: Int, y: Int, value: T) {
        checkInBounds(x, y)
        array[x][y] = value
    }

    override fun toString(): String = formatToString()

}

data class MatrixChange<T>(val position: Point, val oldValue: T, val newValue: T)

interface ObservableMatrix<T> : MutableMatrix<T> {

    fun subscribe(): ReceiveChannel<MatrixChange<T>>

}

