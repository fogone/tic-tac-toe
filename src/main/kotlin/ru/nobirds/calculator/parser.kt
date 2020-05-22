package ru.nobirds.calculator

class ExpressionParser() {

    fun parse(text: String): Expression {
        val tokens = text.tokens()

        require(tokens.hasNext) { "Expression is empty" }

        return tokens.fetchNextExpression()
    }

}

private fun TokenIterator.fetchNextExpression(): Expression {
    rollToHiPriorityOperator()
    if(current == null) reset()
    return NumberExpression(0.toBigDecimal())
}

private fun TokenIterator.rollToHiPriorityOperator() {
    while (forward() != null &&
            current?.let { it is OperatorToken && it.operator in setOf("*", "/") } != true) {
        // do nothing
    }
}

private fun invalidSyntax(message: String): Nothing = throw InvalidSyntaxException(message)

class InvalidSyntaxException(message: String) : Exception(message)

//fun TokenIterator.fetchNextExpression(): Expression {
//    val left = when (val token = forward()) {
//        is GroupToken -> {
//            token.tokens.asTokenIterator().fetchNextExpression()
//        }
//        is NumberToken -> {
//            NumberExpression(token.number.toBigDecimal())
//        }
//        is OperatorToken -> {
//            val operator = UnaryOperators.bySymbol(token.operator)
//            UnaryOperatorExpression(operator, fetchNextExpression())
//        }
//        is UnsupportedToken -> invalidSyntax("Unsupported token: ${token.token}")
//        null -> invalidSyntax("Ends")
//    }
//
//    return fetchBinaryOperator(left)
//}
//
//fun TokenIterator.fetchBinaryOperator(left: Expression): Expression {
//    return if (hasNext) {
//        when (val nextToken = forward()) {
//            is OperatorToken -> {
//                val operator = BinaryOperators.bySymbol(nextToken.operator)
//                val right = fetchNextExpression()
//                BinaryOperatorExpression(operator, left, right)
//            }
//            else -> invalidSyntax("Expected operator, but $nextToken found")
//        }
//    } else {
//        left
//    }
//}

