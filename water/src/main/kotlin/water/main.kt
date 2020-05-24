package ru.nobirds.water

import ru.nobirds.utils.*
import kotlin.random.Random

enum class GameFieldType(val symbol: String) {
    WALL("X"), WATER("~"), NONE(" ")
}

private fun List<Int>.createMatrix(): Matrix<GameFieldType> {
    val width = size
    val height = max()!!

    return mutableMatrixOf(width, height) { x, y ->
        if (y > get(x)) GameFieldType.NONE else GameFieldType.WALL
    }
}

fun createRandomGameField(size: Int = 5): List<Int> {
    return (0..size).map { Random.nextInt(0, size) }
}

fun main() {
    val field = createRandomGameField(15)
    val matrix = field.createMatrix()

    val withWater = matrix.mapIndexed { x, y, value ->
        when {
            value == GameFieldType.WALL -> GameFieldType.WALL
            matrix.isWater(x, y) -> GameFieldType.WATER
            else -> GameFieldType.NONE
        }
    }

    println(withWater.asString())
    println(withWater.count { it == GameFieldType.WATER })

}

private fun Matrix<GameFieldType>.isWater(x: Int, y: Int): Boolean {
    return (x downTo 0).map { get(it, y) }.any { it == GameFieldType.WALL } &&
            (x until size.x).map { get(it, y) }.any { it == GameFieldType.WALL }
}
