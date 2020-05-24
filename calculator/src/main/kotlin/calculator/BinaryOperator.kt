package ru.nobirds.calculator

import java.math.BigDecimal

interface BinaryOperator {

    val grouping: Boolean

    fun evaluate(left: BigDecimal, right: BigDecimal): BigDecimal

}

fun binaryOperator(symbol: String, grouping: Boolean, operator: (BigDecimal, BigDecimal) -> BigDecimal): BinaryOperator = object : BinaryOperator {
    override val grouping: Boolean = grouping
    override fun toString(): String = symbol
    override fun evaluate(left: BigDecimal, right: BigDecimal): BigDecimal = operator(left, right)
}

object BinaryOperators {

    val plus = binaryOperator("+", false) { first, second -> first + second }
    val minus = binaryOperator("-", false) { first, second -> first - second }
    val multiple = binaryOperator("*", true) { first, second -> first * second }
    val divide = binaryOperator("/", true) { first, second -> first / second }

    fun bySymbol(symbol: String): BinaryOperator {
        return when (symbol) {
            "+" -> plus
            "-" -> minus
            "*" -> multiple
            "/" -> divide
            else -> error("Unsupported operation")
        }
    }
}

interface UnaryOperator {

    fun evaluate(expression: BigDecimal): BigDecimal

}

fun unaryOperator(symbol: String, operator: (BigDecimal) -> BigDecimal): UnaryOperator = object : UnaryOperator {
    override fun toString(): String = symbol
    override fun evaluate(expression: BigDecimal): BigDecimal = operator(expression)
}

object UnaryOperators {

    val plus = unaryOperator("+") { it }
    val minus = unaryOperator("-") { -it }

    fun bySymbol(symbol: String): UnaryOperator = when (symbol) {
        "+" -> plus
        "-" -> minus
        else -> error("Unsupported operator $symbol")
    }
}
