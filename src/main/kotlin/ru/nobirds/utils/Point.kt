package ru.nobirds.utils

import kotlin.random.Random

data class Point(val x: Int, val y: Int)

infix fun Int.x(value: Int): Point =
        Point(this, value)

fun Point.random(): Point = Random.nextInt(x) x Random.nextInt(y)

operator fun Point.unaryMinus(): Point = Point(-x, -y)
operator fun Point.plus(other: Point): Point = Point(x + other.x, y + other.y)
operator fun Point.minus(other: Point): Point = this + (-other)
