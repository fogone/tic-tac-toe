package ru.nobirds.calculator

import java.math.BigDecimal

fun Expression.evaluate(): BigDecimal {
    return when (val e = this) {
        is BinaryOperatorExpression -> e.let {
            it.operator.evaluate(it.left.evaluate(), it.right.evaluate())
        }
        is UnaryOperatorExpression -> e.let {
            it.operator.evaluate(it.expression.evaluate())
        }
        is NumberExpression -> e.number
    }
}
