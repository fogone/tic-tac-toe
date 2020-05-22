package ru.nobirds.calculator

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class ParserTest {

    @Test
    fun tokensTest() {
        val tokensIterator = TokenStream("10*-(3+99)-33/9").tokens()

        val tokens = generateSequence { if(tokensIterator.hasNext) tokensIterator.forward() else null }.toList()

        assertThat(tokens, equalTo(listOf(
                NumberToken("10"),
                OperatorToken("*"),
                OperatorToken("-"),
                GroupToken(listOf(
                        NumberToken("3"),
                        OperatorToken("+"),
                        NumberToken("99")
                )),
                OperatorToken("-"),
                NumberToken("33"),
                OperatorToken("/"),
                NumberToken("9"))
        ))
    }

    @Test
    fun groupsTokensTest() {
        val tokens = TokenStream("10*(3+(99-8))-6").tokens().asSequence().toList()

        assertThat(tokens, equalTo(listOf(
                NumberToken("10"),
                OperatorToken("*"),
                GroupToken(listOf(
                        NumberToken("3"),
                        OperatorToken("+"),
                        GroupToken(listOf(
                                NumberToken("99"),
                                OperatorToken("-"),
                                NumberToken("8")
                        ))
                )),
                OperatorToken("-"),
                NumberToken("6")
        )))
    }

    @Test
    fun simpleExprTest() {
        testExpression("1+2*3") {
            operator(BinaryOperators.plus) {
                left {
                    number(1)
                }
                right {
                    operator(BinaryOperators.multiple) {
                        left {
                            number(2)
                        }
                        right {
                            number(3)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun expressionsTest() {
        testExpression("10*-(3+99)-33/9+1") {
            operator(BinaryOperators.plus) {
                left {
                    operator(BinaryOperators.minus) {
                        left {
                            operator(BinaryOperators.multiple) {
                                left {
                                    number(10)
                                }
                                right {
                                    unaryOperator(UnaryOperators.minus) {
                                        operator(BinaryOperators.plus) {
                                            left {
                                                number(3)
                                            }
                                            right {
                                                number(99)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        right {
                            operator(BinaryOperators.divide) {
                                left {
                                    number(33)
                                }
                                right {
                                    number(9)
                                }
                            }
                        }
                    }
                }
                right {
                    number(1)
                }
            }
        }
    }

    private fun testExpression(expression: String, expected: ExpressionBuilder.() -> Unit) {
        assertThat(ExpressionParser().parse(expression).evaluate(), equalTo(expression(expected).evaluate()))
    }

}
