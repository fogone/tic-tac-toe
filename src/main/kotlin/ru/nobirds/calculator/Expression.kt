package ru.nobirds.calculator

import java.math.BigDecimal

sealed class Expression
data class NumberExpression(val number: BigDecimal) : Expression()
data class BinaryOperatorExpression(val operator: BinaryOperator, val left: Expression, val right: Expression) : Expression()
data class UnaryOperatorExpression(val operator: UnaryOperator, val expression: Expression) : Expression()
