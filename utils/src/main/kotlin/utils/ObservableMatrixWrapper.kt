package ru.nobirds.utils

data class MatrixChange<T>(val point: Point, val oldValue: T, val newValue: T)

interface ObservableMatrix<T> : MutableMatrix<T> {

    fun addChangeListener(listener: (MatrixChange<T>) -> Unit)

}

class ObservableMatrixWrapper<T>(private val matrix: MutableMatrix<T>) : ObservableMatrix<T>, Matrix<T> by matrix {

    private val listeners = mutableListOf<(MatrixChange<T>) -> Unit>()

    override fun addChangeListener(listener: (MatrixChange<T>) -> Unit) {
        this.listeners.add(listener)
    }

    override fun set(x: Int, y: Int, value: T) {
        val position = x x y
        val oldValue = matrix[position]
        matrix[position] = value
        if (oldValue !== value) {
            for (listener in listeners) {
                listener(MatrixChange(position, oldValue, value))
            }
        }
    }

}

fun <T> MutableMatrix<T>.asObservable(): ObservableMatrix<T> = ObservableMatrixWrapper(this)
