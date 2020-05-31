package ru.nobirds.minesweeper.fx

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.binding.LongBinding
import javafx.beans.binding.ObjectBinding
import ru.nobirds.utils.*
import tornadofx.longBinding
import tornadofx.objectBinding

data class MatrixChange<T>(val point: Point, val oldValue: T, val newValue: T)

interface ObservableMatrix<T: Observable> : Matrix<T>, Observable {

    fun addChangeListener(listener: (MatrixChange<T>) -> Unit): Cancelable

}

interface ObservableMutableMatrix<T: Observable> : MutableMatrix<T>, ObservableMatrix<T>

class ObservableMatrixWrapper<T: Observable>(private val matrix: MutableMatrix<T>) : ObservableMutableMatrix<T>, Matrix<T> by matrix {

    private val observableSupport = support(matrix.values.toList())

    private val listeners = mutableListOf<(MatrixChange<T>) -> Unit>()

    override fun removeListener(listener: InvalidationListener) = observableSupport.removeListener(listener)
    override fun addListener(listener: InvalidationListener) = observableSupport.addListener(listener)

    override fun addChangeListener(listener: (MatrixChange<T>) -> Unit): Cancelable {
        listeners.add(listener)
        return cancelable {
            listeners.remove(listener)
        }
    }

    override fun set(x: Int, y: Int, value: T) {
        val position = x x y
        val oldValue = matrix[position]
        matrix[position] = value

        if (oldValue !== value) {
            observableSupport.fireValueChangedEvent()
            fireValueChangedEvent(MatrixChange(position, oldValue, value))
        }
    }

    private fun fireValueChangedEvent(matrixChange: MatrixChange<T>) {
        for (listener in listeners) {
            runCatchingInUncaught {
                listener(matrixChange)
            }
        }
    }

    override fun toString(): String = matrix.toString()

}

fun <T:Observable, R:Any> ObservableMatrix<T>.binding(binding: ObservableMatrix<T>.() -> R): ObjectBinding<R?> =
    objectBinding(this) { binding() }

fun <T:Observable> ObservableMatrix<T>.countBinding(condition: (T) -> Boolean): LongBinding =
    longBinding(this) { count(condition) }

fun <T: Observable> MutableMatrix<T>.asObservable(): ObservableMutableMatrix<T> = ObservableMatrixWrapper(this)

