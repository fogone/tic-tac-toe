package ru.nobirds.calculator

sealed class Token

data class GroupToken(val tokens: List<Token>) : Token() {
    override fun toString(): String = "(${tokens.joinToString("")})"
}

data class OperatorToken(val operator: String) : Token() {
    override fun toString(): String = operator
}

data class NumberToken(val number: String) : Token() {
    override fun toString(): String = number
}

data class UnsupportedToken(val token: String) : Token() {
    override fun toString(): String = token
}

fun String.tokens(): TokenIterator {
    return iterator().process().toList().asTokenIterator()
}

private fun CharIterator.process(
        condition: CharIterator.(Char) -> Boolean = { false }): Sequence<Token> = sequence {

    val numberToken = StringBuilder()

    while (hasNext()) {
        val ch = next()

        if (condition(ch)) {
            yieldNumberToken(numberToken)
            break
        }

        when (ch) {
            '*', '/', '+', '-' -> {
                yieldNumberToken(numberToken)
                yield(OperatorToken(ch.toString()))
            }
            '(' -> {
                yieldNumberToken(numberToken)
                yield(GroupToken(process { it == ')' }.toList()))
            }
            ' ' -> {
                yieldNumberToken(numberToken)
            }
            in '0'..'9' -> {
                numberToken.append(ch)
            }
            else -> {
                yieldNumberToken(numberToken)
                yield(UnsupportedToken(ch.toString()))
            }
        }
    }

    yieldNumberToken(numberToken)
}

private suspend fun SequenceScope<Token>.yieldNumberToken(numberToken: StringBuilder) {
    if (numberToken.isNotEmpty()) {
        yield(NumberToken(numberToken.toString()))
        numberToken.clear()
    }
}
