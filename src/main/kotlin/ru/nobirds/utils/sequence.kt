package ru.nobirds.utils

fun <T> Sequence<T>.takeWhileIncluding(condition: (T) -> Boolean): Sequence<T> = sequence {
    for (item in this@takeWhileIncluding) {
        yield(item)
        if(!condition(item))
            break
    }
}
