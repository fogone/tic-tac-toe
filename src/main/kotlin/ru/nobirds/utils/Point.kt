package ru.nobirds.utils

data class Point(val x: Int, val y: Int)

infix fun Int.x(value: Int): Point =
    Point(this, value)