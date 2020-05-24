package ru.nobirds.water

import ru.nobirds.utils.Matrix
import ru.nobirds.utils.xIndices
import ru.nobirds.utils.yIndices

fun Matrix<GameFieldType>.asString(): String = buildString {
    yIndices.reversed().forEach { y ->
        append("$y| ")

        val line = xIndices.joinToString("") { x ->
            val value = get(x, y)
            if(value == GameFieldType.WALL) "|${value.symbol}" else " ${value.symbol}"
        }

        appendln(line)
    }
    append("   =")
    appendln(xIndices.joinToString("") { "==" })
}
