package ru.nobirds.minesweeper.fx

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.binding.Binding
import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.paint.Color
import tornadofx.*

interface Cancelable {
    fun cancel()
}

fun cancelable(block: () -> Unit): Cancelable = object :
    Cancelable {
    override fun cancel() = block()
}

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
                        private val dependencies: List<Observable> = emptyList()) : Observable,
    Cancelable {

    private val listeners = mutableListOf<InvalidationListener>()

    private val cancelables = dependencies.map {
        it.addInvalidationListener { fireValueChangedEvent() }
    }

    override fun removeListener(listener: InvalidationListener) {
        listeners.remove(listener)
    }

    override fun addListener(listener: InvalidationListener) {
        listeners.add(listener)
    }

    fun fireValueChangedEvent() {
        for (listener in listeners) {
            runCatchingInUncaught {
                listener.invalidated(observable)
            }
        }
    }

    override fun cancel() {
        for (cancelable in cancelables) {
            cancelable.cancel()
        }
    }
}

inline fun runCatchingInUncaught(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e)
    }
}

fun <V, T: ObservableValue<V>, R> T.map(vararg dependencies: Observable, binding: (V) -> R): Binding<R?> =
    objectBinding(this, *dependencies) { it?.let { binding(it) } }

inline fun <T, R, P: ObservableValue<T>> P.flatMap(vararg dependencies: Observable,
                                                   crossinline transform: T.() -> ObservableValue<R?>): Binding<R?> {

    val bindingProperty = objectProperty<R>()

    value?.let { currentValue ->
        bindingProperty.bind(currentValue.transform())
    }

    onChange {
        bindingProperty.unbind()
        if(it != null)
            bindingProperty.bind(it.transform())
    }

    return bindingProperty.objectBinding(*dependencies) { it }
}

inline fun <T:Any> ObservableValue<T?>.onNonNullChange(crossinline listener: (T) -> Unit) {
    onChange { if(it != null) listener(it) }
}

inline fun <T> ObservableValue<T>.onState(state: T, crossinline listener: () -> Unit) {
    onChange { if(it == state) listener() }
}

inline fun ObservableValue<Boolean>.onState(crossinline listener: () -> Unit) {
    onState(true, listener)
}

fun Double.toProperty(builder: DoubleProperty.() -> Unit): DoubleProperty = SimpleDoubleProperty(this).apply(builder)
fun Float.toProperty(builder: FloatProperty.() -> Unit): FloatProperty = SimpleFloatProperty(this).apply(builder)
fun Long.toProperty(builder: LongProperty.() -> Unit): LongProperty = SimpleLongProperty(this).apply(builder)
fun Int.toProperty(builder: IntegerProperty.() -> Unit): IntegerProperty = SimpleIntegerProperty(this).apply(builder)
fun Boolean.toProperty(builder: BooleanProperty.() -> Unit): BooleanProperty = SimpleBooleanProperty(this).apply(builder)
fun String.toProperty(builder: StringProperty.() -> Unit): StringProperty = SimpleStringProperty(this).apply(builder)
fun <T> T.toProperty(builder: ObjectProperty<T>.() -> Unit): ObjectProperty<T> = SimpleObjectProperty(this).apply(builder)

fun color(r: Int, g: Int, b: Int): Color = Color.rgb(r, g, b)

fun Long.toTime(): String {
    val minutes = this / 60
    val seconds = this % 60
    val prefix = if(seconds < 10) "0" else ""
    return "$minutes:$prefix$seconds"
}

private fun <T, R> ObservableList<T>.createBoundList(vararg dependencies: Observable, conversion: (T) -> R): ObservableList<R> {
    val result = observableListOf<R> { dependencies as Array<Observable> }
    result.bind(this, conversion)
    return result
}

fun <T> ObservableList<T>.filtered(vararg dependencies: Observable, condition: (T) -> Boolean): ObservableList<T> {
    return createBoundList(*dependencies) { it }.filtered(condition)
}

fun <T, R> ObservableList<T>.mapped(vararg dependencies: Observable, transform: (T) -> R): ObservableList<R> {
    return createBoundList(*dependencies, conversion = transform)
}

fun <T> ObservableList<T>.sorted(vararg dependencies: Observable, comparator: Comparator<T>): ObservableList<T> {
    return createBoundList(*dependencies) { it }.sorted(comparator)
}

