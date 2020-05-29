package ru.nobirds.minesweeper.fx

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.binding.Binding
import javafx.beans.binding.LongBinding
import javafx.beans.binding.NumberBinding
import javafx.beans.binding.ObjectBinding
import javafx.beans.value.ObservableValue
import ru.nobirds.utils.*
import tornadofx.longBinding
import tornadofx.objectBinding

data class MatrixChange<T>(val point: Point, val oldValue: T, val newValue: T)

interface Cancelable {
    fun cancel()
}

fun cancelable(block: () -> Unit): Cancelable = object : Cancelable {
    override fun cancel() = block()
}

interface ObservableMatrix<T: Observable> : Matrix<T>, Observable {

    fun addChangeListener(listener: (MatrixChange<T>) -> Unit): Cancelable

}

interface ObservableMutableMatrix<T: Observable> : MutableMatrix<T>, ObservableMatrix<T>

fun Observable.addInvalidationListener(listener: (Observable) -> Unit): Cancelable {
    val invalidationListener = InvalidationListener(listener)
    addListener(invalidationListener)
    return cancelable {
        removeListener(invalidationListener)
    }
}

fun Observable.support(vararg dependencies: Observable): ObservableSupport {
    return support(dependencies.toList())
}

fun Observable.support(dependencies: List<Observable>): ObservableSupport {
    return ObservableSupport(this, dependencies)
}

class ObservableSupport(private val observable: Observable,
                        private val dependencies: List<Observable> = emptyList()) : Observable, Cancelable {

    private val listeners = mutableListOf<InvalidationListener>()

    private val cancelables = dependencies.map {
        addInvalidationListener { invalidate() }
    }

    override fun removeListener(listener: InvalidationListener) {
        listeners.remove(listener)
    }

    override fun addListener(listener: InvalidationListener) {
        listeners.add(listener)
    }

    fun invalidate(observable: Observable = this.observable) {
        for (listener in listeners) {
            listener.invalidated(observable)
        }
    }

    override fun cancel() {
        for (cancelable in cancelables) {
            cancelable.cancel()
        }
    }
}

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
            observableSupport.invalidate()
            for (listener in listeners) {
                listener(MatrixChange(position, oldValue, value))
            }
        }
    }

}

fun <V, T: ObservableValue<V>, R> T.map(vararg dependencies: Observable, binding: (V) -> R): Binding<R?> =
    objectBinding(this, *dependencies) { it?.let { binding(it) } }

fun <T:Observable, R:Any> ObservableMatrix<T>.binding(binding: ObservableMatrix<T>.() -> R): ObjectBinding<R?> =
    objectBinding(this) { binding() }

fun <T:Observable> ObservableMatrix<T>.countBinding(condition: (T) -> Boolean): LongBinding =
    longBinding(this) { count(condition) }

fun <T: Observable> MutableMatrix<T>.asObservable(): ObservableMutableMatrix<T> = ObservableMatrixWrapper(this)


val <T> Matrix<T>.rows: Sequence<Sequence<T>>
    get() = sequence {
        xIndices.forEach { x ->
            yield(sequence {
                yIndices.forEach { y ->
                    yield(get(x, y))
                }
            })
        }
    }
