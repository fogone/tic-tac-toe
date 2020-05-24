package ru.nobirds.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing

class ObservableMatrixWrapper<T>(private val matrix: MutableMatrix<T>) : ObservableMatrix<T> {

    private val changes = BroadcastChannel<MatrixChange<T>>(10)

    override fun set(x: Int, y: Int, value: T) {
        val oldValue = matrix[x, y]
        matrix[x, y] = value

        GlobalScope.launch(Dispatchers.Swing) {
            changes.send(MatrixChange(Point(x, y), oldValue, value))
        }
    }

    override val size: Point
        get() = matrix.size

    override fun get(x: Int, y: Int): T {
        return matrix[x, y]
    }

    override fun subscribe(): ReceiveChannel<MatrixChange<T>> {
        return changes.openSubscription()
    }
}
