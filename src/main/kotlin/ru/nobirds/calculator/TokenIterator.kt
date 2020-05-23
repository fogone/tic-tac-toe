package ru.nobirds.calculator

interface TokenIterator {

    val current: Token?
    val next: Token?
    val prev: Token?

    fun reset()
    fun forward(): Token?
    fun backward(): Token?

}

val TokenIterator.hasNext: Boolean get() = next != null
val TokenIterator.hasPrev: Boolean get() = prev != null

fun TokenIterator.asSequence(): Sequence<Token> = generateSequence { forward() }

class TokenIteratorImpl(private val tokens: List<Token>) : TokenIterator {

    private var currentPosition = -1

    override val current: Token?
        get() = get(currentPosition)

    override val next: Token?
        get() = get(currentPosition + 1)

    override val prev: Token?
        get() = get(currentPosition - 1)


    override fun reset() {
        currentPosition = -1
    }

    override fun forward(): Token? {
        if (contains(currentPosition + 1)) {
            currentPosition++
        }
        return current
    }

    override fun backward(): Token? {
        if (contains(currentPosition - 1)) {
            currentPosition--
        }
        return current
    }

    private fun get(index: Int): Token? {
        return if(contains(index)) tokens[index] else null
    }

    private fun contains(index: Int): Boolean {
        return index in tokens.indices
    }

}

fun List<Token>.asTokenIterator(): TokenIterator {
    return TokenIteratorImpl(this)
}
