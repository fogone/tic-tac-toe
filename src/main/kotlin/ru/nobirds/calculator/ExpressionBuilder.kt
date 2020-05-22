package ru.nobirds.calculator

import java.math.BigDecimal

class BinaryOperatorBuilder(private val operator: BinaryOperator) {

    private var left: Expression? = null
    private var right: Expression? = null

    fun left(builder: ExpressionBuilder.() -> Unit) {
        this.left = expression(builder)
    }

    fun right(builder: ExpressionBuilder.() -> Unit) {
        this.right = expression(builder)
    }

    fun build(): BinaryOperatorExpression =
            BinaryOperatorExpression(operator, requireNotNull(left), requireNotNull(right))

}

class ExpressionBuilder {

    private var expression: Expression? = null

    fun operator(operator: BinaryOperator, builder: BinaryOperatorBuilder.() -> Unit) {
        this.expression = BinaryOperatorBuilder(operator).apply(builder).build()
    }

    fun number(number: Int) {
        number(number.toBigDecimal())
    }

    fun number(number: BigDecimal) {
        this.expression = NumberExpression(number)
    }

    fun unaryOperator(operator: UnaryOperator, builder: ExpressionBuilder.() -> Unit) {
        this.expression = UnaryOperatorExpression(operator, expression(builder))
    }

    fun build(): Expression {
        return requireNotNull(expression)
    }

}

fun expression(builder: ExpressionBuilder.() -> Unit): Expression {
    return ExpressionBuilder().apply(builder).build()
}
